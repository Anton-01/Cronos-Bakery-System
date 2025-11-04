package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.NotificationPreferences;
import com.cronos.bakery.domain.entity.inventory.StockAlert;
import com.cronos.bakery.domain.entity.inventory.enums.AlertStatus;
import com.cronos.bakery.domain.entity.inventory.enums.AlertType;
import com.cronos.bakery.infrastructure.persistence.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.cronos.bakery.infrastructure.util.PercentageUtils.calculatePercentage;

/**
 * Service for managing stock alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final CustomizationService customizationService;
    private final RawMaterialService rawMaterialService;

    /**
     * Check and create stock alerts for a material
     */
    public void checkAndCreateAlert(Long userId, RawMaterial material) {
        NotificationPreferences prefs = customizationService.getNotificationPreferences(userId);

        if (!prefs.getNotifyLowStock()) {
            return;
        }

        BigDecimal currentQuantity = material.getCurrentStock();
        BigDecimal minQuantity = material.getMinimumStock();

        if (minQuantity == null || minQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // Check if already has active alert
        if (stockAlertRepository.findByRawMaterialIdAndStatus(material.getId(), AlertStatus.ACTIVE).isPresent()) {
            return;
        }

        AlertType alertType = null;
        String message = null;

        if (currentQuantity.compareTo(BigDecimal.ZERO) == 0) {
            alertType = AlertType.OUT_OF_STOCK;
            message = String.format("Out of stock: %s", material.getName());
        } else if (currentQuantity.compareTo(minQuantity) < 0) {
            alertType = AlertType.LOW_STOCK;
            double percentRemaining = currentQuantity.divide(minQuantity, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            message = String.format("Low stock alert: %s (%.0f%% remaining)",
                material.getName(), percentRemaining);
        } else if (currentQuantity.compareTo(minQuantity.multiply(BigDecimal.valueOf(1.2))) <= 0) {
            alertType = AlertType.REORDER_POINT;
            message = String.format("Reorder point reached: %s", material.getName());
        }

        if (alertType != null) {
            createAlert(userId, material, alertType, currentQuantity, minQuantity, message);
        }
    }

    private void createAlert(Long userId, RawMaterial material, AlertType alertType, BigDecimal currentQuantity, BigDecimal thresholdQuantity, String message) {

        StockAlert alert = StockAlert.builder()
            .user(material.getUser())
            .rawMaterial(material)
            .alertType(alertType)
            .currentQuantity(currentQuantity)
            .thresholdQuantity(thresholdQuantity)
            .thresholdPercent(calculatePercentage(currentQuantity, thresholdQuantity))
            .message(message)
            .status(AlertStatus.ACTIVE)
            .triggeredAt(LocalDateTime.now())
            .emailSent(false)
            .autoResolved(false)
            .build();

        stockAlertRepository.save(alert);
        log.info("Created {} alert for material: {} (user: {})", alertType, material.getName(), userId);
    }

    /**
     * Get active alerts for user
     */
    @Transactional(readOnly = true)
    public List<StockAlert> getActiveAlerts(Long userId) {
        return stockAlertRepository.findByUserIdAndStatusOrderByTriggeredAtDesc(userId, AlertStatus.ACTIVE);
    }

    /**
     * Get all alerts for user with pagination
     */
    @Transactional(readOnly = true)
    public Page<StockAlert> getUserAlerts(Long userId, Pageable pageable) {
        return stockAlertRepository.findByUserIdOrderByTriggeredAtDesc(userId, pageable);
    }

    /**
     * Get count of active alerts
     */
    @Transactional(readOnly = true)
    public long countActiveAlerts(Long userId) {
        return stockAlertRepository.countActiveAlertsByUser(userId);
    }

    /**
     * Acknowledge alert
     */
    public void acknowledgeAlert(Long alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);

        log.info("Alert {} acknowledged", alertId);
    }

    /**
     * Resolve alert
     */
    public void resolveAlert(Long alertId, boolean autoResolved) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setAutoResolved(autoResolved);
        stockAlertRepository.save(alert);

        log.info("Alert {} resolved (auto: {})", alertId, autoResolved);
    }

    /**
     * Auto-resolve alerts when stock is replenished
     */
    public void autoResolveAlertsForMaterial(Long materialId) {
        List<StockAlert> activeAlerts = stockAlertRepository.findByRawMaterialIdAndStatusIn(
            materialId, List.of(AlertStatus.ACTIVE, AlertStatus.ACKNOWLEDGED)
        );

        for (StockAlert alert : activeAlerts) {
            resolveAlert(alert.getId(), true);
        }
    }

    /**
     * Dismiss alert
     */
    public void dismissAlert(Long alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setStatus(AlertStatus.DISMISSED);
        stockAlertRepository.save(alert);

        log.info("Alert {} dismissed", alertId);
    }

    /**
     * Clean up old resolved alerts
     */
    @Async
    public void cleanupOldAlerts(Long userId, int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        stockAlertRepository.deleteByUserIdAndStatusAndTriggeredAtBefore(
            userId, AlertStatus.RESOLVED, cutoff
        );
        log.info("Cleaned up old alerts for user: {}", userId);
    }

    /**
     * Get pending email alerts (for scheduled job)
     */
    @Transactional(readOnly = true)
    public List<StockAlert> getPendingEmailAlerts() {
        return stockAlertRepository.findPendingEmailAlerts();
    }

    /**
     * Mark alert email as sent
     */
    public void markEmailSent(Long alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setEmailSent(true);
        alert.setEmailSentAt(LocalDateTime.now());
        stockAlertRepository.save(alert);
    }
}
