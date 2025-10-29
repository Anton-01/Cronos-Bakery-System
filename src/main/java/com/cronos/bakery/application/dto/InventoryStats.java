package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventoryStats {
    private Long totalMaterials;
    private Long lowStockItems;
    private BigDecimal totalInventoryValue;
    private String currency;
}
