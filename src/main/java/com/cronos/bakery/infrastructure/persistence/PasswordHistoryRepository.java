package com.cronos.bakary.infrastructure.persistence;



import com.cronos.bakary.domain.entity.PasswordHistory;
import com.cronos.bakary.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.user = :user ORDER BY ph.changedAt DESC")
    List<PasswordHistory> findByUserOrderByChangedAtDesc(@Param("user") User user, Pageable pageable);

    @Query("SELECT ph.passwordHash FROM PasswordHistory ph WHERE ph.user = :user ORDER BY ph.changedAt DESC")
    List<String> findRecentPasswordHashes(@Param("user") User user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM PasswordHistory ph WHERE ph.user = :user AND ph.id NOT IN " + "(SELECT p.id FROM PasswordHistory p WHERE p.user = :user ORDER BY p.changedAt DESC LIMIT :keep)")
    void deleteOldPasswordHistory(@Param("user") User user, @Param("keep") int keep);
}

