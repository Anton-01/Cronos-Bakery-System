package com.cronos.bakery.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RawMaterialResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String supplier;
    private String categoryName;
    private String purchaseUnit;
    private BigDecimal purchaseQuantity;
    private BigDecimal unitCost;
    private String currency;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private Boolean needsRecalculation;
    private Set<String> allergens;
    private LocalDateTime createdAt;
    private LocalDateTime lastPriceUpdate;
}
