package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.*;
import com.cronos.bakery.application.dto.response.DashboardResponse;
import com.cronos.bakery.application.dto.response.RawMaterialResponse;
import com.cronos.bakery.application.dto.response.StockAlertResponse;
import com.cronos.bakery.application.mapper.StockAlertMapper;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.inventory.StockAlert;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for dashboard operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final RawMaterialService rawMaterialService;
    private final StockAlertService stockAlertService;
    private final StockAlertMapper stockAlertMapper;

    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get inventory stats
        InventoryStats inventoryStats = getInventoryStats(username);

        // Get recipe stats (placeholder for now)
        RecipeStats recipeStats = RecipeStats.builder()
                .totalRecipes(0L)
                .activeRecipes(0L)
                .needsRecalculation(0L)
                .build();

        // Get quote stats (placeholder for now)
        QuoteStats quoteStats = QuoteStats.builder()
                .totalQuotes(0L)
                .pendingQuotes(0L)
                .monthlyRevenue(java.math.BigDecimal.ZERO)
                .currency(user.getDefaultCurrency())
                .build();

        // Get recent activities
        List<RecentActivity> recentActivities = getRecentActivities(username, 5);

        // Get price alerts (placeholder for now)
        List<PriceAlert> priceAlerts = new ArrayList<>();

        // Get low stock alerts
        List<LowStockAlert> lowStockAlerts = getLowStockAlertsByUsername(username);

        return DashboardResponse.builder()
                .inventoryStats(inventoryStats)
                .recipeStats(recipeStats)
                .quoteStats(quoteStats)
                .recentActivities(recentActivities)
                .priceAlerts(priceAlerts)
                .lowStockAlerts(lowStockAlerts)
                .build();
    }

    /**
     * Get low stock alerts for dashboard
     */
    @Transactional(readOnly = true)
    public List<StockAlertResponse> getLowStockAlerts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StockAlert> alerts = stockAlertService.getActiveAlerts(user.getId());

        return alerts.stream()
                .map(stockAlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get recent activities with limit
     */
    @Transactional(readOnly = true)
    public List<RecentActivity> getRecentActivities(String username, Integer limit) {
        // This is a placeholder implementation
        // In a real application, you would fetch this from an activity log table
        List<RecentActivity> activities = new ArrayList<>();

        // Add sample activity for materials
        List<RawMaterialResponse> materials = rawMaterialService.getLowStockItems(username);
        for (RawMaterialResponse material : materials) {
            if (activities.size() >= limit) break;
            activities.add(RecentActivity.builder()
                    .type("LOW_STOCK")
                    .description("Low stock alert for " + material.getName())
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }

        return activities;
    }

    private InventoryStats getInventoryStats(String username) {
        List<RawMaterialResponse> allMaterials = rawMaterialService.getLowStockItems(username);
        Long lowStockCount = (long) allMaterials.size();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return InventoryStats.builder()
                .totalMaterials(0L) // This should be fetched from the service
                .lowStockItems(lowStockCount)
                .totalInventoryValue(java.math.BigDecimal.ZERO)
                .currency(user.getDefaultCurrency())
                .build();
    }

    private List<LowStockAlert> getLowStockAlertsByUsername(String username) {
        List<RawMaterialResponse> lowStockMaterials = rawMaterialService.getLowStockItems(username);

        return lowStockMaterials.stream()
                .map(material -> LowStockAlert.builder()
                        .materialId(material.getId())
                        .materialName(material.getName())
                        .currentStock(material.getCurrentStock())
                        .minimumStock(material.getMinimumStock())
                        .unit(material.getPurchaseUnit())
                        .build())
                .collect(Collectors.toList());
    }
}
