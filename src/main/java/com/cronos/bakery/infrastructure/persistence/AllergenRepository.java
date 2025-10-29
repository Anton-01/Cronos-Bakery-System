package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.Allergen;
import com.cronos.bakery.domain.entity.core.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {

    List<Allergen> findByIsSystemDefaultTrue();

    List<Allergen> findByUser(User user);

    @Query("SELECT a FROM Allergen a WHERE a.isSystemDefault = true OR a.user = :user")
    List<Allergen> findAllAvailableForUser(User user);
}
