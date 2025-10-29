package com.cronos.bakery.application.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private String username;
    private String email;
    private List<String> roles;
    private Boolean requiresTwoFactor;
    private String message;
}
