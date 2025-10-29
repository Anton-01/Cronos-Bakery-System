package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UnitConversionResponse {
    private BigDecimal originalQuantity;
    private String fromUnit;
    private BigDecimal convertedQuantity;
    private String toUnit;
    private BigDecimal conversionFactor;
}
