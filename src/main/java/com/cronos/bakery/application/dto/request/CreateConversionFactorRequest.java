package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversionFactorRequest {
    @NotNull(message = "From unit is required")
    private Long fromUnitId;

    @NotNull(message = "To unit is required")
    private Long toUnitId;

    @NotNull(message = "Factor is required")
    @DecimalMin(value = "0.0000000001")
    private BigDecimal factor;

    private String notes;
}
