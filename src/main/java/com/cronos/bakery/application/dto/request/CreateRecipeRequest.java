package com.cronos.bakery.application.dto.request;

import com.cronos.bakery.domain.entity.recipes.enums.RecipeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequest {

    @NotBlank(message = "Recipe name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    private Long categoryId;

    @NotNull(message = "Yield quantity is required")
    @DecimalMin(value = "0.1", message = "Yield must be greater than 0")
    private BigDecimal yieldQuantity;

    @NotBlank(message = "Yield unit is required")
    private String yieldUnit;

    private Integer preparationTimeMinutes;

    private Integer bakingTimeMinutes;

    private Integer coolingTimeMinutes;

    @Size(max = 5000)
    private String instructions;

    @Valid
    @NotEmpty(message = "At least one ingredient is required")
    private List<RecipeIngredientRequest> ingredients;

    @Valid
    private List<RecipeSubRecipeRequest> subRecipes;

    @Valid
    private List<RecipeFixedCostRequest> fixedCosts;
}
