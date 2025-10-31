package com.cronos.bakery.domain.entity.tax;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity for tax rates by region (IVA/VAT management)
 */
@Entity
@Table(name = "tax_rates", indexes = {
    @Index(name = "idx_tax_rate_user", columnList = "user_id"),
    @Index(name = "idx_tax_rate_region", columnList = "region_code"),
    @Index(name = "idx_tax_rate_active", columnList = "is_active,effective_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxRate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Country code is required")
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode; // ISO 3166-1 alpha-2

    @Column(name = "region_code", length = 10)
    private String regionCode; // State/Province code

    @NotBlank(message = "Region name is required")
    @Column(name = "region_name", nullable = false, length = 200)
    private String regionName;

    @NotBlank(message = "Tax name is required")
    @Column(name = "tax_name", nullable = false, length = 100)
    private String taxName; // e.g., "IVA", "VAT", "GST"

    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100%")
    @Column(name = "tax_rate_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRatePercent;

    @Column(name = "reduced_rate_percent", precision = 5, scale = 2)
    private BigDecimal reducedRatePercent; // For special items

    @Column(name = "super_reduced_rate_percent", precision = 5, scale = 2)
    private BigDecimal superReducedRatePercent; // For essential items

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate = LocalDate.now();

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "applies_to_food", nullable = false)
    private Boolean appliesToFood = true;

    @Column(name = "tax_id_required", nullable = false)
    private Boolean taxIdRequired = false;

    @Version
    private Long version;
}
