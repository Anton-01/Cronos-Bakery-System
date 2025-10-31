package com.cronos.bakery.domain.entity.recipes;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredient_substitutes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientSubstitute extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_ingredient_id", nullable = false)
    private RecipeIngredient originalIngredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_material_id", nullable = false)
    private com.cronos.bakery.domain.entity.core.RawMaterial substituteMaterial;

    @Column(name = "conversion_ratio", nullable = false, precision = 10, scale = 4)
    @Builder.Default
    private java.math.BigDecimal conversionRatio = java.math.BigDecimal.ONE;

    @Column(length = 50)
    private String reason;

    private String notes;
}
