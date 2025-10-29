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
public class DeviceFingerprintResponse {

    private Long id;
    private String deviceName;
    private String browser;
    private String operatingSystem;
    private String deviceType;
    private String ipAddress;
    private String location;
    private Boolean isTrusted;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime trustedAt;
    private Integer loginCount;
}
