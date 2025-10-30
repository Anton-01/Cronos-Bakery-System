package com.cronos.bakery.domain.entity.notification.enums;

/**
 * Enum for different types of email templates
 */
public enum EmailTemplateType {
    QUOTE_SENT("Quote Sent"),
    PRICE_CHANGE("Price Change Notification"),
    LOW_STOCK("Low Stock Alert"),
    DAILY_SUMMARY("Daily Summary"),
    WEEKLY_REPORT("Weekly Report"),
    MONTHLY_REPORT("Monthly Report"),
    QUOTE_VIEWED("Quote Viewed"),
    QUOTE_EXPIRING("Quote Expiring Soon"),
    RECIPE_COST_CHANGE("Recipe Cost Change"),
    WELCOME("Welcome Email"),
    PASSWORD_RESET("Password Reset"),
    CUSTOM("Custom Template");

    private final String displayName;

    EmailTemplateType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
