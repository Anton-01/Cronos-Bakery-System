package com.cronos.bakery.domain.entity.core;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_price_history", indexes = {
        @Index(name = "idx_material_date", columnList = "raw_material_id, changed_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "previous_cost", precision = 15, scale = 2)
    private BigDecimal previousCost;

    @Column(name = "new_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal newCost;

    @Column(name = "change_percentage", precision = 5, scale = 2)
    private BigDecimal changePercentage;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by")
    private String changedBy;

    private String reason;
}