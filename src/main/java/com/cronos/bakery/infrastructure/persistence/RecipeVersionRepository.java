package com.cronos.bakery.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecipeVersionRepository extends JpaRepository<RecipeVersion, Long> {

    List<RecipeVersion> findByRecipeOrderByVersionNumberDesc(Recipe recipe);

    Optional<RecipeVersion> findByRecipeAndVersionNumber(Recipe recipe, Integer versionNumber);

    @Query("SELECT rv FROM RecipeVersion rv WHERE rv.recipe = :recipe AND rv.isCurrent = true")
    Optional<RecipeVersion> findCurrentVersion(@Param("recipe") Recipe recipe);
}