package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.enums.ReportType;
import com.cronos.bakery.domain.entity.customization.enums.TemplateFormat;
import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity for customizable report templates
 */
@Entity
@Table(name = "report_templates", indexes = {
    @Index(name = "idx_report_template_user", columnList = "user_id"),
    @Index(name = "idx_report_template_type", columnList = "report_type"),
    @Index(name = "idx_report_template_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTemplate extends AuditableEntity {

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
    @Column(name = "report_type", nullable = false, length = 50)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 10)
    @Builder.Default
    private TemplateLanguage language = TemplateLanguage.ES;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 20)
    @Builder.Default
    private TemplateFormat format = TemplateFormat.PDF;

    @Column(name = "header_html", columnDefinition = "TEXT")
    private String headerHtml;

    @Column(name = "body_html", columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "footer_html", columnDefinition = "TEXT")
    private String footerHtml;

    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles;

    @Column(name = "include_charts", nullable = false)
    @Builder.Default
    private Boolean includeCharts = true;

    @Column(name = "include_summary", nullable = false)
    @Builder.Default
    private Boolean includeSummary = true;

    @Column(name = "include_detailed_breakdown", nullable = false)
    @Builder.Default
    private Boolean includeDetailedBreakdown = true;

    @Column(name = "show_logo", nullable = false)
    @Builder.Default
    private Boolean showLogo = true;

    @Column(name = "chart_color_scheme", length = 50)
    @Builder.Default
    private String chartColorScheme = "default";

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Version
    private Long version;
}
