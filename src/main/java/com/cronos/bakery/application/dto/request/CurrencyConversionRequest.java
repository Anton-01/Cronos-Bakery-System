package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionRequest {

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String fromCurrency;

    @NotBlank
    @Size(min = 3, max = 3)
    private String toCurrency;
}
