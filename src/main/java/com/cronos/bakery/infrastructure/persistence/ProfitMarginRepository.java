package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.recipes.ProfitMargin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfitMarginRepository extends JpaRepository<ProfitMargin, Long> {

    List<ProfitMargin> findByUserAndIsActiveTrue(User user);

    Optional<ProfitMargin> findByUserAndIsDefaultTrue(User user);

    Optional<ProfitMargin> findByUserAndName(User user, String name);
}
