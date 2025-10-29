package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class QuoteStats {
    private Long totalQuotes;
    private Long pendingQuotes;
    private BigDecimal monthlyRevenue;
    private String currency;
}
