package com.cronos.bakery.application.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceHistoryResponse {
    private Long id;
    private BigDecimal previousCost;
    private BigDecimal newCost;
    private BigDecimal changePercentage;
    private LocalDateTime changedAt;
    private String changedBy;
    private String reason;
}
