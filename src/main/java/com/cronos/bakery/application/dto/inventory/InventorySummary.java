package com.cronos.bakery.application.dto.inventory;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventorySummary {
    private Long totalItems;
    private Long lowStockItems;
    private BigDecimal totalInventoryValue;
    private String currency;
}
