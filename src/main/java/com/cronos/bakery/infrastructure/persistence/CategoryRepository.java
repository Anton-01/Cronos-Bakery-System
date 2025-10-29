package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.Category;
import com.cronos.bakery.domain.entity.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    List<Category> findByUserOrIsSystemDefaultTrue(User user);

    @Query("SELECT c FROM Category c WHERE c.isSystemDefault = true")
    List<Category> findSystemCategories();

    Optional<Category> findByNameAndUser(String name, User user);

    boolean existsByNameAndUser(String name, User user);
}
