package com.cronos.bakery.domain.service;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PriceChangeEmailData {
    private String materialName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal changePercentage;
    private Integer affectedRecipesCount;
    private List<String> affectedRecipes;
}
