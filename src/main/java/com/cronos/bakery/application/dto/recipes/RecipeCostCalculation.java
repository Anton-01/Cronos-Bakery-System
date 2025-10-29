package com.cronos.bakery.application.dto.recipes;

import com.cronos.bakery.domain.entity.recipes.Recipe;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecipeCostCalculation {
    private Recipe recipe;
    private BigDecimal scaleFactor;
    private BigDecimal materialsCost;
    private BigDecimal subRecipesCost;
    private BigDecimal fixedCosts;
    private BigDecimal totalCost;
    private BigDecimal costPerUnit;
}
