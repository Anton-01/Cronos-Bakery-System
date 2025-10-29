package com.cronos.bakery.domain.entity.quote;

import com.cronos.bakery.domain.entity.core.AuditableEntity;
import com.cronos.bakery.domain.entity.recipes.ProfitMargin;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "quote_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "scale_factor", precision = 10, scale = 4)
    @Builder.Default
    private BigDecimal scaleFactor = BigDecimal.ONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profit_margin_id")
    private ProfitMargin profitMargin;

    @Column(name = "unit_cost", nullable = false, precision = 15, scale = 6)
    private BigDecimal unitCost;

    @Column(name = "profit_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal profitPercentage;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    private String notes;

    @Column(name = "display_order")
    private Integer displayOrder;
}
