package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.UnitType;
import com.cronos.bakery.domain.entity.core.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {

    Optional<MeasurementUnit> findByCode(String code);

    List<MeasurementUnit> findByType(UnitType type);

    List<MeasurementUnit> findByIsSystemDefaultTrue();
}
