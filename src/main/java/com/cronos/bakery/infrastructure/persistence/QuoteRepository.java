package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.quote.Quote;
import com.cronos.bakery.domain.entity.quote.enums.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    Page<Quote> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Quote> findByUserAndStatus(User user, QuoteStatus status, Pageable pageable);

    Optional<Quote> findByShareToken(String shareToken);

    @Query("SELECT q FROM Quote q WHERE q.shareToken = :token AND q.shareExpiresAt > :now")
    Optional<Quote> findValidSharedQuote(@Param("token") String token, @Param("now") LocalDateTime now);

    @Query("SELECT q FROM Quote q WHERE q.user = :user AND " +
            "(LOWER(q.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(q.quoteNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Quote> searchByUser(@Param("user") User user, @Param("search") String search, Pageable pageable);

    @Query("SELECT q FROM Quote q WHERE q.status = :status AND q.validUntil < :now")
    List<Quote> findExpiredQuotes(@Param("status") QuoteStatus status, @Param("now") LocalDateTime now);
}