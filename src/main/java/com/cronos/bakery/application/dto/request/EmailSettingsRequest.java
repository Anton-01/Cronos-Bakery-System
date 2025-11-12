package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailSettingsRequest {

    @Email(message = "Invalid sender email format")
    private String senderEmail;

    @NotBlank(message = "Sender name is required")
    private String senderName;

    @Email(message = "Invalid reply-to email format")
    private String replyToEmail;

    private String smtpHost;

    private Integer smtpPort;

    private String smtpUsername;

    private String smtpPassword;

    private Boolean useTls;

    private Boolean useSsl;

    private String emailSignature;

    private Boolean autoSendQuotes;

    private Boolean useCustomSmtp;

    private Boolean isActive;
}
