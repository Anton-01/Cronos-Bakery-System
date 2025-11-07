package com.cronos.bakery.infrastructure.constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Application-wide constants
 * For configurable values, use application.yml instead
 */
public final class ApplicationConstants {

    private ApplicationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== Quote Constants ====================

    /**
     * Default validity days for new quotes if not specified
     */
    public static final int DEFAULT_QUOTE_VALIDITY_DAYS = 30;

    /**
     * Number of hours a shared quote link remains valid
     */
    public static final int SHARE_LINK_EXPIRATION_HOURS = 72;

    /**
     * Number of days to look back for recent quote access logs
     */
    public static final int RECENT_ACCESS_DAYS = 7;

    // ==================== Session Constants ====================

    /**
     * Default session expiration in days
     */
    public static final int DEFAULT_SESSION_EXPIRATION_DAYS = 7;

    // ==================== Security Constants ====================

    /**
     * Length of "Bearer " prefix in JWT authorization header
     */
    public static final int JWT_BEARER_PREFIX_LENGTH = 7;

    // ==================== File Upload Constants ====================

    /**
     * Maximum file size for general uploads (10MB in bytes)
     */
    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    /**
     * Default thumbnail size for images
     */
    public static final int DEFAULT_THUMBNAIL_SIZE = 300;

    // ==================== Calculation Constants ====================

    /**
     * Default scale for percentage calculations
     */
    public static final int PERCENTAGE_CALCULATION_SCALE = 4;

    /**
     * Default scale for monetary calculations
     */
    public static final int MONETARY_CALCULATION_SCALE = 2;

    /**
     * Default rounding mode for calculations
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Default division scale for calculations
     */
    public static final int DEFAULT_DIVISION_SCALE = 4;


    /**
     * Default value for calculate percentages
     */
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * Value of 100 used in percentage calculations
     */
    public static final int PERCENTAGE_BASE = 100;

    // ==================== Default Colors ====================

    /**
     * Default primary color for branding
     */
    public static final String DEFAULT_PRIMARY_COLOR = "#007bff";

    /**
     * Default secondary color for branding
     */
    public static final String DEFAULT_SECONDARY_COLOR = "#6c757d";

    /**
     * Default accent color for branding
     */
    public static final String DEFAULT_ACCENT_COLOR = "#28a745";

    /**
     * Default text color for branding
     */
    public static final String DEFAULT_TEXT_COLOR = "#212529";

    /**
     * Default background color for branding
     */
    public static final String DEFAULT_BACKGROUND_COLOR = "#ffffff";

    /**
     * Default font family for branding
     */
    public static final String DEFAULT_FONT_FAMILY = "Arial, sans-serif";

    /**
     * Default header font family for branding
     */
    public static final String DEFAULT_HEADER_FONT_FAMILY = "Georgia, serif";

    /**
     * Default base font size
     */
    public static final int DEFAULT_FONT_SIZE = 14;

    // ==================== External API Constants ====================

    /**
     * Exchange rate API base URL
     */
    public static final String EXCHANGE_RATE_API_URL = "https://api.exchangerate-api.com/v4/latest/";

    // ==================== PDF Generation Constants ====================

    /**
     * Default table width percentage in PDF
     */
    public static final float PDF_TABLE_WIDTH_PERCENTAGE = 100f;

    // ==================== Cache Constants ====================

    /**
     * Maximum cache size for in-memory caches
     */
    public static final int MAX_CACHE_SIZE = 1000;

    // ==================== CORS Constants ====================

    /**
     * Default allowed origins for development
     */
    public static final String[] DEFAULT_ALLOWED_ORIGINS = {
        "http://localhost:3000", "http://localhost:4200"
    };

    /**
     * Default allowed methods http accepted
     */
    public static final String[] DEFAULT_ALLOWED_METHODS_HTTP = {
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    };

    /**
     * Default allowed methods http accepted
     */
    public static final String[] DEFAULT_ALLOWED_PUBLIC_URL = {
            "/auth/register", "/auth/login", "/auth/refresh",
            "/actuator/**", "/api-docs/**", "/swagger-ui/**",
            "/swagger-ui.html"
    };

    // ==================== Async Configuration Constants ====================

    /**
     * Default async executor queue capacity
     */
    public static final int ASYNC_EXECUTOR_QUEUE_CAPACITY = 100;

    // ==================== Notification Constants ====================

    /**
     * Default price change threshold percentage for notifications
     */
    public static final BigDecimal DEFAULT_PRICE_CHANGE_THRESHOLD = new BigDecimal("5.00");

    /**
     * Default low stock threshold percentage
     */
    public static final BigDecimal DEFAULT_LOW_STOCK_THRESHOLD = new BigDecimal("20.00");

    /**
     * Default quote expiry notice hours
     */
    public static final int DEFAULT_QUOTE_EXPIRY_NOTICE_HOURS = 24;

    /**
     * Default recipe cost change threshold percentage
     */
    public static final BigDecimal DEFAULT_RECIPE_COST_CHANGE_THRESHOLD = new BigDecimal("10.00");
}
