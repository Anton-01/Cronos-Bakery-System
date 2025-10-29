package com.cronos.bakery.domain.entity.recipes;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_cost_history", indexes = {
        @Index(name = "idx_recipe_cost_date", columnList = "recipe_id, calculated_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCostHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "recipe_version")
    private Integer recipeVersion;

    @Column(name = "materials_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal materialsCost;

    @Column(name = "fixed_costs", nullable = false, precision = 15, scale = 2)
    private BigDecimal fixedCosts;

    @Column(name = "sub_recipes_cost", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal subRecipesCost = BigDecimal.ZERO;

    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "cost_per_unit", nullable = false, precision = 15, scale = 6)
    private BigDecimal costPerUnit;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "calculated_by")
    private String calculatedBy;

    @Column(name = "calculation_notes", length = 1000)
    private String calculationNotes;
}
