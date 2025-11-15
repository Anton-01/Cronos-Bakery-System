package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.domain.entity.inventory.enums.AlertStatus;
import com.cronos.bakery.domain.entity.inventory.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertResponse {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
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
