package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.tax.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

    List<TaxRate> findByCountryCodeAndIsActiveTrue(String countryCode);

    Optional<TaxRate> findByCountryCodeAndRegionCodeAndIsActiveTrueAndIsDefaultTrue(
        String countryCode, String regionCode
    );

    @Query("SELECT tr FROM TaxRate tr WHERE tr.countryCode = :countryCode AND " +
           "(tr.regionCode = :regionCode OR tr.regionCode IS NULL) AND " +
           "tr.isActive = true AND tr.effectiveDate <= :date AND " +
           "(tr.expiryDate IS NULL OR tr.expiryDate >= :date) " +
           "ORDER BY tr.regionCode DESC NULLS LAST, tr.effectiveDate DESC")
    List<TaxRate> findEffectiveTaxRates(
        @Param("countryCode") String countryCode,
        @Param("regionCode") String regionCode,
        @Param("date") LocalDate date
    );

    Optional<TaxRate> findByUserIdAndIsDefaultTrueAndIsActiveTrue(Long userId);

    List<TaxRate> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT tr FROM TaxRate tr WHERE tr.user IS NULL AND tr.countryCode = :countryCode AND tr.isActive = true AND tr.isDefault = true")
    Optional<TaxRate> findSystemDefaultByCountry(@Param("countryCode") String countryCode);

    boolean existsByCountryCodeAndRegionCode(String countryCode, String regionCode);
}
