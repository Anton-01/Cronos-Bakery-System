package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;

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
    private Boolean notifyPriceChanges = true;

    @Column(name = "price_change_threshold_percent")
    private Double priceChangeThresholdPercent = 5.0;

    @Column(name = "notify_price_increase_only", nullable = false)
    private Boolean notifyPriceIncreaseOnly = false;

    // Low stock notifications
    @Column(name = "notify_low_stock", nullable = false)
    private Boolean notifyLowStock = true;

    @Column(name = "low_stock_threshold_percent")
    private Double lowStockThresholdPercent = 20.0;

    // Quote notifications
    @Column(name = "notify_quote_viewed", nullable = false)
    private Boolean notifyQuoteViewed = true;

    @Column(name = "notify_quote_expiring", nullable = false)
    private Boolean notifyQuoteExpiring = true;

    @Column(name = "quote_expiry_notice_hours")
    private Integer quoteExpiryNoticeHours = 24;

    // Recipe cost notifications
    @Column(name = "notify_recipe_cost_change", nullable = false)
    private Boolean notifyRecipeCostChange = true;

    @Column(name = "recipe_cost_change_threshold_percent")
    private Double recipeCostChangeThresholdPercent = 10.0;

    // General notifications
    @Column(name = "notify_daily_summary", nullable = false)
    private Boolean notifyDailySummary = false;

    @Column(name = "notify_weekly_report", nullable = false)
    private Boolean notifyWeeklyReport = false;

    @Column(name = "notify_monthly_report", nullable = false)
    private Boolean notifyMonthlyReport = true;

    // Notification channels
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    @Column(name = "websocket_notifications", nullable = false)
    private Boolean websocketNotifications = true;

    @Column(name = "quiet_hours_start")
    private Integer quietHoursStart; // 0-23

    @Column(name = "quiet_hours_end")
    private Integer quietHoursEnd; // 0-23

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
