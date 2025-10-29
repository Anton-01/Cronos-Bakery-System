package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.MaterialPriceHistory;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaterialPriceHistoryRepository extends JpaRepository<MaterialPriceHistory, Long> {

    Page<MaterialPriceHistory> findByRawMaterialOrderByChangedAtDesc(RawMaterial rawMaterial, Pageable pageable);

    List<MaterialPriceHistory> findByRawMaterialAndChangedAtBetween(
            RawMaterial rawMaterial,
            LocalDateTime start,
            LocalDateTime end
    );
}
