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
public class SecurityNotificationResponse {

    private Long id;
    private String type;
    private String title;
    private String message;
    private String severity;
    private String deviceName;
    private String ipAddress;
    private String location;
    private String browser;
    private String operatingSystem;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
