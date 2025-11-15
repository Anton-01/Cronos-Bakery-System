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
public class QuoteStatisticsResponse {
    private Long totalQuotes;
    private Long draftQuotes;
    private Long sentQuotes;
    private Long viewedQuotes;
    private Long acceptedQuotes;
    private Long rejectedQuotes;
    private BigDecimal totalQuotedValue;
    private BigDecimal totalAcceptedValue;
    private String currency;
    private Double conversionRate;
    private Long activeQuotes;
}
