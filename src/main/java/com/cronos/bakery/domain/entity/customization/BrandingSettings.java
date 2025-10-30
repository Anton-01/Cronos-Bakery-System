package com.cronos.bakery.domain.entity.customization;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Entity for user branding settings (logos, colors, fonts)
 */
@Entity
@Table(name = "branding_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandingSettings extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "business_name", length = 200)
    private String businessName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "logo_small_url", length = 500)
    private String logoSmallUrl;

    @NotBlank(message = "Primary color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid hex color format")
    @Column(name = "primary_color", nullable = false, length = 7)
    private String primaryColor = "#007bff";

    @NotBlank(message = "Secondary color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid hex color format")
    @Column(name = "secondary_color", nullable = false, length = 7)
    private String secondaryColor = "#6c757d";

    @NotBlank(message = "Accent color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid hex color format")
    @Column(name = "accent_color", nullable = false, length = 7)
    private String accentColor = "#28a745";

    @Column(name = "text_color", length = 7)
    private String textColor = "#212529";

    @Column(name = "background_color", length = 7)
    private String backgroundColor = "#ffffff";

    @Column(name = "font_family", length = 100)
    private String fontFamily = "Arial, sans-serif";

    @Column(name = "font_size_base")
    private Integer fontSizeBase = 14;

    @Column(name = "header_font_family", length = 100)
    private String headerFontFamily = "Georgia, serif";

    @Column(name = "company_slogan", length = 500)
    private String companySlogan;

    @Column(name = "footer_text", length = 1000)
    private String footerText;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
