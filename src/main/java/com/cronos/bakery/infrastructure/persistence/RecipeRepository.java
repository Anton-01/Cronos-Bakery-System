package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.domain.entity.recipes.enums.RecipeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findByUser(User user, Pageable pageable);

    Page<Recipe> findByUserAndStatus(User user, RecipeStatus status, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.user = :user AND r.isActive = true")
    Page<Recipe> findActiveByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.user = :user AND " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Recipe> searchByUser(@Param("user") User user, @Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.ingredients ri WHERE ri.rawMaterial.id = :materialId")
    List<Recipe> findRecipesUsingMaterial(@Param("materialId") Long materialId);

    @Query("SELECT r FROM Recipe r WHERE r.needsRecalculation = true AND r.user = :user")
    List<Recipe> findRecipesNeedingRecalculation(@Param("user") User user);

    long countByUser(User user);

    long countByUserAndStatus(User user, RecipeStatus status);

    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.user = :user AND r.needsRecalculation = true")
    long countByUserAndNeedsRecalculation(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT r.category.id) FROM Recipe r WHERE r.user = :user AND r.category IS NOT NULL")
    long countDistinctCategoriesByUser(@Param("user") User user);

    @Query("SELECT COUNT(ri) FROM RecipeIngredient ri WHERE ri.recipe.user = :user")
    long countTotalIngredientsByUser(@Param("user") User user);
}
