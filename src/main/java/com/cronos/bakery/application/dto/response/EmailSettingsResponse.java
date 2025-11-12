package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmailSettingsResponse {
    private Long id;
    private String senderEmail;
    private String senderName;
    private String replyToEmail;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private Boolean useTls;
    private Boolean useSsl;
    private String emailSignature;
    private Boolean autoSendQuotes;
    private Boolean useCustomSmtp;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
