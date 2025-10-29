package com.cronos.bakary.infrastructure.persistence;


import com.cronos.bakary.domain.entity.LoginHistory;
import com.cronos.bakary.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    Page<LoginHistory> findByUserOrderByLoginAtDesc(User user, Pageable pageable);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user = :user AND lh.loginAt BETWEEN :start AND :end ORDER BY lh.loginAt DESC")
    List<LoginHistory> findByUserAndDateRange(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.ipAddress = :ip AND lh.loginAt > :since")
    List<LoginHistory> findRecentLoginsByIp(@Param("ip") String ip, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.user = :user AND lh.successful = false AND lh.loginAt > :since")
    long countFailedLoginAttempts(@Param("user") User user, @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT lh.ipAddress FROM LoginHistory lh WHERE lh.user = :user AND lh.successful = true ORDER BY lh.loginAt DESC")
    List<String> findDistinctIpAddressesByUser(@Param("user") User user, Pageable pageable);
}
