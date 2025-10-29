package com.cronos.bakery.domain.entity.quote;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.quote.enums.QuoteStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quotes", indexes = {
        @Index(name = "idx_user_quote", columnList = "user_id, created_at"),
        @Index(name = "idx_share_token", columnList = "share_token")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_number", nullable = false, unique = true)
    private String quoteNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_address", length = 500)
    private String clientAddress;

    @Column(length = 2000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private String currency;

    // Share functionality
    @Column(name = "share_token", unique = true)
    private String shareToken;

    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;

    @Column(name = "is_shareable")
    @Builder.Default
    private Boolean isShareable = false;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<QuoteItem> items = new HashSet<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<QuoteAccessLog> accessLogs = new HashSet<>();

    @PrePersist
    public void generateQuoteNumber() {
        if (this.quoteNumber == null) {
            this.quoteNumber = "Q-" + System.currentTimeMillis();
        }
    }

    public void generateShareToken() {
        this.shareToken = UUID.randomUUID().toString();
        this.shareExpiresAt = LocalDateTime.now().plusHours(72);
        this.isShareable = true;
    }
}
