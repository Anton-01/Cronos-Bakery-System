package com.cronos.bakery.domain.service;

import com.cronos.bakery.domain.entity.core.ConversionFactor;
import com.cronos.bakery.domain.entity.core.MeasurementUnit;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.exception.ConversionNotFoundException;
import com.cronos.bakery.infrastructure.persistence.ConversionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitConversionService {

    private final ConversionFactorRepository conversionFactorRepository;

    /**
     * Converts a quantity from one unit to another
     */
    public BigDecimal convert(BigDecimal quantity, MeasurementUnit fromUnit, MeasurementUnit toUnit, User user) {

        if (fromUnit.equals(toUnit)) {
            return quantity;
        }

        // Try direct conversion
        Optional<ConversionFactor> directConversion = conversionFactorRepository.findConversion(fromUnit, toUnit, user);

        if (directConversion.isPresent()) {
            return quantity.multiply(directConversion.get().getFactor())
                    .setScale(6, RoundingMode.HALF_UP);
        }

        // Try inverse conversion
        Optional<ConversionFactor> inverseConversion = conversionFactorRepository.findConversion(toUnit, fromUnit, user);

        if (inverseConversion.isPresent()) {
            return quantity.divide(inverseConversion.get().getFactor(), 6, RoundingMode.HALF_UP);
        }

        // Try indirect conversion through common unit
        Optional<BigDecimal> indirectResult = findIndirectConversion(quantity, fromUnit, toUnit, user);
        if (indirectResult.isPresent()) {
            return indirectResult.get();
        }

        throw new ConversionNotFoundException(
                String.format("No conversion found from %s to %s", fromUnit.getCode(), toUnit.getCode())
        );
    }

    /**
     * Finds indirect conversion path through intermediate units
     */
    private Optional<BigDecimal> findIndirectConversion(BigDecimal quantity,
                                                        MeasurementUnit fromUnit,
                                                        MeasurementUnit toUnit,
                                                        User user) {

        // Get all conversions for both units
        List<ConversionFactor> fromConversions = conversionFactorRepository.findByUnit(fromUnit);
        List<ConversionFactor> toConversions = conversionFactorRepository.findByUnit(toUnit);

        // Find common intermediate unit
        for (ConversionFactor fromConv : fromConversions) {
            MeasurementUnit intermediate = fromConv.getFromUnit().equals(fromUnit) ?
                    fromConv.getToUnit() : fromConv.getFromUnit();

            for (ConversionFactor toConv : toConversions) {
                MeasurementUnit toIntermediate = toConv.getFromUnit().equals(toUnit) ?
                        toConv.getToUnit() : toConv.getFromUnit();

                if (intermediate.equals(toIntermediate)) {
                    // Found path: fromUnit -> intermediate -> toUnit
                    BigDecimal step1 = convert(quantity, fromUnit, intermediate, user);
                    return Optional.of(convert(step1, intermediate, toUnit, user));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Gets all possible conversion targets for a given unit
     */
    public List<MeasurementUnit> getConversionTargets(MeasurementUnit unit, User user) {
        List<ConversionFactor> conversions = conversionFactorRepository.findByUnit(unit);

        return conversions.stream()
                .map(cf -> cf.getFromUnit().equals(unit) ? cf.getToUnit() : cf.getFromUnit())
                .distinct()
                .toList();
    }
}
