package com.cronos.bakery.domain.entity.quote;

import com.cronos.bakery.domain.entity.core.Category;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.recipes.*;
import com.cronos.bakery.domain.entity.recipes.enums.RecipeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quote_access_logs", indexes = {
        @Index(name = "idx_quote_access", columnList = "quote_id, accessed_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "accessed_by_email")
    private String accessedByEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Yield Information
    @Column(name = "yield_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal yieldQuantity;

    @Column(name = "yield_unit", nullable = false)
    private String yieldUnit; // pieces, servings, kg, etc.

    @Column(name = "preparation_time_minutes")
    private Integer preparationTimeMinutes;

    @Column(name = "baking_time_minutes")
    private Integer bakingTimeMinutes;

    @Column(name = "cooling_time_minutes")
    private Integer coolingTimeMinutes;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RecipeStatus status = RecipeStatus.DRAFT;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "needs_recalculation")
    @Builder.Default
    private Boolean needsRecalculation = false;

    // Current Version
    @Column(name = "current_version")
    @Builder.Default
    private Integer currentVersion = 1;

    // Ingredients
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    // Sub-recipes
    @OneToMany(mappedBy = "parentRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeSubRecipe> subRecipes = new HashSet<>();

    // Fixed Costs (gas, electricity, etc.)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeFixedCost> fixedCosts = new HashSet<>();

    // Files and Images
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RecipeFile> files = new HashSet<>();

    // Cost History
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<RecipeCostHistory> costHistory = new HashSet<>();

    // Version History
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<RecipeVersion> versions = new HashSet<>();

    // Allergens (computed from ingredients)
    @ManyToMany
    @JoinTable(
            name = "recipe_allergens",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    @Builder.Default
    private Set<Allergen> allergens = new HashSet<>();

    // Instructions
    @Column(length = 5000)
    private String instructions;

    @Column(name = "storage_instructions", length = 1000)
    private String storageInstructions;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;
}