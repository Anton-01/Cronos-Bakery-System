package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.RecentActivity;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.DashboardResponse;
import com.cronos.bakery.application.dto.response.StockAlertResponse;
import com.cronos.bakery.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication authentication) {
        DashboardResponse response = dashboardService.getDashboardData(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Get low stock alerts")
    public ResponseEntity<ApiResponse<List<StockAlertResponse>>> getLowStockAlerts(Authentication authentication) {
        List<StockAlertResponse> alerts = dashboardService.getLowStockAlerts(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/activity")
    @Operation(summary = "Get recent activities")
    public ResponseEntity<ApiResponse<List<RecentActivity>>> getRecentActivities(
            Authentication authentication,
            @Parameter(description = "Maximum number of activities to return", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        List<RecentActivity> activities = dashboardService.getRecentActivities(authentication.getName(), limit);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
}
