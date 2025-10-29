package com.cronos.bakery.application.dto.notifications;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LowStockItem {
    private Long materialId;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private String unit;
}
