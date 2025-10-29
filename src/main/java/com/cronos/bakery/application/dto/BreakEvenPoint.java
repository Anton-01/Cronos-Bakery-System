package com.cronos.bakery.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BreakEvenPoint {
    private BigDecimal sellingPrice;
    private BigDecimal unitsToBreakEven;
    private BigDecimal revenueToBreakEven;
    private BigDecimal contributionMargin;
}
