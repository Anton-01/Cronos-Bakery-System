package com.cronos.bakery.application.dto.response;

import com.cronos.bakery.application.dto.*;
import lombok.*;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private InventoryStats inventoryStats;
    private RecipeStats recipeStats;
    private QuoteStats quoteStats;
    private List<RecentActivity> recentActivities;
    private List<PriceAlert> priceAlerts;
    private List<LowStockAlert> lowStockAlerts;
}
