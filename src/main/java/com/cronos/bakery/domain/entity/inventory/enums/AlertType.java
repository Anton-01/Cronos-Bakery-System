package com.cronos.bakery.domain.entity.inventory.enums;

/**
 * Enum for different types of inventory alerts
 */
public enum AlertType {
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock"),
    REORDER_POINT("Reorder Point Reached"),
    EXPIRING_SOON("Expiring Soon"),
    EXPIRED("Expired");

    private final String displayName;

    AlertType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
