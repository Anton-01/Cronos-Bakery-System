package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceAlert {
    private Long materialId;
    private String materialName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal changePercentage;
    private Integer affectedRecipes;
    private LocalDateTime changedAt;
}
