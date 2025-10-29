package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class IngredientSubstituteRequest {
    @NotNull
    private Long substituteMaterialId;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal quantity;

    @NotNull
    private Long unitId;

    @NotNull
    private String reason;

    private String notes;
}
