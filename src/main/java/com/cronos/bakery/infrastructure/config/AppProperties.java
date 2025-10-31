package com.cronos.bakery.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application-wide configuration properties
 * Maps to the 'app' section in application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    /**
     * Base URL of the application (e.g., https://cronos-bakery.com)
     * Used for generating links in emails, shared quotes, etc.
     * Defaults to http://localhost:8080 for development
     */
    private String baseUrl = "http://localhost:8080";

    private Upload upload = new Upload();

    @Getter
    @Setter
    public static class Upload {
        private String profilePictures = "uploads/profile-pictures";
        private String coverPictures = "uploads/cover-pictures";
        private Long maxProfilePictureSize = 5242880L; // 5MB
        private Long maxCoverPictureSize = 10485760L;  // 10MB
    }
}
