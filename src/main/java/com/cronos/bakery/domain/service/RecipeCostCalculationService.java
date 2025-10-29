package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.recipes.BreakEvenAnalysis;
import com.cronos.bakery.application.dto.recipes.RecipeCostCalculation;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.recipes.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeCostCalculationService {

    private final UnitConversionService conversionService;

    /**
     * Calculates the total cost of a recipe
     */
    public RecipeCostCalculation calculateRecipeCost(Recipe recipe, User user) {
        return calculateRecipeCost(recipe, BigDecimal.ONE, user);
    }

    /**
     * Calculates recipe cost with scale factor (for doubling/tripling recipes)
     */
    public RecipeCostCalculation calculateRecipeCost(Recipe recipe, BigDecimal scaleFactor, User user) {

        RecipeCostCalculation calculation = new RecipeCostCalculation();
        calculation.setRecipe(recipe);
        calculation.setScaleFactor(scaleFactor);

        // Calculate ingredients cost
        BigDecimal ingredientsCost = calculateIngredientsCost(recipe, scaleFactor, user);
        calculation.setMaterialsCost(ingredientsCost);

        // Calculate sub-recipes cost
        BigDecimal subRecipesCost = calculateSubRecipesCost(recipe, scaleFactor, user);
        calculation.setSubRecipesCost(subRecipesCost);

        // Calculate fixed costs
        BigDecimal fixedCosts = calculateFixedCosts(recipe, scaleFactor, ingredientsCost);
        calculation.setFixedCosts(fixedCosts);

        // Total cost
        BigDecimal totalCost = ingredientsCost.add(subRecipesCost).add(fixedCosts);
        calculation.setTotalCost(totalCost);

        // Cost per unit
        BigDecimal adjustedYield = recipe.getYieldQuantity().multiply(scaleFactor);
        BigDecimal costPerUnit = totalCost.divide(adjustedYield, 6, RoundingMode.HALF_UP);
        calculation.setCostPerUnit(costPerUnit);

        return calculation;
    }

    private BigDecimal calculateIngredientsCost(Recipe recipe, BigDecimal scaleFactor, User user) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            BigDecimal ingredientCost = calculateIngredientCost(ingredient, scaleFactor, user);
            totalCost = totalCost.add(ingredientCost);
        }

        return totalCost;
    }

    private BigDecimal calculateIngredientCost(RecipeIngredient ingredient, BigDecimal scaleFactor, User user) {

        RawMaterial material = ingredient.getRawMaterial();

        // Scale the quantity
        BigDecimal scaledQuantity = ingredient.getQuantity().multiply(scaleFactor);

        // Convert recipe unit to purchase unit
        BigDecimal quantityInPurchaseUnit = conversionService.convert(
                scaledQuantity,
                ingredient.getUnit(),
                material.getPurchaseUnit(),
                user
        );

        // Calculate cost per purchase unit
        BigDecimal costPerPurchaseUnit = material.getUnitCost()
                .divide(material.getPurchaseQuantity(), 6, RoundingMode.HALF_UP);

        // Total cost for this ingredient
        return quantityInPurchaseUnit.multiply(costPerPurchaseUnit)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSubRecipesCost(Recipe recipe, BigDecimal scaleFactor, User user) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (RecipeSubRecipe subRecipe : recipe.getSubRecipes()) {
            BigDecimal subRecipeScaledQuantity = subRecipe.getQuantity().multiply(scaleFactor);

            // Recursively calculate sub-recipe cost
            RecipeCostCalculation subRecipeCost = calculateRecipeCost(
                    subRecipe.getSubRecipe(),
                    subRecipeScaledQuantity,
                    user
            );

            totalCost = totalCost.add(subRecipeCost.getTotalCost());
        }

        return totalCost;
    }

    private BigDecimal calculateFixedCosts(Recipe recipe, BigDecimal scaleFactor,
                                           BigDecimal materialsCost) {
        BigDecimal totalFixedCost = BigDecimal.ZERO;

        for (RecipeFixedCost fixedCost : recipe.getFixedCosts()) {
            BigDecimal cost = BigDecimal.ZERO;

            switch (fixedCost.getCalculationMethod()) {
                case FIXED_AMOUNT:
                    cost = fixedCost.getAmount();
                    break;

                case TIME_BASED:
                    // Cost per minute * time
                    BigDecimal totalMinutes = BigDecimal.valueOf(
                            recipe.getPreparationTimeMinutes() +
                                    recipe.getBakingTimeMinutes()
                    );
                    cost = fixedCost.getAmount().multiply(totalMinutes)
                            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                    break;

                case PERCENTAGE_OF_MATERIAL:
                    cost = materialsCost.multiply(fixedCost.getPercentage())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    break;

                case PER_UNIT:
                    BigDecimal adjustedYield = recipe.getYieldQuantity().multiply(scaleFactor);
                    cost = fixedCost.getAmount().multiply(adjustedYield);
                    break;
            }

            totalFixedCost = totalFixedCost.add(cost);
        }

        return totalFixedCost;
    }

    /**
     * Calculates break-even point
     */
    public BreakEvenAnalysis calculateBreakEven(Recipe recipe, User user, BigDecimal fixedCostsPerMonth) {

        RecipeCostCalculation costCalc = calculateRecipeCost(recipe, user);

        BreakEvenAnalysis analysis = new BreakEvenAnalysis();
        analysis.setVariableCostPerUnit(costCalc.getCostPerUnit());
        analysis.setFixedCosts(fixedCostsPerMonth);

        return analysis;
    }

    /**
     * Calculates price with different profit margins
     */
    public Map<String, BigDecimal> calculatePriceWithMargins(RecipeCostCalculation costCalc, List<ProfitMargin> margins) {

        Map<String, BigDecimal> prices = new HashMap<>();

        for (ProfitMargin margin : margins) {
            BigDecimal price = calculatePriceWithMargin(costCalc.getCostPerUnit(), margin);
            prices.put(margin.getName(), price);
        }

        return prices;
    }

    private BigDecimal calculatePriceWithMargin(BigDecimal cost, ProfitMargin margin) {
        BigDecimal multiplier = BigDecimal.ONE.add(
                margin.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        return cost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
