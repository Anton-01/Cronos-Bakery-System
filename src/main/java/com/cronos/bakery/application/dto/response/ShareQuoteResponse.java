package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShareQuoteResponse {
    private String shareToken;
    private String shareUrl;
    private LocalDateTime expiresAt;
}
