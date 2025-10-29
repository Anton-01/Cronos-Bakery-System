package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ConversionFactorResponse {
    private Long id;
    private String fromUnit;
    private String toUnit;
    private BigDecimal factor;
    private Boolean isSystemDefault;
    private String notes;
}
