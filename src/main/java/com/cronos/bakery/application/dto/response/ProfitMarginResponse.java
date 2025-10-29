package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProfitMarginResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal percentage;
    private Boolean isDefault;
    private Boolean isActive;
}
