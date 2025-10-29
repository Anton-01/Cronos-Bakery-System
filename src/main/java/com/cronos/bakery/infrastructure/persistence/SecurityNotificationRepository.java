package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.SecurityNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface SecurityNotificationRepository extends JpaRepository<SecurityNotification, Long> {

    @Query("SELECT sn FROM SecurityNotification sn WHERE sn.user.id = :userId ORDER BY sn.createdAt DESC")
    Page<SecurityNotification> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT sn FROM SecurityNotification sn WHERE sn.user.id = :userId AND sn.isRead = false ORDER BY sn.createdAt DESC")
    List<SecurityNotification> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(sn) FROM SecurityNotification sn WHERE sn.user.id = :userId AND sn.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE SecurityNotification sn SET sn.isRead = true, sn.readAt = :readAt WHERE sn.id = :notificationId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("UPDATE SecurityNotification sn SET sn.isRead = true, sn.readAt = :readAt WHERE sn.user.id = :userId AND sn.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Query("SELECT sn FROM SecurityNotification sn WHERE sn.emailSent = false AND sn.severity IN ('WARNING', 'CRITICAL')")
    List<SecurityNotification> findPendingEmailNotifications();
}
