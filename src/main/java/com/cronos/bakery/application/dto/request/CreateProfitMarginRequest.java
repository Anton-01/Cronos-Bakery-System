package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfitMarginRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0")
    @DecimalMax(value = "500")
    private BigDecimal percentage;

    private Boolean isDefault;
}
