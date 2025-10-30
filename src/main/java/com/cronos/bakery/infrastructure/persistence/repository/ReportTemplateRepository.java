package com.cronos.bakery.infrastructure.persistence.repository;

import com.cronos.bakery.domain.entity.customization.ReportTemplate;
import com.cronos.bakery.domain.entity.customization.enums.ReportType;
import com.cronos.bakery.domain.entity.customization.enums.TemplateLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findByUserIdAndIsActiveTrue(Long userId);

    List<ReportTemplate> findByUserIdAndReportTypeAndIsActiveTrue(Long userId, ReportType reportType);

    Optional<ReportTemplate> findByUserIdAndReportTypeAndLanguageAndIsDefaultTrueAndIsActiveTrue(
        Long userId, ReportType reportType, TemplateLanguage language
    );

    Optional<ReportTemplate> findByUserIdAndReportTypeAndIsDefaultTrueAndIsActiveTrue(
        Long userId, ReportType reportType
    );

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.user.id = :userId AND rt.reportType = :reportType AND rt.isActive = true ORDER BY rt.isDefault DESC, rt.createdAt DESC")
    List<ReportTemplate> findUserTemplatesByTypeOrderedByDefault(
        @Param("userId") Long userId,
        @Param("reportType") ReportType reportType
    );

    long countByUserIdAndIsActiveTrue(Long userId);
}
