package com.cronos.bakery.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientRequest {

    @NotNull(message = "Raw material is required")
    private Long rawMaterialId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001")
    private BigDecimal quantity;

    @NotNull(message = "Unit is required")
    private Long unitId;

    private Boolean isOptional;

    private String notes;

    private Integer displayOrder;

    @Valid
    private List<IngredientSubstituteRequest> substitutes;
}
