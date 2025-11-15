package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.recipes.RecipeCostCalculation;
import com.cronos.bakery.application.dto.request.CreateRecipeRequest;
import com.cronos.bakery.application.dto.request.RecipeFixedCostRequest;
import com.cronos.bakery.application.dto.request.RecipeIngredientRequest;
import com.cronos.bakery.application.dto.request.RecipeSubRecipeRequest;
import com.cronos.bakery.application.dto.response.PriceWithMarginResponse;
import com.cronos.bakery.application.dto.response.RecipeCostResponse;
import com.cronos.bakery.application.dto.response.RecipeResponse;
import com.cronos.bakery.application.dto.response.RecipeStatisticsResponse;
import com.cronos.bakery.application.dto.response.RecipeVersionResponse;
import com.cronos.bakery.application.service.enums.CostCalculationMethod;
import com.cronos.bakery.application.service.enums.FixedCostType;
import com.cronos.bakery.domain.entity.core.*;
import com.cronos.bakery.domain.entity.recipes.*;
import com.cronos.bakery.domain.entity.recipes.enums.RecipeStatus;
import com.cronos.bakery.domain.service.RecipeCostCalculationService;
import com.cronos.bakery.infrastructure.persistence.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RecipeVersionRepository recipeVersionRepository;
    private final RecipeCostHistoryRepository costHistoryRepository;
    private final ProfitMarginRepository profitMarginRepository;
    private final RecipeCostCalculationService costCalculationService;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new recipe
     */
    @Transactional
    @CacheEvict(value = "recipes", allEntries = true)
    public RecipeResponse createRecipe(CreateRecipeRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = Recipe.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .yieldQuantity(request.getYieldQuantity())
                .yieldUnit(request.getYieldUnit())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .bakingTimeMinutes(request.getBakingTimeMinutes())
                .coolingTimeMinutes(request.getCoolingTimeMinutes())
                .instructions(request.getInstructions())
                .status(RecipeStatus.DRAFT)
                .currentVersion(1)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            recipe.setCategory(category);
        }

        // Add ingredients
        Set<RecipeIngredient> ingredients = new HashSet<>();
        for (RecipeIngredientRequest ingReq : request.getIngredients()) {
            RecipeIngredient ingredient = createIngredient(ingReq, recipe);
            ingredients.add(ingredient);
        }
        recipe.setIngredients(ingredients);

        // Add sub-recipes
        if (request.getSubRecipes() != null) {
            Set<RecipeSubRecipe> subRecipes = new HashSet<>();
            for (RecipeSubRecipeRequest subReq : request.getSubRecipes()) {
                RecipeSubRecipe subRecipe = createSubRecipe(subReq, recipe);
                subRecipes.add(subRecipe);
            }
            recipe.setSubRecipes(subRecipes);
        }

        // Add fixed costs
        if (request.getFixedCosts() != null) {
            Set<RecipeFixedCost> fixedCosts = new HashSet<>();
            for (RecipeFixedCostRequest fcReq : request.getFixedCosts()) {
                RecipeFixedCost fixedCost = createFixedCost(fcReq, recipe);
                fixedCosts.add(fixedCost);
            }
            recipe.setFixedCosts(fixedCosts);
        }

        // Calculate allergens
        updateRecipeAllergens(recipe);

        recipe = recipeRepository.save(recipe);

        // Calculate initial cost
        calculateAndSaveCost(recipe, user);

        // Create initial version
        createRecipeVersion(recipe, "Initial version", user.getUsername());

        log.info("Recipe created: {} by user: {}", recipe.getName(), username);

        return mapToResponse(recipe, user);
    }

    /**
     * Updates an existing recipe
     */
    @Transactional
    @CacheEvict(value = "recipes", key = "#recipeId")
    public RecipeResponse updateRecipe(Long recipeId, CreateRecipeRequest request, String changes, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        validateRecipeOwnership(recipe, username);

        User user = recipe.getUser();

        // Update basic fields
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setYieldQuantity(request.getYieldQuantity());
        recipe.setYieldUnit(request.getYieldUnit());
        recipe.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        recipe.setBakingTimeMinutes(request.getBakingTimeMinutes());
        recipe.setInstructions(request.getInstructions());

        // Update ingredients
        recipe.getIngredients().clear();
        for (RecipeIngredientRequest ingReq : request.getIngredients()) {
            RecipeIngredient ingredient = createIngredient(ingReq, recipe);
            recipe.getIngredients().add(ingredient);
        }

        // Update allergens
        updateRecipeAllergens(recipe);

        // Increment version
        recipe.setCurrentVersion(recipe.getCurrentVersion() + 1);

        recipe = recipeRepository.save(recipe);

        // Recalculate cost
        calculateAndSaveCost(recipe, user);

        // Create new version
        createRecipeVersion(recipe, changes, username);

        log.info("Recipe updated: {} - Version: {}", recipe.getName(), recipe.getCurrentVersion());

        return mapToResponse(recipe, user);
    }

    /**
     * Calculates recipe cost with scale factor
     */
    @Transactional(readOnly = true)
    public RecipeCostResponse calculateRecipeCost(Long recipeId, BigDecimal scaleFactor, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        validateRecipeOwnership(recipe, username);

        User user = recipe.getUser();

        RecipeCostCalculation costCalc = costCalculationService.calculateRecipeCost(
                recipe,
                scaleFactor != null ? scaleFactor : BigDecimal.ONE,
                user
        );

        // Get all profit margins
        List<ProfitMargin> margins = profitMarginRepository.findByUserAndIsActiveTrue(user);
        Map<String, BigDecimal> pricesWithMargins =
                costCalculationService.calculatePriceWithMargins(costCalc, margins);

        List<PriceWithMarginResponse> priceResponses = pricesWithMargins.entrySet().stream()
                .map(entry -> {
                    ProfitMargin margin = margins.stream()
                            .filter(m -> m.getName().equals(entry.getKey()))
                            .findFirst()
                            .orElseThrow();

                    return PriceWithMarginResponse.builder()
                            .marginName(entry.getKey())
                            .marginPercentage(margin.getPercentage())
                            .sellingPrice(entry.getValue())
                            .profitAmount(entry.getValue().subtract(costCalc.getCostPerUnit()))
                            .build();
                })
                .collect(Collectors.toList());

        return RecipeCostResponse.builder()
                .recipeId(recipe.getId())
                .recipeName(recipe.getName())
                .scaleFactor(costCalc.getScaleFactor())
                .materialsCost(costCalc.getMaterialsCost())
                .subRecipesCost(costCalc.getSubRecipesCost())
                .fixedCosts(costCalc.getFixedCosts())
                .totalCost(costCalc.getTotalCost())
                .costPerUnit(costCalc.getCostPerUnit())
                .currency(user.getDefaultCurrency())
                .pricesWithDifferentMargins(priceResponses)
                .build();
    }

    /**
     * Gets all recipes for a user
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "recipes", key = "#username + '_' + #pageable.pageNumber")
    public Page<RecipeResponse> getUserRecipes(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.findByUser(user, pageable)
                .map(recipe -> mapToResponse(recipe, user));
    }

    /**
     * Searches recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(String username, String search, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.searchByUser(user, search, pageable)
                .map(recipe -> mapToResponse(recipe, user));
    }

    /**
     * Gets recipe by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "recipes", key = "#recipeId")
    public RecipeResponse getRecipeById(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        validateRecipeOwnership(recipe, username);

        return mapToResponse(recipe, recipe.getUser());
    }

    /**
     * Gets recipe versions
     */
    @Transactional(readOnly = true)
    public List<RecipeVersionResponse> getRecipeVersions(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        validateRecipeOwnership(recipe, username);

        return recipeVersionRepository.findByRecipeOrderByVersionNumberDesc(recipe).stream()
                .map(this::mapVersionToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Recalculates costs for recipes using a material
     */
    @Transactional
    public void recalculateCostsForMaterial(Long materialId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Recipe> affectedRecipes = recipeRepository.findRecipesUsingMaterial(materialId);

        for (Recipe recipe : affectedRecipes) {
            if (recipe.getUser().equals(user)) {
                calculateAndSaveCost(recipe, user);
                recipe.setNeedsRecalculation(false);
            }
        }

        recipeRepository.saveAll(affectedRecipes);

        log.info("Recalculated costs for {} recipes affected by material change", affectedRecipes.size());
    }

    private RecipeIngredient createIngredient(RecipeIngredientRequest request, Recipe recipe) {
        RawMaterial material = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RuntimeException("Raw material not found"));

        MeasurementUnit unit = measurementUnitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        return RecipeIngredient.builder()
                .recipe(recipe)
                .rawMaterial(material)
                .quantity(request.getQuantity())
                .unit(unit)
                .isOptional(request.getIsOptional())
                .notes(request.getNotes())
                .displayOrder(request.getDisplayOrder())
                .build();
    }

    private RecipeSubRecipe createSubRecipe(RecipeSubRecipeRequest request, Recipe parentRecipe) {
        Recipe subRecipe = recipeRepository.findById(request.getSubRecipeId())
                .orElseThrow(() -> new RuntimeException("Sub-recipe not found"));

        return RecipeSubRecipe.builder()
                .parentRecipe(parentRecipe)
                .subRecipe(subRecipe)
                .quantity(request.getQuantity())
                .displayOrder(request.getDisplayOrder())
                .build();
    }

    private RecipeFixedCost createFixedCost(RecipeFixedCostRequest request, Recipe recipe) {
        return RecipeFixedCost.builder()
                .recipe(recipe)
                .name(request.getName())
                .description(request.getDescription())
                .type(FixedCostType.valueOf(request.getType()))
                .amount(request.getAmount())
                .calculationMethod(CostCalculationMethod.valueOf(request.getCalculationMethod()))
                .timeInMinutes(request.getTimeInMinutes())
                .percentage(request.getPercentage())
                .build();
    }

    private void updateRecipeAllergens(Recipe recipe) {
        Set<Allergen> allergens = new HashSet<>();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            allergens.addAll(ingredient.getRawMaterial().getAllergens());
        }

        recipe.setAllergens(allergens);
    }

    private void calculateAndSaveCost(Recipe recipe, User user) {
        RecipeCostCalculation costCalc = costCalculationService.calculateRecipeCost(recipe, user);

        RecipeCostHistory history = RecipeCostHistory.builder()
                .recipe(recipe)
                .recipeVersion(recipe.getCurrentVersion())
                .materialsCost(costCalc.getMaterialsCost())
                .fixedCosts(costCalc.getFixedCosts())
                .subRecipesCost(costCalc.getSubRecipesCost())
                .totalCost(costCalc.getTotalCost())
                .costPerUnit(costCalc.getCostPerUnit())
                .currency(user.getDefaultCurrency())
                .calculatedAt(LocalDateTime.now())
                .build();

        costHistoryRepository.save(history);
    }

    private void createRecipeVersion(Recipe recipe, String changes, String username) {
        try {
            String snapshotData = objectMapper.writeValueAsString(recipe);

            RecipeVersion version = RecipeVersion.builder()
                    .recipe(recipe)
                    .versionNumber(recipe.getCurrentVersion())
                    .changes(changes)
                    .snapshotData(snapshotData)
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .isCurrent(true)
                    .build();

            // Mark previous version as not current
            recipeVersionRepository.findCurrentVersion(recipe)
                    .ifPresent(prev -> {
                        prev.setIsCurrent(false);
                        recipeVersionRepository.save(prev);
                    });

            recipeVersionRepository.save(version);

        } catch (Exception e) {
            log.error("Error creating recipe version: {}", e.getMessage());
        }
    }

    private void validateRecipeOwnership(Recipe recipe, String username) {
        if (!recipe.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to recipe");
        }
    }

    private RecipeResponse mapToResponse(Recipe recipe, User user) {
        Optional<RecipeCostHistory> latestCost = costHistoryRepository.findLatestByRecipe(recipe);

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .categoryName(recipe.getCategory() != null ? recipe.getCategory().getName() : null)
                .yieldQuantity(recipe.getYieldQuantity())
                .yieldUnit(recipe.getYieldUnit())
                .preparationTimeMinutes(recipe.getPreparationTimeMinutes())
                .bakingTimeMinutes(recipe.getBakingTimeMinutes())
                .status(recipe.getStatus())
                .needsRecalculation(recipe.getNeedsRecalculation())
                .currentVersion(recipe.getCurrentVersion())
                .estimatedCost(latestCost.map(RecipeCostHistory::getTotalCost).orElse(null))
                .allergens(recipe.getAllergens().stream()
                        .map(a -> "es".equals(user.getDefaultLanguage()) ? a.getNameEs() : a.getNameEn())
                        .collect(Collectors.toSet()))
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }

    private RecipeVersionResponse mapVersionToResponse(RecipeVersion version) {
        return RecipeVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .versionName(version.getVersionName())
                .changes(version.getChanges())
                .createdAt(version.getCreatedAt())
                .createdBy(version.getCreatedBy())
                .isCurrent(version.getIsCurrent())
                .build();
    }

    /**
     * Gets statistics for recipes
     */
    @Transactional(readOnly = true)
    public RecipeStatisticsResponse getStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalRecipes = recipeRepository.countByUser(user);
        long draftRecipes = recipeRepository.countByUserAndStatus(user, RecipeStatus.DRAFT);
        long activeRecipes = recipeRepository.countByUserAndStatus(user, RecipeStatus.ACTIVE);
        long archivedRecipes = recipeRepository.countByUserAndStatus(user, RecipeStatus.ARCHIVED);
        long recipesNeedingRecalculation = recipeRepository.countByUserAndNeedsRecalculation(user);
        long totalCategories = recipeRepository.countDistinctCategoriesByUser(user);
        long totalIngredients = recipeRepository.countTotalIngredientsByUser(user);

        BigDecimal averageCost = costHistoryRepository.calculateAverageCostPerRecipeByUser(user.getId());
        if (averageCost == null) {
            averageCost = BigDecimal.ZERO;
        }

        return RecipeStatisticsResponse.builder()
                .totalRecipes(totalRecipes)
                .draftRecipes(draftRecipes)
                .activeRecipes(activeRecipes)
                .archivedRecipes(archivedRecipes)
                .recipesNeedingRecalculation(recipesNeedingRecalculation)
                .averageCostPerRecipe(averageCost.setScale(2, java.math.RoundingMode.HALF_UP))
                .currency(user.getDefaultCurrency())
                .totalCategories(totalCategories)
                .totalIngredients(totalIngredients)
                .build();
    }
}
