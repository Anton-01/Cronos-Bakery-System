package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.CreateRawMaterialRequest;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.PriceHistoryResponse;
import com.cronos.bakery.application.dto.response.RawMaterialResponse;
import com.cronos.bakery.application.dto.response.RawMaterialStatisticsResponse;
import com.cronos.bakery.application.service.RawMaterialService;
import com.cronos.bakery.application.service.enums.StockOperation;
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
@RequestMapping("/raw-materials")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Raw Materials", description = "Raw material management endpoints")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @PostMapping
    @Operation(summary = "Create new raw material")
    public ResponseEntity<ApiResponse<RawMaterialResponse>> createRawMaterial(@Valid @RequestBody CreateRawMaterialRequest request, Authentication authentication) {
        RawMaterialResponse response = rawMaterialService.createRawMaterial(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Material created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update raw material")
    public ResponseEntity<ApiResponse<RawMaterialResponse>> updateRawMaterial(@PathVariable Long id, @Valid @RequestBody CreateRawMaterialRequest request, Authentication authentication) {
        RawMaterialResponse response = rawMaterialService.updateRawMaterial(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Material updated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all raw materials")
    public ResponseEntity<ApiResponse<Page<RawMaterialResponse>>> getUserMaterials(Authentication authentication, Pageable pageable) {
        Page<RawMaterialResponse> materials = rawMaterialService.getUserMaterials(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get materials by category")
    public ResponseEntity<ApiResponse<Page<RawMaterialResponse>>> getMaterialsByCategory(@PathVariable Long categoryId, Authentication authentication, Pageable pageable) {
        Page<RawMaterialResponse> materials = rawMaterialService.getMaterialsByCategory(authentication.getName(), categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/search")
    @Operation(summary = "Search raw materials")
    public ResponseEntity<ApiResponse<Page<RawMaterialResponse>>> searchMaterials(@RequestParam String query, Authentication authentication, Pageable pageable) {
        Page<RawMaterialResponse> materials = rawMaterialService.searchMaterials(authentication.getName(), query, pageable);
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get raw materials statistics")
    public ResponseEntity<ApiResponse<RawMaterialStatisticsResponse>> getStatistics(Authentication authentication) {
        RawMaterialStatisticsResponse statistics = rawMaterialService.getStatistics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get material by ID")
    public ResponseEntity<ApiResponse<RawMaterialResponse>> getMaterialById(@PathVariable Long id, Authentication authentication) {
        RawMaterialResponse response = rawMaterialService.getMaterialById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update material stock")
    public ResponseEntity<ApiResponse<RawMaterialResponse>> updateStock(@PathVariable Long id, @RequestParam BigDecimal quantity, @RequestParam StockOperation operation, Authentication authentication) {
        RawMaterialResponse response = rawMaterialService.updateStock(id, quantity, operation, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", response));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<ApiResponse<List<RawMaterialResponse>>> getLowStockItems(Authentication authentication) {
        List<RawMaterialResponse> materials = rawMaterialService.getLowStockItems(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/{id}/price-history")
    @Operation(summary = "Get price history")
    public ResponseEntity<ApiResponse<Page<PriceHistoryResponse>>> getPriceHistory(@PathVariable Long id, Authentication authentication, Pageable pageable) {
        Page<PriceHistoryResponse> history = rawMaterialService.getPriceHistory(id, authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete material")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id, Authentication authentication) {

        rawMaterialService.deleteMaterial(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Material deleted successfully", null));
    }
}
