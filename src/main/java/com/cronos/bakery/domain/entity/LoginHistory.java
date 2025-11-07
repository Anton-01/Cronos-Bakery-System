package com.cronos.bakery.domain.entity;

import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history", indexes = {
        @Index(name = "idx_user_login", columnList = "user_id, login_at"),
        @Index(name = "idx_ip_address", columnList = "ip_address")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(length = 100)
    private String device;

    @Column(length = 100)
    private String location;

    @Builder.Default
    @Column(nullable = false)
    private Boolean successful = true;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "two_factor_used")
    private Boolean twoFactorUsed;

    @PrePersist
    protected void onCreate() {
        if (this.loginAt == null) {
            this.loginAt = LocalDateTime.now();
        }
    }
}
