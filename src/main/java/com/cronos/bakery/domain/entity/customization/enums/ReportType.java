package com.cronos.bakery.domain.entity.customization.enums;

/**
 * Enum for different types of reports
 */
public enum ReportType {
    COST_ANALYSIS("Cost Analysis Report"),
    PROFITABILITY("Profitability Analysis"),
    INVENTORY("Inventory Status Report"),
    PRICE_HISTORY("Price History Report"),
    RECIPE_COSTS("Recipe Costs Report"),
    BREAKEVEN_ANALYSIS("Break-Even Analysis"),
    MONTHLY_SUMMARY("Monthly Summary Report"),
    SALES_QUOTES("Sales and Quotes Report"),
    TAX_REPORT("Tax Report"),
    CUSTOM("Custom Report");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
