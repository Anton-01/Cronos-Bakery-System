package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.inventory.InventorySummary;
import com.cronos.bakery.application.dto.notifications.LowStockItem;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryManagementService {

    private final RawMaterialRepository rawMaterialRepository;
    private final EmailService emailService;

    /**
     * Updates stock after using materials in production
     */
    @Transactional
    public void consumeStock(Long materialId, BigDecimal quantity) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        BigDecimal newStock = material.getCurrentStock().subtract(quantity);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient stock for material: " + material.getName());
        }

        material.setCurrentStock(newStock);
        rawMaterialRepository.save(material);

        // Check if stock is below minimum
        if (material.getMinimumStock() != null &&
                newStock.compareTo(material.getMinimumStock()) < 0) {
            log.warn("Low stock alert for material: {} - Current: {}, Minimum: {}",
                    material.getName(), newStock, material.getMinimumStock());
        }
    }

    /**
     * Adds stock after purchase
     */
    @Transactional
    public void addStock(Long materialId, BigDecimal quantity) {
        RawMaterial material = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        BigDecimal newStock = material.getCurrentStock().add(quantity);
        material.setCurrentStock(newStock);
        rawMaterialRepository.save(material);

        log.info("Stock added for material: {} - Added: {}, New total: {}",
                material.getName(), quantity, newStock);
    }

    /**
     * Scheduled task to check low stock items (runs daily at 8 AM)
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void checkLowStockAndNotify() {
        log.info("Running scheduled low stock check");

        // Get all users
        List<User> users = rawMaterialRepository.findAll().stream()
                .map(RawMaterial::getUser)
                .distinct()
                .toList();

        for (User user : users) {
            List<RawMaterial> lowStockItems = rawMaterialRepository.findLowStockItems(user);

            if (!lowStockItems.isEmpty()) {
                List<LowStockItem> items = lowStockItems.stream()
                        .map(material -> LowStockItem.builder()
                                .materialId(material.getId())
                                .materialName(material.getName())
                                .currentStock(material.getCurrentStock())
                                .minimumStock(material.getMinimumStock())
                                .unit(material.getPurchaseUnit().getCode())
                                .build())
                        .toList();

                emailService.sendLowStockAlert(user, items);

                log.info("Low stock alert sent to user: {} - {} items",
                        user.getUsername(), items.size());
            }
        }
    }

    /**
     * Gets inventory summary for a user
     */
    public InventorySummary getInventorySummary(User user) {
        List<RawMaterial> allMaterials = rawMaterialRepository.findByUser(user, null).getContent();

        long totalItems = allMaterials.size();
        long lowStockItems = rawMaterialRepository.countByUserAndCurrentStockLessThanMinimumStock(user);

        BigDecimal totalInventoryValue = allMaterials.stream()
                .map(material -> {
                    BigDecimal costPerUnit = material.getUnitCost()
                            .divide(material.getPurchaseQuantity(), 6, BigDecimal.ROUND_HALF_UP);
                    return material.getCurrentStock().multiply(costPerUnit);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return InventorySummary.builder()
                .totalItems(totalItems)
                .lowStockItems(lowStockItems)
                .totalInventoryValue(totalInventoryValue)
                .currency(user.getDefaultCurrency())
                .build();
    }
}
