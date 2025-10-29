package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LowStockAlert {
    private Long materialId;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private String unit;
}
