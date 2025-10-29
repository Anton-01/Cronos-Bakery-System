package com.cronos.bakery.domain.entity.recipes;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_sub_recipes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSubRecipe extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_recipe_id", nullable = false)
    private Recipe parentRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_recipe_id", nullable = false)
    private Recipe subRecipe;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "display_order")
    private Integer displayOrder;

    private String notes;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;
}
