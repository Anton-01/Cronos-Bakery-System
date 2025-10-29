package com.cronos.bakery.presentation.controller;


import com.cronos.bakery.application.dto.request.CreateCategoryRequest;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.CategoryResponse;
import com.cronos.bakery.application.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request, Authentication authentication) {

        CategoryResponse response = categoryService.createCategory(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getUserCategories(
            Authentication authentication) {

        List<CategoryResponse> categories = categoryService.getUserCategories(
                authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/system")
    @Operation(summary = "Get system categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSystemCategories() {
        List<CategoryResponse> categories = categoryService.getSystemCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
