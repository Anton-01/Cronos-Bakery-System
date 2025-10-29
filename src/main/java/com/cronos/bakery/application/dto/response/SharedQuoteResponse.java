package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SharedQuoteResponse {
    private String quoteNumber;
    private String businessName;
    private String clientName;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String currency;
    private LocalDateTime validUntil;
    private List<SharedQuoteItemResponse> items;
}
