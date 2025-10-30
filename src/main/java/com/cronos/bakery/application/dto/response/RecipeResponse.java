package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.domain.entity.recipes.enums.RecipeStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class RecipeResponse {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private BigDecimal yieldQuantity;
    private String yieldUnit;
    private Integer preparationTimeMinutes;
    private Integer bakingTimeMinutes;
    private RecipeStatus status;
    private Boolean needsRecalculation;
    private Integer currentVersion;
    private List<RecipeIngredientResponse> ingredients;
    private BigDecimal estimatedCost;
    private String primaryImageUrl;
    private Set<String> allergens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
