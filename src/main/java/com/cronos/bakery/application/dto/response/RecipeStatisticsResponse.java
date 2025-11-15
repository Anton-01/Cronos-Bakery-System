package com.cronos.bakery.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStatisticsResponse {
    private Long totalRecipes;
    private Long draftRecipes;
    private Long activeRecipes;
    private Long archivedRecipes;
    private Long recipesNeedingRecalculation;
    private BigDecimal averageCostPerRecipe;
    private String currency;
    private Long totalCategories;
    private Long totalIngredients;
}
