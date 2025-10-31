package com.cronos.bakery.domain.entity.quote;

import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quote_access_logs", indexes = {
        @Index(name = "idx_quote_access", columnList = "quote_id, accessed_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "accessed_by_email")
    private String accessedByEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}