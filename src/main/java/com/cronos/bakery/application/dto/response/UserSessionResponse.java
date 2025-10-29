package com.cronos.bakery.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {

    private Long id;
    private String deviceId;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private String device;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    private LocalDateTime expiresAt;
    private LocalDateTime terminatedAt;
    private String terminationReason;
    private Boolean isCurrent;
}
