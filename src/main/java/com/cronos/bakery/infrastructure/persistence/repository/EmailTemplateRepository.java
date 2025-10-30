package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import com.cronos.bakery.domain.entity.notification.EmailTemplate;
import com.cronos.bakery.domain.entity.notification.enums.EmailTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    List<EmailTemplate> findByTemplateTypeAndLanguageAndIsActiveTrue(
        EmailTemplateType templateType, TemplateLanguage language
    );

    Optional<EmailTemplate> findByTemplateTypeAndLanguageAndIsSystemTemplateTrueAndIsActiveTrue(
        EmailTemplateType templateType, TemplateLanguage language
    );

    Optional<EmailTemplate> findByUserIdAndTemplateTypeAndLanguageAndIsActiveTrue(
        Long userId, EmailTemplateType templateType, TemplateLanguage language
    );

    List<EmailTemplate> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT et FROM EmailTemplate et WHERE " +
           "(et.user.id = :userId OR et.isSystemTemplate = true) AND " +
           "et.templateType = :type AND et.language = :language AND et.isActive = true " +
           "ORDER BY et.isSystemTemplate ASC, et.isDefault DESC")
    List<EmailTemplate> findBestMatchTemplate(
        @Param("userId") Long userId,
        @Param("type") EmailTemplateType type,
        @Param("language") TemplateLanguage language
    );

    @Query("SELECT et FROM EmailTemplate et WHERE et.isSystemTemplate = true AND et.isActive = true")
    List<EmailTemplate> findAllSystemTemplates();

    long countByUserIdAndIsActiveTrue(Long userId);
}
