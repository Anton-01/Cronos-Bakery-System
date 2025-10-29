package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UnitConversionRequest {
    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantity;

    @NotNull
    private Long fromUnitId;

    @NotNull
    private Long toUnitId;
}
