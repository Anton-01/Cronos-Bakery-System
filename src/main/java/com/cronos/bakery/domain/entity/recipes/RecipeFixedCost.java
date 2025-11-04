package com.cronos.bakery.domain.entity.recipes;

import com.cronos.bakery.application.service.enums.CostCalculationMethod;
import com.cronos.bakery.application.service.enums.FixedCostType;
import com.cronos.bakery.domain.entity.core.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_fixed_costs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeFixedCost extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedCostType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CostCalculationMethod calculationMethod;

    // For time-based calculation
    @Column(name = "time_in_minutes")
    private Integer timeInMinutes;

    // For percentage-based calculation
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
