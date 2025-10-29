package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.request.CreateConversionFactorRequest;
import com.cronos.bakery.application.dto.request.UnitConversionRequest;
import com.cronos.bakery.application.dto.response.ConversionFactorResponse;
import com.cronos.bakery.application.dto.response.MeasurementUnitResponse;
import com.cronos.bakery.application.dto.response.UnitConversionResponse;
import com.cronos.bakery.domain.entity.core.ConversionFactor;
import com.cronos.bakery.domain.entity.core.MeasurementUnit;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.service.UnitConversionService;
import com.cronos.bakery.infrastructure.persistence.ConversionFactorRepository;
import com.cronos.bakery.infrastructure.persistence.MeasurementUnitRepository;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionFactorService {

    private final ConversionFactorRepository conversionFactorRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final UserRepository userRepository;
    private final UnitConversionService unitConversionService;

    /**
     * Creates a custom conversion factor
     */
    @Transactional
    @CacheEvict(value = "conversions", allEntries = true)
    public ConversionFactorResponse createConversionFactor(CreateConversionFactorRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MeasurementUnit fromUnit = measurementUnitRepository.findById(request.getFromUnitId())
                .orElseThrow(() -> new RuntimeException("From unit not found"));

        MeasurementUnit toUnit = measurementUnitRepository.findById(request.getToUnitId())
                .orElseThrow(() -> new RuntimeException("To unit not found"));

        ConversionFactor conversionFactor = ConversionFactor.builder()
                .fromUnit(fromUnit)
                .toUnit(toUnit)
                .factor(request.getFactor())
                .isSystemDefault(false)
                .user(user)
                .notes(request.getNotes())
                .build();

        conversionFactor = conversionFactorRepository.save(conversionFactor);

        log.info("Conversion factor created: {} to {} by user: {}",
                fromUnit.getCode(), toUnit.getCode(), username);

        return mapToResponse(conversionFactor);
    }

    /**
     * Converts quantity between units
     */
    @Transactional(readOnly = true)
    public UnitConversionResponse convertUnits(UnitConversionRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MeasurementUnit fromUnit = measurementUnitRepository.findById(request.getFromUnitId())
                .orElseThrow(() -> new RuntimeException("From unit not found"));

        MeasurementUnit toUnit = measurementUnitRepository.findById(request.getToUnitId())
                .orElseThrow(() -> new RuntimeException("To unit not found"));

        BigDecimal converted = unitConversionService.convert(
                request.getQuantity(),
                fromUnit,
                toUnit,
                user
        );

        return UnitConversionResponse.builder()
                .originalQuantity(request.getQuantity())
                .fromUnit(fromUnit.getCode())
                .convertedQuantity(converted)
                .toUnit(toUnit.getCode())
                .build();
    }

    /**
     * Gets all available measurement units
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "units")
    public List<MeasurementUnitResponse> getAllUnits() {
        return measurementUnitRepository.findAll().stream()
                .map(this::mapUnitToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets conversion factors for a user
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "conversions", key = "#username")
    public List<ConversionFactorResponse> getUserConversions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ConversionFactor> systemConversions = conversionFactorRepository.findByIsSystemDefaultTrue();
        List<ConversionFactor> userConversions = conversionFactorRepository.findByUser(user);

        systemConversions.addAll(userConversions);

        return systemConversions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConversionFactorResponse mapToResponse(ConversionFactor factor) {
        return ConversionFactorResponse.builder()
                .id(factor.getId())
                .fromUnit(factor.getFromUnit().getCode())
                .toUnit(factor.getToUnit().getCode())
                .factor(factor.getFactor())
                .isSystemDefault(factor.getIsSystemDefault())
                .notes(factor.getNotes())
                .build();
    }

    private MeasurementUnitResponse mapUnitToResponse(MeasurementUnit unit) {
        return MeasurementUnitResponse.builder()
                .id(unit.getId())
                .code(unit.getCode())
                .name(unit.getName())
                .namePlural(unit.getNamePlural())
                .type(unit.getType().name())
                .isSystemDefault(unit.getIsSystemDefault())
                .build();
    }
}
