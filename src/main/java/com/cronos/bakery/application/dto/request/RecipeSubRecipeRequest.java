package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RecipeSubRecipeRequest {
    @NotNull
    private Long subRecipeId;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal quantity;

    private Integer displayOrder;
}
