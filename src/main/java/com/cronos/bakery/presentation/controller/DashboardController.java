package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    @GetMapping
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication authentication) {

        // This would call multiple services to gather dashboard data
        DashboardResponse response = DashboardResponse.builder()
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
