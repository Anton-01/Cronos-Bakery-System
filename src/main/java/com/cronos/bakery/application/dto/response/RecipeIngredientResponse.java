package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RecipeIngredientResponse {
    private Long id;
    private String materialName;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal costPerUnit;
    private BigDecimal totalCost;
    private Boolean isOptional;
    private String notes;
}
