package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.*;
import com.cronos.bakery.infrastructure.exception.ResourceNotFoundException;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import com.cronos.bakery.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing user customization settings
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomizationService {

    private final BrandingSettingsRepository brandingRepository;
    private final EmailSettingsRepository emailSettingsRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final UserRepository userRepository;

    // Branding Settings

    @Cacheable(value = "brandingSettings", key = "#userId")
    public BrandingSettings getBrandingSettings(Long userId) {
        return brandingRepository.findByUserIdAndIsActiveTrue(userId)
            .orElseGet(() -> createDefaultBrandingSettings(userId));
    }

    @CacheEvict(value = "brandingSettings", key = "#userId")
    public BrandingSettings updateBrandingSettings(Long userId, BrandingSettings settings) {
        BrandingSettings existing = brandingRepository.findByUserId(userId)
            .orElseGet(() -> {
                BrandingSettings newSettings = new BrandingSettings();
                newSettings.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found")));
                return newSettings;
            });

        // Update fields
        existing.setBusinessName(settings.getBusinessName());
        existing.setLogoUrl(settings.getLogoUrl());
        existing.setLogoSmallUrl(settings.getLogoSmallUrl());
        existing.setPrimaryColor(settings.getPrimaryColor());
        existing.setSecondaryColor(settings.getSecondaryColor());
        existing.setAccentColor(settings.getAccentColor());
        existing.setTextColor(settings.getTextColor());
        existing.setBackgroundColor(settings.getBackgroundColor());
        existing.setFontFamily(settings.getFontFamily());
        existing.setFontSizeBase(settings.getFontSizeBase());
        existing.setHeaderFontFamily(settings.getHeaderFontFamily());
        existing.setCompanySlogan(settings.getCompanySlogan());
        existing.setFooterText(settings.getFooterText());
        existing.setWebsiteUrl(settings.getWebsiteUrl());
        existing.setPhone(settings.getPhone());
        existing.setEmail(settings.getEmail());
        existing.setAddress(settings.getAddress());
        existing.setTaxId(settings.getTaxId());
        existing.setIsActive(settings.getIsActive());

        log.info("Updating branding settings for user: {}", userId);
        return brandingRepository.save(existing);
    }

    private BrandingSettings createDefaultBrandingSettings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        BrandingSettings settings = BrandingSettings.builder()
            .user(user)
            .businessName(user.getUsername() + "'s Bakery")
            .primaryColor("#007bff")
            .secondaryColor("#6c757d")
            .accentColor("#28a745")
            .textColor("#212529")
            .backgroundColor("#ffffff")
            .fontFamily("Arial, sans-serif")
            .fontSizeBase(14)
            .headerFontFamily("Georgia, serif")
            .isActive(true)
            .build();

        log.info("Creating default branding settings for user: {}", userId);
        return brandingRepository.save(settings);
    }

    // Email Settings

    @Cacheable(value = "emailSettings", key = "#userId")
    public EmailSettings getEmailSettings(Long userId) {
        return emailSettingsRepository.findByUserIdAndIsActiveTrue(userId)
            .orElseGet(() -> createDefaultEmailSettings(userId));
    }

    @CacheEvict(value = "emailSettings", key = "#userId")
    public EmailSettings updateEmailSettings(Long userId, EmailSettings settings) {
        EmailSettings existing = emailSettingsRepository.findByUserId(userId)
            .orElseGet(() -> {
                EmailSettings newSettings = new EmailSettings();
                newSettings.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found")));
                return newSettings;
            });

        existing.setSenderEmail(settings.getSenderEmail());
        existing.setSenderName(settings.getSenderName());
        existing.setReplyToEmail(settings.getReplyToEmail());
        existing.setSmtpHost(settings.getSmtpHost());
        existing.setSmtpPort(settings.getSmtpPort());
        existing.setSmtpUsername(settings.getSmtpUsername());
        if (settings.getSmtpPassword() != null && !settings.getSmtpPassword().isEmpty()) {
            existing.setSmtpPassword(settings.getSmtpPassword()); // TODO: Encrypt
        }
        existing.setUseTls(settings.getUseTls());
        existing.setUseSsl(settings.getUseSsl());
        existing.setEmailSignature(settings.getEmailSignature());
        existing.setAutoSendQuotes(settings.getAutoSendQuotes());
        existing.setUseCustomSmtp(settings.getUseCustomSmtp());
        existing.setIsActive(settings.getIsActive());

        log.info("Updating email settings for user: {}", userId);
        return emailSettingsRepository.save(existing);
    }

    private EmailSettings createDefaultEmailSettings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        EmailSettings settings = EmailSettings.builder()
            .user(user)
            .senderName(user.getUsername())
            .senderEmail(user.getEmail())
            .replyToEmail(user.getEmail())
            .useTls(true)
            .useSsl(false)
            .autoSendQuotes(false)
            .useCustomSmtp(false)
            .isActive(true)
            .build();

        log.info("Creating default email settings for user: {}", userId);
        return emailSettingsRepository.save(settings);
    }

    // Notification Preferences

    @Cacheable(value = "notificationPreferences", key = "#userId")
    public NotificationPreferences getNotificationPreferences(Long userId) {
        return notificationPreferencesRepository.findByUserIdAndIsActiveTrue(userId)
            .orElseGet(() -> createDefaultNotificationPreferences(userId));
    }

    @CacheEvict(value = "notificationPreferences", key = "#userId")
    public NotificationPreferences updateNotificationPreferences(Long userId, NotificationPreferences prefs) {
        NotificationPreferences existing = notificationPreferencesRepository.findByUserId(userId)
            .orElseGet(() -> {
                NotificationPreferences newPrefs = new NotificationPreferences();
                newPrefs.setUser(userService.findById(userId));
                return newPrefs;
            });

        existing.setNotifyPriceChanges(prefs.getNotifyPriceChanges());
        existing.setPriceChangeThresholdPercent(prefs.getPriceChangeThresholdPercent());
        existing.setNotifyPriceIncreaseOnly(prefs.getNotifyPriceIncreaseOnly());
        existing.setNotifyLowStock(prefs.getNotifyLowStock());
        existing.setLowStockThresholdPercent(prefs.getLowStockThresholdPercent());
        existing.setNotifyQuoteViewed(prefs.getNotifyQuoteViewed());
        existing.setNotifyQuoteExpiring(prefs.getNotifyQuoteExpiring());
        existing.setQuoteExpiryNoticeHours(prefs.getQuoteExpiryNoticeHours());
        existing.setNotifyRecipeCostChange(prefs.getNotifyRecipeCostChange());
        existing.setRecipeCostChangeThresholdPercent(prefs.getRecipeCostChangeThresholdPercent());
        existing.setNotifyDailySummary(prefs.getNotifyDailySummary());
        existing.setNotifyWeeklyReport(prefs.getNotifyWeeklyReport());
        existing.setNotifyMonthlyReport(prefs.getNotifyMonthlyReport());
        existing.setEmailNotifications(prefs.getEmailNotifications());
        existing.setWebsocketNotifications(prefs.getWebsocketNotifications());
        existing.setQuietHoursStart(prefs.getQuietHoursStart());
        existing.setQuietHoursEnd(prefs.getQuietHoursEnd());
        existing.setIsActive(prefs.getIsActive());

        log.info("Updating notification preferences for user: {}", userId);
        return notificationPreferencesRepository.save(existing);
    }

    private NotificationPreferences createDefaultNotificationPreferences(Long userId) {
        NotificationPreferences prefs = NotificationPreferences.builder()
            .user(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found")))
            .notifyPriceChanges(true)
            .priceChangeThresholdPercent(5.0)
            .notifyPriceIncreaseOnly(false)
            .notifyLowStock(true)
            .lowStockThresholdPercent(20.0)
            .notifyQuoteViewed(true)
            .notifyQuoteExpiring(true)
            .quoteExpiryNoticeHours(24)
            .notifyRecipeCostChange(true)
            .recipeCostChangeThresholdPercent(10.0)
            .notifyDailySummary(false)
            .notifyWeeklyReport(false)
            .notifyMonthlyReport(true)
            .emailNotifications(true)
            .websocketNotifications(true)
            .isActive(true)
            .build();

        log.info("Creating default notification preferences for user: {}", userId);
        return notificationPreferencesRepository.save(prefs);
    }

    public boolean shouldNotifyUser(Long userId, String notificationType) {
        NotificationPreferences prefs = getNotificationPreferences(userId);

        return switch (notificationType) {
            case "PRICE_CHANGE" -> prefs.getNotifyPriceChanges() && prefs.getEmailNotifications();
            case "LOW_STOCK" -> prefs.getNotifyLowStock() && prefs.getEmailNotifications();
            case "QUOTE_VIEWED" -> prefs.getNotifyQuoteViewed();
            case "QUOTE_EXPIRING" -> prefs.getNotifyQuoteExpiring() && prefs.getEmailNotifications();
            case "RECIPE_COST_CHANGE" -> prefs.getNotifyRecipeCostChange() && prefs.getEmailNotifications();
            default -> false;
        };
    }
}
