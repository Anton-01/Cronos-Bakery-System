package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.tax.TaxRate;
import com.cronos.bakery.infrastructure.persistence.repository.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for tax calculations by region
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaxCalculationService {

    private final TaxRateRepository taxRateRepository;

    /**
     * Get effective tax rate for a region
     */
    @Cacheable(value = "taxRates", key = "#countryCode + '_' + #regionCode")
    public TaxRate getEffectiveTaxRate(String countryCode, String regionCode) {
        return getEffectiveTaxRate(countryCode, regionCode, LocalDate.now());
    }

    /**
     * Get effective tax rate for a region on a specific date
     */
    public TaxRate getEffectiveTaxRate(String countryCode, String regionCode, LocalDate date) {
        List<TaxRate> rates = taxRateRepository.findEffectiveTaxRates(
            countryCode.toUpperCase(),
            regionCode != null ? regionCode.toUpperCase() : null,
            date
        );

        if (!rates.isEmpty()) {
            return rates.getFirst(); // Most specific match first
        }

        // Try system default
        return taxRateRepository.findSystemDefaultByCountry(countryCode.toUpperCase())
            .orElseThrow(() -> new RuntimeException(
                "No tax rate found for country: " + countryCode
            ));
    }

    /**
     * Get user's default tax rate
     */
    public Optional<TaxRate> getUserDefaultTaxRate(Long userId) {
        return taxRateRepository.findByUserIdAndIsDefaultTrueAndIsActiveTrue(userId);
    }

    /**
     * Calculate tax amount
     */
    public BigDecimal calculateTax(BigDecimal amount, TaxRate taxRate) {
        return calculateTax(amount, taxRate.getTaxRatePercent());
    }

    /**
     * Calculate tax amount from percentage
     */
    public BigDecimal calculateTax(BigDecimal amount, BigDecimal taxRatePercent) {
        if (amount == null || taxRatePercent == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = taxRatePercent
            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate amount with tax included
     */
    public BigDecimal calculateWithTax(BigDecimal amount, TaxRate taxRate) {
        BigDecimal tax = calculateTax(amount, taxRate);
        return amount.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate amount from tax-included price
     */
    public BigDecimal calculateWithoutTax(BigDecimal amountWithTax, TaxRate taxRate) {
        if (amountWithTax == null || taxRate == null) {
            return amountWithTax;
        }

        BigDecimal divisor = BigDecimal.valueOf(100).add(taxRate.getTaxRatePercent())
            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return amountWithTax.divide(divisor, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax breakdown
     */
    public TaxBreakdown calculateTaxBreakdown(BigDecimal amount, TaxRate taxRate) {
        BigDecimal taxAmount = calculateTax(amount, taxRate);
        BigDecimal totalAmount = amount.add(taxAmount);

        return TaxBreakdown.builder()
            .baseAmount(amount)
            .taxRate(taxRate.getTaxRatePercent() != null ? taxRate.getTaxRatePercent().doubleValue() : null)
            .taxName(taxRate.getTaxName())
            .taxAmount(taxAmount)
            .totalAmount(totalAmount)
            .regionName(taxRate.getRegionName())
            .countryCode(taxRate.getCountryCode())
            .build();
    }

    /**
     * Get all tax rates for a country
     */
    public List<TaxRate> getTaxRatesByCountry(String countryCode) {
        return taxRateRepository.findByCountryCodeAndIsActiveTrue(countryCode.toUpperCase());
    }

    /**
     * Get user's custom tax rates
     */
    public List<TaxRate> getUserTaxRates(Long userId) {
        return taxRateRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Create or update tax rate
     */
    @Transactional
    public TaxRate saveTaxRate(TaxRate taxRate) {
        log.info("Saving tax rate: {} for {}/{}", taxRate.getTaxName(), taxRate.getCountryCode(), taxRate.getRegionCode());
        return taxRateRepository.save(taxRate);
    }

    /**
     * DTO for tax breakdown
     */
    @lombok.Builder
    @lombok.Data
    public static class TaxBreakdown {
        private BigDecimal baseAmount;
        private Double taxRate;
        private String taxName;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private String regionName;
        private String countryCode;
    }
}
