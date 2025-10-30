package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.BrandingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandingSettingsRepository extends JpaRepository<BrandingSettings, Long> {

    Optional<BrandingSettings> findByUserId(Long userId);

    Optional<BrandingSettings> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserId(Long userId);
}
