package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.SecurityNotification;
import com.cronos.bakery.infrastructure.persistence.SecurityNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityNotificationService {

    private final SecurityNotificationRepository notificationRepository;

    /**
     * Gets paginated notifications for a user
     */
    public Page<SecurityNotification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    /**
     * Gets unread notifications for a user
     */
    public List<SecurityNotification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    /**
     * Gets count of unread notifications
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Marks a notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId, LocalDateTime.now());
        log.debug("Marked notification {} as read", notificationId);
    }

    /**
     * Marks all notifications as read for a user
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        int count = notificationRepository.markAllAsRead(userId, LocalDateTime.now());
        log.info("Marked {} notifications as read for user {}", count, userId);
        return count;
    }

    /**
     * Deletes a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        SecurityNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        notificationRepository.delete(notification);
        log.info("Deleted notification {} for user {}", notificationId, userId);
    }
}
