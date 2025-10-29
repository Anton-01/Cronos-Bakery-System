package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionToken(String sessionToken);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true ORDER BY us.lastActivityAt DESC")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true")
    long countActiveSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId ORDER BY us.createdAt DESC")
    List<UserSession> findAllSessionsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.terminatedAt = :terminatedAt, us.terminationReason = :reason WHERE us.user.id = :userId AND us.isActive = true AND us.sessionToken != :currentSessionToken")
    int terminateOtherUserSessions(@Param("userId") Long userId, @Param("currentSessionToken") String currentSessionToken, @Param("terminatedAt") LocalDateTime terminatedAt, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.terminatedAt = :terminatedAt, us.terminationReason = :reason WHERE us.id = :sessionId")
    int terminateSession(@Param("sessionId") Long sessionId, @Param("terminatedAt") LocalDateTime terminatedAt, @Param("reason") String reason);

    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now AND us.isActive = true")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.terminatedAt = :now, us.terminationReason = 'Expired' WHERE us.expiresAt < :now AND us.isActive = true")
    int terminateExpiredSessions(@Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSession us WHERE us.user.id = :userId AND us.isActive = true AND us.deviceId = :deviceId")
    Optional<UserSession> findActiveSessionByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") String deviceId);
}
