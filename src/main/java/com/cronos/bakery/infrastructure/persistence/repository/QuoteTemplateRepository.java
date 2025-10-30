package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.QuoteTemplate;
import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteTemplateRepository extends JpaRepository<QuoteTemplate, Long> {

    List<QuoteTemplate> findByUserIdAndIsActiveTrue(Long userId);

    List<QuoteTemplate> findByUserIdAndLanguageAndIsActiveTrue(Long userId, TemplateLanguage language);

    Optional<QuoteTemplate> findByUserIdAndIsDefaultTrueAndIsActiveTrue(Long userId);

    Optional<QuoteTemplate> findByUserIdAndLanguageAndIsDefaultTrueAndIsActiveTrue(
        Long userId, TemplateLanguage language
    );

    @Query("SELECT qt FROM QuoteTemplate qt WHERE qt.user.id = :userId AND qt.isActive = true ORDER BY qt.isDefault DESC, qt.createdAt DESC")
    List<QuoteTemplate> findUserTemplatesOrderedByDefault(@Param("userId") Long userId);

    long countByUserIdAndIsActiveTrue(Long userId);
}
