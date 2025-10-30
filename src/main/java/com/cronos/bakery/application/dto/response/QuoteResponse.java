package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.domain.entity.quote.enums.QuoteStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class QuoteResponse {
    private Long id;
    private String quoteNumber;
    private String clientName;
    private String clientEmail;
    private QuoteStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String currency;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
}
