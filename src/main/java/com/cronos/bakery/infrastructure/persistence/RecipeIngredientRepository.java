package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.recipes.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
}
