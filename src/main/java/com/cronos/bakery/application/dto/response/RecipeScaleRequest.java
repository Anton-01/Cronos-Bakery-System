package com.cronos.bakery.application.dto.response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RecipeScaleRequest {
    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal scaleFactor;
}
