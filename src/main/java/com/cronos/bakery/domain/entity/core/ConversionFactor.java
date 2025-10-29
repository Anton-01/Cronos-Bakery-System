package com.cronos.bakery.domain.entity.core;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "conversion_factors", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_unit_id", "to_unit_id", "user_id"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionFactor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_unit_id", nullable = false)
    private MeasurementUnit fromUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_unit_id", nullable = false)
    private MeasurementUnit toUnit;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal factor;

    @Column(name = "is_system_default")
    @Builder.Default
    private Boolean isSystemDefault = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String notes;
}
