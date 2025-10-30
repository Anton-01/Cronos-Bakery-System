package com.cronos.bakery.domain.entity.customization.enums;

/**
 * Enum for template languages
 */
public enum TemplateLanguage {
    ES("Español"),
    EN("English");

    private final String displayName;

    TemplateLanguage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
