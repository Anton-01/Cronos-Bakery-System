package com.cronos.bakery.domain.entity;

import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_notifications", indexes = {
        @Index(name = "idx_user_notification", columnList = "user_id, created_at"),
        @Index(name = "idx_notification_read", columnList = "user_id, is_read")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationSeverity severity = NotificationSeverity.INFO;

    // Device/Session Information
    @Column(name = "device_name", length = 200)
    private String deviceName;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markEmailAsSent() {
        this.emailSent = true;
        this.emailSentAt = LocalDateTime.now();
    }

    public enum NotificationType {
        NEW_DEVICE_LOGIN,
        UNKNOWN_LOCATION_LOGIN,
        PASSWORD_CHANGED,
        TWO_FACTOR_ENABLED,
        TWO_FACTOR_DISABLED,
        SESSION_TERMINATED,
        ACCOUNT_LOCKED,
        FAILED_LOGIN_ATTEMPT,
        PROFILE_UPDATED,
        EMAIL_CHANGED,
        SUSPICIOUS_ACTIVITY
    }

    public enum NotificationSeverity {
        INFO,
        WARNING,
        CRITICAL
    }
}
