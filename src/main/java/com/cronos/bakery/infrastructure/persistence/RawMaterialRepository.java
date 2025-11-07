package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.Category;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Page<RawMaterial> findByUser(User user, Pageable pageable);

    Page<RawMaterial> findByUserAndCategory(User user, Category category, Pageable pageable);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.user = :user AND " +
            "(LOWER(rm.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(rm.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(rm.supplier) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RawMaterial> searchByUser(@Param("user") User user, @Param("search") String search, Pageable pageable);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.user = :user AND " +
            "rm.currentStock < rm.minimumStock")
    List<RawMaterial> findLowStockItems(@Param("user") User user);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.needsRecalculation = true AND rm.user = :user")
    List<RawMaterial> findMaterialsNeedingRecalculation(@Param("user") User user);

    @Query("SELECT COUNT(rm) FROM RawMaterial rm WHERE rm.user = :user AND rm.currentStock < rm.minimumStock")
    long countByUserAndCurrentStockLessThanMinimumStock(@Param("user") User user);
}
