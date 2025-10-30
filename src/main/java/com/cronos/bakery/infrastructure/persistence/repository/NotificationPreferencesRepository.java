package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserId(Long userId);

    Optional<NotificationPreferences> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT np FROM NotificationPreferences np WHERE np.notifyPriceChanges = true AND np.emailNotifications = true AND np.isActive = true")
    List<NotificationPreferences> findUsersWithPriceChangeNotificationsEnabled();

    @Query("SELECT np FROM NotificationPreferences np WHERE np.notifyLowStock = true AND np.emailNotifications = true AND np.isActive = true")
    List<NotificationPreferences> findUsersWithLowStockNotificationsEnabled();

    @Query("SELECT np FROM NotificationPreferences np WHERE np.notifyQuoteViewed = true AND np.isActive = true")
    List<NotificationPreferences> findUsersWithQuoteViewedNotificationsEnabled();

    boolean existsByUserId(Long userId);
}
