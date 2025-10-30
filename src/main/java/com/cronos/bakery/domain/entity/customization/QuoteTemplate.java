package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.enums.TemplateFormat;
import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity for customizable quote templates
 */
@Entity
@Table(name = "quote_templates", indexes = {
    @Index(name = "idx_quote_template_user", columnList = "user_id"),
    @Index(name = "idx_quote_template_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Template name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 10)
    private TemplateLanguage language = TemplateLanguage.ES;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 20)
    private TemplateFormat format = TemplateFormat.PDF;

    @Column(name = "header_html", columnDefinition = "TEXT")
    private String headerHtml;

    @Column(name = "body_html", columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "footer_html", columnDefinition = "TEXT")
    private String footerHtml;

    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles;

    @Column(name = "show_logo", nullable = false)
    private Boolean showLogo = true;

    @Column(name = "show_item_images", nullable = false)
    private Boolean showItemImages = false;

    @Column(name = "show_allergens", nullable = false)
    private Boolean showAllergens = true;

    @Column(name = "show_tax_breakdown", nullable = false)
    private Boolean showTaxBreakdown = true;

    @Column(name = "show_payment_terms", nullable = false)
    private Boolean showPaymentTerms = true;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "signature_text", length = 500)
    private String signatureText;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Version
    private Long version;
}
