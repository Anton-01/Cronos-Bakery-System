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

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name
}
