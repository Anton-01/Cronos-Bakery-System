package com.cronos.bakery.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuoteRequest {
    @NotBlank(message = "Client name is required")
    private String clientName;

    @Email
    private String clientEmail;

    private String clientPhone;

    private String clientAddress;

    private String notes;

    private Integer validityDays;

    @Valid
    @NotEmpty(message = "At least one item is required")
    private List<QuoteItemRequest> items;
}
