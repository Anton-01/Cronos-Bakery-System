package com.cronos.bakery.infrastructure.persistence;

import com.cronos.bakery.domain.entity.quote.Quote;
import com.cronos.bakery.domain.entity.quote.QuoteAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteAccessLogRepository extends JpaRepository<QuoteAccessLog, Long> {

    List<QuoteAccessLog> findByQuoteOrderByAccessedAtDesc(Quote quote);

    @Query("SELECT qal FROM QuoteAccessLog qal WHERE qal.quote = :quote AND qal.accessedAt >= :since")
    List<QuoteAccessLog> findRecentAccessLogs(@Param("quote") Quote quote, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(qal) FROM QuoteAccessLog qal WHERE qal.quote = :quote")
    long countAccessesByQuote(@Param("quote") Quote quote);
}
