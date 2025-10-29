package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuoteAccessStatsResponse {
    private Long quoteId;
    private Long totalAccesses;
    private List<AccessLogEntry> recentAccesses;
}
