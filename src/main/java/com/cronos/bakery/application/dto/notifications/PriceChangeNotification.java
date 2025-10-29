package com.cronos.bakery.application.dto.notifications;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceChangeNotification {
    private Long materialId;
    private String materialName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal changePercentage;
    private Integer affectedRecipesCount;
    private LocalDateTime timestamp;
}
