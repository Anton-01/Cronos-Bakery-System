package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.request.CreateRawMaterialRequest;
import com.cronos.bakery.application.dto.response.PriceHistoryResponse;
import com.cronos.bakery.application.dto.response.RawMaterialResponse;
import com.cronos.bakery.application.service.enums.StockOperation;
import com.cronos.bakery.domain.entity.core.*;
import com.cronos.bakery.domain.service.PriceChangeNotificationService;
import com.cronos.bakery.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final CategoryRepository categoryRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final AllergenRepository allergenRepository;
    private final UserRepository userRepository;
    private final MaterialPriceHistoryRepository priceHistoryRepository;
    private final PriceChangeNotificationService priceChangeNotificationService;

    /**
     * Creates a new raw material
     */
    @Transactional
    @CacheEvict(value = "materials", allEntries = true)
    public RawMaterialResponse createRawMaterial(CreateRawMaterialRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        MeasurementUnit purchaseUnit = measurementUnitRepository.findById(request.getPurchaseUnitId())
                .orElseThrow(() -> new RuntimeException("Purchase unit not found"));

        RawMaterial material = RawMaterial.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .supplier(request.getSupplier())
                .category(category)
                .purchaseUnit(purchaseUnit)
                .purchaseQuantity(request.getPurchaseQuantity())
                .unitCost(request.getUnitCost())
                .currency(request.getCurrency() != null ? request.getCurrency() : user.getDefaultCurrency())
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : BigDecimal.ZERO)
                .minimumStock(request.getMinimumStock())
                .lastPurchaseDate(LocalDateTime.now())
                .lastPriceUpdate(LocalDateTime.now())
                .build();

        // Add allergens
        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            Set<Allergen> allergens = new HashSet<>();
            for (Long allergenId : request.getAllergenIds()) {
                Allergen allergen = allergenRepository.findById(allergenId)
                        .orElseThrow(() -> new RuntimeException("Allergen not found: " + allergenId));
                allergens.add(allergen);
            }
            material.setAllergens(allergens);
        }

        material = rawMaterialRepository.save(material);

        log.info("Raw material created: {} by user: {}", material.getName(), username);

        return mapToResponse(material);
    }

    /**
     * Updates a raw material
     */
    @Transactional
    @CacheEvict(value = "materials", key = "#materialId")
    public RawMaterialResponse updateRawMaterial(Long materialId, CreateRawMaterialRequest request,
                                                 String username) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        validateMaterialOwnership(material, username);

        // Check if price changed
        boolean priceChanged = !material.getUnitCost().equals(request.getUnitCost());
        BigDecimal oldPrice = material.getUnitCost();

        // Update basic fields
        material.setName(request.getName());
        material.setDescription(request.getDescription());
        material.setBrand(request.getBrand());
        material.setSupplier(request.getSupplier());
        material.setPurchaseQuantity(request.getPurchaseQuantity());
        material.setUnitCost(request.getUnitCost());
        material.setMinimumStock(request.getMinimumStock());

        // Update category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            material.setCategory(category);
        }

        // Update allergens
        if (request.getAllergenIds() != null) {
            material.getAllergens().clear();
            for (Long allergenId : request.getAllergenIds()) {
                Allergen allergen = allergenRepository.findById(allergenId)
                        .orElseThrow(() -> new RuntimeException("Allergen not found"));
                material.getAllergens().add(allergen);
            }
        }

        material = rawMaterialRepository.save(material);

        // Handle price change
        if (priceChanged) {
            priceChangeNotificationService.handlePriceChange(
                    material,
                    request.getUnitCost(),
                    "Manual update by user"
            );
        }

        log.info("Raw material updated: {}", material.getName());

        return mapToResponse(material);
    }

    /**
     * Updates material stock
     */
    @Transactional
    @CacheEvict(value = "materials", key = "#materialId")
    public RawMaterialResponse updateStock(Long materialId, BigDecimal quantity,
                                           StockOperation operation, String username) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        validateMaterialOwnership(material, username);

        BigDecimal newStock;
        switch (operation) {
            case ADD:
                newStock = material.getCurrentStock().add(quantity);
                material.setLastPurchaseDate(LocalDateTime.now());
                break;
            case REMOVE:
                newStock = material.getCurrentStock().subtract(quantity);
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Insufficient stock");
                }
                break;
            case SET:
                newStock = quantity;
                break;
            default:
                throw new RuntimeException("Invalid stock operation");
        }

        material.setCurrentStock(newStock);
        material = rawMaterialRepository.save(material);

        log.info("Stock updated for material: {} - Operation: {}, Quantity: {}, New stock: {}",
                material.getName(), operation, quantity, newStock);

        return mapToResponse(material);
    }

    /**
     * Gets all materials for a user
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "materials", key = "#username + '_' + #pageable.pageNumber")
    public Page<RawMaterialResponse> getUserMaterials(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return rawMaterialRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets materials by category
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialResponse> getMaterialsByCategory(String username, Long categoryId,
                                                            Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return rawMaterialRepository.findByUserAndCategory(user, category, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Searches materials
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialResponse> searchMaterials(String username, String search,
                                                     Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return rawMaterialRepository.searchByUser(user, search, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets material by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "materials", key = "#materialId")
    public RawMaterialResponse getMaterialById(Long materialId, String username) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        validateMaterialOwnership(material, username);

        return mapToResponse(material);
    }

    /**
     * Gets low stock items
     */
    @Transactional(readOnly = true)
    public List<RawMaterialResponse> getLowStockItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return rawMaterialRepository.findLowStockItems(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets price history for a material
     */
    @Transactional(readOnly = true)
    public Page<PriceHistoryResponse> getPriceHistory(Long materialId, String username, Pageable pageable) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        validateMaterialOwnership(material, username);

        return priceHistoryRepository.findByRawMaterialOrderByChangedAtDesc(material, pageable)
                .map(this::mapPriceHistoryToResponse);
    }

    /**
     * Deletes a material
     */
    @Transactional
    @CacheEvict(value = "materials", key = "#materialId")
    public void deleteMaterial(Long materialId, String username) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        validateMaterialOwnership(material, username);

        // Check if material is used in recipes
        // This would be implemented with a proper check

        rawMaterialRepository.delete(material);

        log.info("Material deleted: {} by user: {}", material.getName(), username);
    }

    private void validateMaterialOwnership(RawMaterial material, String username) {
        if (!material.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to material");
        }
    }

    private RawMaterialResponse mapToResponse(RawMaterial material) {
        return RawMaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .brand(material.getBrand())
                .supplier(material.getSupplier())
                .categoryName(material.getCategory().getName())
                .purchaseUnit(material.getPurchaseUnit().getCode())
                .purchaseQuantity(material.getPurchaseQuantity())
                .unitCost(material.getUnitCost())
                .currency(material.getCurrency())
                .currentStock(material.getCurrentStock())
                .minimumStock(material.getMinimumStock())
                .needsRecalculation(material.getNeedsRecalculation())
                .allergens(material.getAllergens().stream()
                        .map(Allergen::getName)
                        .collect(Collectors.toSet()))
                .createdAt(material.getCreatedAt())
                .lastPriceUpdate(material.getLastPriceUpdate())
                .build();
    }

    private PriceHistoryResponse mapPriceHistoryToResponse(MaterialPriceHistory history) {
        return PriceHistoryResponse.builder()
                .id(history.getId())
                .previousCost(history.getPreviousCost())
                .newCost(history.getNewCost())
                .changePercentage(history.getChangePercentage())
                .changedAt(history.getChangedAt())
                .changedBy(history.getChangedBy())
                .reason(history.getReason())
                .build();
    }
}
