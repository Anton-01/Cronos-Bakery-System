package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.domain.entity.inventory.enums.AlertStatus;
import com.cronos.bakery.domain.entity.inventory.enums.AlertType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockAlertResponse {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialUnit;
    private AlertType alertType;
    private AlertStatus status;
    private BigDecimal currentQuantity;
    private BigDecimal thresholdQuantity;
    private BigDecimal thresholdPercent;
    private String message;
    private LocalDateTime triggeredAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private Boolean emailSent;
    private Boolean autoResolved;
}
