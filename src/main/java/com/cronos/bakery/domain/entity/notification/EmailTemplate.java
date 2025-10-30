package com.cronos.bakery.domain.entity.notification;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import com.cronos.bakery.domain.entity.notification.enums.EmailTemplateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity for bilingual email templates
 */
@Entity
@Table(name = "email_templates", indexes = {
    @Index(name = "idx_email_template_user", columnList = "user_id"),
    @Index(name = "idx_email_template_type", columnList = "template_type"),
    @Index(name = "idx_email_template_lang", columnList = "language"),
    @Index(name = "idx_email_template_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Template name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false, length = 50)
    private EmailTemplateType templateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 10)
    private TemplateLanguage language = TemplateLanguage.ES;

    @NotBlank(message = "Subject is required")
    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    @Column(name = "html_body", columnDefinition = "TEXT")
    private String htmlBody;

    @Column(name = "text_body", columnDefinition = "TEXT")
    private String textBody;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "variables_help", columnDefinition = "TEXT")
    private String variablesHelp; // JSON with available template variables

    @Column(name = "is_system_template", nullable = false)
    private Boolean isSystemTemplate = false;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Version
    private Long version;
}
