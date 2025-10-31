package com.cronos.bakery.infrastructure.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GoogleAuthenticatorConfig {
    private final SecurityProperties securityProperties;

    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        com.warrenstrange.googleauth.GoogleAuthenticatorConfig config =
                new com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setWindowSize(securityProperties.getTwoFactor().getWindowSize())
                .build();

        return new GoogleAuthenticator(config);
    }
}
