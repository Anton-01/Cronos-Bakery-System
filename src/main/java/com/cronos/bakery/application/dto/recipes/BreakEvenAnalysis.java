package com.cronos.bakery.application.dto.recipes;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class BreakEvenAnalysis {
    private BigDecimal variableCostPerUnit;
    private BigDecimal fixedCosts;

    public BigDecimal calculateBreakEvenUnits(BigDecimal sellingPrice) {
        BigDecimal contribution = sellingPrice.subtract(variableCostPerUnit);

        if (contribution.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than variable cost");
        }

        return fixedCosts.divide(contribution, 0, RoundingMode.UP);
    }

    public BigDecimal calculateBreakEvenRevenue(BigDecimal sellingPrice) {
        return calculateBreakEvenUnits(sellingPrice).multiply(sellingPrice);
    }
}
