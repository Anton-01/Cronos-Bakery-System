package com.cronos.bakery.domain.service;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class NutritionalInfo {
    private BigDecimal calories;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;
    private BigDecimal fats;
    private BigDecimal fiber;
    private BigDecimal sugars;
    private BigDecimal sodium;
}
