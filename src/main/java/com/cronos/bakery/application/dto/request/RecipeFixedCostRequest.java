package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RecipeFixedCostRequest {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String type;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @NotBlank
    private String calculationMethod;

    private Integer timeInMinutes;

    private BigDecimal percentage;
}
