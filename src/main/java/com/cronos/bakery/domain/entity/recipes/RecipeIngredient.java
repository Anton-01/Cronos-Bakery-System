package com.cronos.bakery.domain.entity.recipes;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.core.MeasurementUnit;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe_ingredients", indexes = {
        @Index(name = "idx_recipe_ingredient", columnList = "recipe_id, raw_material_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private MeasurementUnit unit;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_optional")
    @Builder.Default
    private Boolean isOptional = false;

    private String notes;

    // Substitutes
    @OneToMany(mappedBy = "originalIngredient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<IngredientSubstitute> substitutes = new HashSet<>();

    // Calculated cost at the time of recipe creation
    @Column(name = "cost_per_unit", precision = 15, scale = 6)
    private BigDecimal costPerUnit;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;
}
