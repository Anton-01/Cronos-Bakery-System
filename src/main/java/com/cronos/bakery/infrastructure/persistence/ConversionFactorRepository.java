package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.ConversionFactor;
import com.cronos.bakery.domain.entity.core.MeasurementUnit;
import com.cronos.bakery.domain.entity.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionFactorRepository extends JpaRepository<ConversionFactor, Long> {

    @Query("SELECT cf FROM ConversionFactor cf WHERE " +
            "cf.fromUnit = :fromUnit AND cf.toUnit = :toUnit AND " +
            "(cf.user = :user OR (cf.user IS NULL AND cf.isSystemDefault = true))")
    Optional<ConversionFactor> findConversion(
            @Param("fromUnit") MeasurementUnit fromUnit,
            @Param("toUnit") MeasurementUnit toUnit,
            @Param("user") User user
    );

    List<ConversionFactor> findByUser(User user);

    List<ConversionFactor> findByIsSystemDefaultTrue();

    @Query("SELECT cf FROM ConversionFactor cf WHERE cf.fromUnit = :unit OR cf.toUnit = :unit")
    List<ConversionFactor> findByUnit(@Param("unit") MeasurementUnit unit);
}
