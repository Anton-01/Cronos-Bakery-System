package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessLogEntry {
    private LocalDateTime accessedAt;
    private String ipAddress;
    private String userAgent;
}
