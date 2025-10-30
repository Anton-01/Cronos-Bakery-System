package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.EmailSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailSettingsRepository extends JpaRepository<EmailSettings, Long> {

    Optional<EmailSettings> findByUserId(Long userId);

    Optional<EmailSettings> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserId(Long userId);
}
