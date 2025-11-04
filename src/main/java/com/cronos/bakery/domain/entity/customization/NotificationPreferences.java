package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity for user notification preferences
 */
@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferences extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Price change notifications
    @Column(name = "notify_price_changes", nullable = false)
    @Builder.Default
    private Boolean notifyPriceChanges = true;

    @Column(name = "price_change_threshold_percent")
    @Builder.Default
    private BigDecimal priceChangeThresholdPercent = new BigDecimal("5.00");

    @Column(name = "notify_price_increase_only", nullable = false)
    @Builder.Default
    private Boolean notifyPriceIncreaseOnly = false;

    // Low stock notifications
    @Column(name = "notify_low_stock", nullable = false)
    @Builder.Default
    private Boolean notifyLowStock = true;

    @Column(name = "low_stock_threshold_percent")
    @Builder.Default
    private BigDecimal lowStockThresholdPercent = new BigDecimal("20.00");

    // Quote notifications
    @Column(name = "notify_quote_viewed", nullable = false)
    @Builder.Default
    private Boolean notifyQuoteViewed = true;

    @Column(name = "notify_quote_expiring", nullable = false)
    @Builder.Default
    private Boolean notifyQuoteExpiring = true;

    @Column(name = "quote_expiry_notice_hours")
    @Builder.Default
    private Integer quoteExpiryNoticeHours = 24;

    // Recipe cost notifications
    @Column(name = "notify_recipe_cost_change", nullable = false)
    @Builder.Default
    private Boolean notifyRecipeCostChange = true;

    @Column(name = "recipe_cost_change_threshold_percent")
    @Builder.Default
    private BigDecimal recipeCostChangeThresholdPercent = new BigDecimal("10.00");

    // General notifications
    @Column(name = "notify_daily_summary", nullable = false)
    @Builder.Default
    private Boolean notifyDailySummary = false;

    @Column(name = "notify_weekly_report", nullable = false)
    @Builder.Default
    private Boolean notifyWeeklyReport = false;

    @Column(name = "notify_monthly_report", nullable = false)
    @Builder.Default
    private Boolean notifyMonthlyReport = true;

    // Notification channels
    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "websocket_notifications", nullable = false)
    @Builder.Default
    private Boolean websocketNotifications = true;

    @Column(name = "quiet_hours_start")
    private Integer quietHoursStart; // 0-23

    @Column(name = "quiet_hours_end")
    private Integer quietHoursEnd; // 0-23

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
