package com.cronos.bakery.domain.entity.inventory;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.inventory.enums.AlertStatus;
import com.cronos.bakery.domain.entity.inventory.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for programmable low stock alerts
 */
@Entity
@Table(name = "stock_alerts", indexes = {
    @Index(name = "idx_stock_alert_user", columnList = "user_id"),
    @Index(name = "idx_stock_alert_material", columnList = "raw_material_id"),
    @Index(name = "idx_stock_alert_status", columnList = "status"),
    @Index(name = "idx_stock_alert_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlert extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Column(name = "current_quantity", nullable = false)
    private BigDecimal currentQuantity;

    @Column(name = "threshold_quantity", nullable = false)
    private BigDecimal thresholdQuantity;

    @Column(name = "threshold_percent")
    private BigDecimal thresholdPercent;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AlertStatus status = AlertStatus.ACTIVE;

    @Column(name = "triggered_at", nullable = false)
    @Builder.Default
    private LocalDateTime triggeredAt = LocalDateTime.now();

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "email_sent", nullable = false)
    @Builder.Default
    private Boolean emailSent = false;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @Column(name = "auto_resolved", nullable = false)
    @Builder.Default
    private Boolean autoResolved = false;
}
