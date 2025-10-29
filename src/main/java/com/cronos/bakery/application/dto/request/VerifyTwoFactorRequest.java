package com.cronos.bakery.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTwoFactorRequest {
    @NotNull(message = "Verification code is required")
    @Min(value = 100000, message = "Code must be 6 digits")
    @Max(value = 999999, message = "Code must be 6 digits")
    private Integer code;
}
