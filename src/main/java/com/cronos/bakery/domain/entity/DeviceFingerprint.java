package com.cronos.bakery.domain.entity;

import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_fingerprints", indexes = {
        @Index(name = "idx_user_device", columnList = "user_id, fingerprint_hash"),
        @Index(name = "idx_device_trusted", columnList = "user_id, is_trusted")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceFingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fingerprint_hash", nullable = false, length = 255)
    private String fingerprintHash;

    @Column(name = "device_name", length = 200)
    private String deviceName;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String location;

    @Builder.Default
    @Column(name = "is_trusted", nullable = false)
    private Boolean isTrusted = false;

    @Column(name = "first_seen_at", nullable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "trusted_at")
    private LocalDateTime trustedAt;

    @Column(name = "login_count", nullable = false)
    @Builder.Default
    private Integer loginCount = 0;

    @PrePersist
    protected void onCreate() {
        this.firstSeenAt = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
    }

    public void updateLastSeen() {
        this.lastSeenAt = LocalDateTime.now();
        this.loginCount++;
    }

    public void trust() {
        this.isTrusted = true;
        this.trustedAt = LocalDateTime.now();
    }

    public void untrust() {
        this.isTrusted = false;
        this.trustedAt = null;
    }
}
