package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.CreateRecipeRequest;
import com.cronos.bakery.application.dto.response.*;
import com.cronos.bakery.application.dto.response.RecipeStatisticsResponse;
import com.cronos.bakery.application.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Recipes", description = "Recipe management endpoints")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @Operation(summary = "Create new recipe")
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(@Valid @RequestBody CreateRecipeRequest request, Authentication authentication) {
        RecipeResponse response = recipeService.createRecipe(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update recipe")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(@PathVariable Long id, @Valid @RequestBody CreateRecipeRequest request, @RequestParam(required = false, defaultValue = "Updated") String changes, Authentication authentication) {
        RecipeResponse response = recipeService.updateRecipe(id, request, changes, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all recipes")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getUserRecipes(Authentication authentication, Pageable pageable) {
        Page<RecipeResponse> recipes = recipeService.getUserRecipes(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    @GetMapping("/search")
    @Operation(summary = "Search recipes")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> searchRecipes(@RequestParam String query, Authentication authentication, Pageable pageable) {
        Page<RecipeResponse> recipes = recipeService.searchRecipes(authentication.getName(), query, pageable);
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get recipes statistics")
    public ResponseEntity<ApiResponse<RecipeStatisticsResponse>> getStatistics(Authentication authentication) {
        RecipeStatisticsResponse statistics = recipeService.getStatistics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable Long id, Authentication authentication) {
        RecipeResponse response = recipeService.getRecipeById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/calculate-cost")
    @Operation(summary = "Calculate recipe cost with scale factor")
    public ResponseEntity<ApiResponse<RecipeCostResponse>> calculateRecipeCost(@PathVariable Long id, @RequestBody(required = false) RecipeScaleRequest request, Authentication authentication) {
        BigDecimal scaleFactor = request != null ? request.getScaleFactor() : BigDecimal.ONE;
        RecipeCostResponse response = recipeService.calculateRecipeCost(id, scaleFactor, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get recipe versions")
    public ResponseEntity<ApiResponse<List<RecipeVersionResponse>>> getRecipeVersions(@PathVariable Long id, Authentication authentication) {
        List<RecipeVersionResponse> versions = recipeService.getRecipeVersions(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    @PostMapping("/recalculate-material/{materialId}")
    @Operation(summary = "Recalculate costs for recipes using a material")
    public ResponseEntity<ApiResponse<Void>> recalculateCostsForMaterial(@PathVariable Long materialId, Authentication authentication) {
        recipeService.recalculateCostsForMaterial(materialId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Costs recalculated successfully", null));
    }
}
