package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.inventory.StockAlert;
import com.cronos.bakery.domain.entity.inventory.enums.AlertStatus;
import com.cronos.bakery.domain.entity.inventory.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    List<StockAlert> findByUserIdAndStatus(Long userId, AlertStatus status);

    Page<StockAlert> findByUserIdOrderByTriggeredAtDesc(Long userId, Pageable pageable);

    List<StockAlert> findByUserIdAndStatusOrderByTriggeredAtDesc(Long userId, AlertStatus status);

    Optional<StockAlert> findByRawMaterialIdAndStatus(Long rawMaterialId, AlertStatus status);

    List<StockAlert> findByRawMaterialIdAndStatusIn(Long rawMaterialId, List<AlertStatus> statuses);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.user.id = :userId AND sa.status = :status AND sa.alertType IN :types ORDER BY sa.triggeredAt DESC")
    List<StockAlert> findByUserAndStatusAndTypeIn(
        @Param("userId") Long userId,
        @Param("status") AlertStatus status,
        @Param("types") List<AlertType> types
    );

    @Query("SELECT COUNT(sa) FROM StockAlert sa WHERE sa.user.id = :userId AND sa.status = 'ACTIVE'")
    long countActiveAlertsByUser(@Param("userId") Long userId);

    @Query("SELECT sa FROM StockAlert sa WHERE sa.status = 'ACTIVE' AND sa.emailSent = false")
    List<StockAlert> findPendingEmailAlerts();

    @Query("SELECT sa FROM StockAlert sa WHERE sa.status = 'ACTIVE' AND sa.triggeredAt < :cutoffTime")
    List<StockAlert> findStaleActiveAlerts(@Param("cutoffTime") LocalDateTime cutoffTime);

    void deleteByUserIdAndStatusAndTriggeredAtBefore(Long userId, AlertStatus status, LocalDateTime before);
}
