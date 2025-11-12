package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RawMaterialStatisticsResponse {
    private Long totalMaterials;
    private Long activeMaterials;
    private Long inactiveMaterials;
    private Long lowStockCount;
    private Long outOfStockCount;
    private BigDecimal totalInventoryValue;
    private BigDecimal averageMaterialCost;
    private String currency;
    private Long categoriesCount;
    private Long materialsWithAllergens;
}
