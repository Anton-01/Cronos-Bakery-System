package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class QuoteItemRequest {
    @NotNull
    private Long recipeId;

    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal quantity;

    private BigDecimal scaleFactor;

    @NotNull
    private Long profitMarginId;

    private String notes;

    private Integer displayOrder;
}
