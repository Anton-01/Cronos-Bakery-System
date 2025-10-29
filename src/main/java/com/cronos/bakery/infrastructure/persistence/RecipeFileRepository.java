package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.application.service.enums.FileType;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.domain.entity.recipes.RecipeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeFileRepository extends JpaRepository<RecipeFile, Long> {

    List<RecipeFile> findByRecipe(Recipe recipe);

    List<RecipeFile> findByRecipeAndFileType(Recipe recipe, FileType fileType);

    Optional<RecipeFile> findByRecipeAndIsPrimaryTrue(Recipe recipe);
}
