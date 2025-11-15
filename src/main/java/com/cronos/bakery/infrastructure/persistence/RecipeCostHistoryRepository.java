package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.domain.entity.recipes.RecipeCostHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeCostHistoryRepository extends JpaRepository<RecipeCostHistory, Long> {

    Page<RecipeCostHistory> findByRecipeOrderByCalculatedAtDesc(Recipe recipe, Pageable pageable);

    @Query("SELECT rch FROM RecipeCostHistory rch WHERE rch.recipe = :recipe " +
            "ORDER BY rch.calculatedAt DESC LIMIT 1")
    Optional<RecipeCostHistory> findLatestByRecipe(@Param("recipe") Recipe recipe);

    List<RecipeCostHistory> findByRecipeAndCalculatedAtBetween(
            Recipe recipe,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT AVG(rch.totalCost) FROM RecipeCostHistory rch " +
            "WHERE rch.recipe.user.id = :userId " +
            "AND rch.id IN (SELECT MAX(rch2.id) FROM RecipeCostHistory rch2 GROUP BY rch2.recipe.id)")
    java.math.BigDecimal calculateAverageCostPerRecipeByUser(@Param("userId") Long userId);
}
