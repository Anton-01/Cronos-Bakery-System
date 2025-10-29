package com.cronos.bakery.application.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateQuoteRequest {
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientAddress;
    private String notes;
    private QuoteStatus status;
}
