package com.cronos.bakery.domain.entity.inventory.enums;

/**
 * Enum for alert status
 */
public enum AlertStatus {
    ACTIVE("Active"),
    ACKNOWLEDGED("Acknowledged"),
    RESOLVED("Resolved"),
    DISMISSED("Dismissed");

    private final String displayName;

    AlertStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
