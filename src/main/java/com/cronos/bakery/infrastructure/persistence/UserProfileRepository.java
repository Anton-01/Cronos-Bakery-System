package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    Optional<UserProfile> findByUserId(@Param("userId") Long userId);

    @Query("SELECT up FROM UserProfile up WHERE up.user.username = :username")
    Optional<UserProfile> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(up) > 0 FROM UserProfile up WHERE up.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
