package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.application.dto.BreakEvenPoint;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BreakEvenResponse {
    private Long recipeId;
    private String recipeName;
    private BigDecimal variableCostPerUnit;
    private BigDecimal fixedCosts;
    private List<BreakEvenPoint> breakEvenPoints;
}
