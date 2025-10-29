package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RecipeCostResponse {
    private Long recipeId;
    private String recipeName;
    private BigDecimal scaleFactor;
    private BigDecimal materialsCost;
    private BigDecimal subRecipesCost;
    private BigDecimal fixedCosts;
    private BigDecimal totalCost;
    private BigDecimal costPerUnit;
    private String currency;
    private List<PriceWithMarginResponse> pricesWithDifferentMargins;
}
