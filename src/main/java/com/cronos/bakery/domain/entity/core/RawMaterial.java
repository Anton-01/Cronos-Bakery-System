package com.cronos.bakery.domain.entity.core;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "raw_materials", indexes = {
        @Index(name = "idx_user_material", columnList = "user_id, name"),
        @Index(name = "idx_category", columnList = "category_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterial extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Purchase Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_unit_id", nullable = false)
    private MeasurementUnit purchaseUnit;

    @Column(name = "purchase_quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal purchaseQuantity;

    @Column(name = "unit_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "MXN";

    // Inventory Control
    @Column(name = "current_stock", precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "minimum_stock", precision = 15, scale = 4)
    private BigDecimal minimumStock;

    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;

    @Column(name = "last_price_update")
    private LocalDateTime lastPriceUpdate;

    // Allergens
    @ManyToMany
    @JoinTable(
            name = "raw_material_allergens",
            joinColumns = @JoinColumn(name = "raw_material_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    @Builder.Default
    private Set<Allergen> allergens = new HashSet<>();

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MaterialPriceHistory> priceHistory = new HashSet<>();

    @Column(name = "needs_recalculation")
    @Builder.Default
    private Boolean needsRecalculation = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}