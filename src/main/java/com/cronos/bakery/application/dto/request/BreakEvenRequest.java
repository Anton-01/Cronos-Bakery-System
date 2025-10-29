package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakEvenRequest {

    @NotNull
    private Long recipeId;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal fixedCostsPerMonth;

    @NotEmpty
    private List<BigDecimal> sellingPrices;
}
