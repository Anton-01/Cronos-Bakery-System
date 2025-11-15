package com.cronos.bakery.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialStatisticsResponse {
    private Long totalMaterials;
    private Long activeMaterials;
    private Long inactiveMaterials;
    private Long lowStockCount;
    private Long outOfStockCount;
    private BigDecimal totalInventoryValue;
    private String currency;
    private Long totalCategories;
    private BigDecimal averageStockLevel;
}
