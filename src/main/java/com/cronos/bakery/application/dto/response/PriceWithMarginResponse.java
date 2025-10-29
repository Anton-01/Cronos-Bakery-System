package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PriceWithMarginResponse {
    private String marginName;
    private BigDecimal marginPercentage;
    private BigDecimal sellingPrice;
    private BigDecimal profitAmount;
}
