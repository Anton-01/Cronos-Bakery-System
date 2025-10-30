package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity for user email configuration settings
 */
@Entity
@Table(name = "email_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSettings extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Email(message = "Invalid sender email format")
    @Column(name = "sender_email", length = 200)
    private String senderEmail;

    @NotBlank(message = "Sender name is required")
    @Column(name = "sender_name", nullable = false, length = 200)
    private String senderName;

    @Email(message = "Invalid reply-to email format")
    @Column(name = "reply_to_email", length = 200)
    private String replyToEmail;

    @Column(name = "smtp_host", length = 200)
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 200)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 500)
    private String smtpPassword;

    @Column(name = "use_tls", nullable = false)
    private Boolean useTls = true;

    @Column(name = "use_ssl", nullable = false)
    private Boolean useSsl = false;

    @Column(name = "email_signature", columnDefinition = "TEXT")
    private String emailSignature;

    @Column(name = "auto_send_quotes", nullable = false)
    private Boolean autoSendQuotes = false;

    @Column(name = "use_custom_smtp", nullable = false)
    private Boolean useCustomSmtp = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
