package com.cronos.bakery.application.service;

import com.cronos.bakery.application.dto.request.LoginRequest;
import com.cronos.bakery.application.dto.response.LoginResponse;
import com.cronos.bakery.application.dto.request.RefreshTokenRequest;
import com.cronos.bakery.application.dto.response.TokenResponse;
import com.cronos.bakery.domain.entity.LoginHistory;
import com.cronos.bakery.domain.entity.RefreshToken;
import com.cronos.bakery.domain.entity.Role;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.service.AccountLockoutService;
import com.cronos.bakery.domain.service.TwoFactorAuthenticationService;
import com.cronos.bakery.infrastructure.exception.InvalidTokenException;
import com.cronos.bakery.infrastructure.exception.UserNotFoundException;
import com.cronos.bakery.infrastructure.persistence.LoginHistoryRepository;
import com.cronos.bakery.infrastructure.persistence.RefreshTokenRepository;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import com.cronos.bakery.infrastructure.security.JwtService;
import com.cronos.bakery.infrastructure.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountLockoutService lockoutService;
    private final TwoFactorAuthenticationService twoFactorService;
    private final RequestContextUtil requestContextUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameWithRoles(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Verificar si la cuenta está bloqueada
        if (lockoutService.isAccountLocked(user)) {
            long remainingMinutes = lockoutService.getRemainingLockoutTime(user);
            recordFailedLogin(user, "Account locked");
            throw new LockedException(String.format("Account is locked. Try again in %d minutes", remainingMinutes));
        }

        try {
            // Autenticar credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Si 2FA está habilitado, verificar el código
            if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
                if (request.getTwoFactorCode() == null) {
                    return LoginResponse.builder()
                            .requiresTwoFactor(true)
                            .message("Two-factor authentication code required")
                            .build();
                }

                if (!twoFactorService.isCodeValid(user, request.getTwoFactorCode())) {
                    lockoutService.handleFailedLogin(user);
                    userRepository.save(user);
                    recordFailedLogin(user, "Invalid 2FA code");
                    throw new BadCredentialsException("Invalid two-factor authentication code");
                }
            }

            // Login exitoso
            lockoutService.handleSuccessfulLogin(user);
            userRepository.save(user);

            // Generar tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Guardar refresh token
            saveRefreshToken(user, refreshToken);

            // Registrar login exitoso
            recordSuccessfulLogin(user, Boolean.TRUE.equals(user.getTwoFactorEnabled()));

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(900) // 15 minutos
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(user.getRoles().stream()
                            .map(Role::getName)
                            .toList())
                    .requiresTwoFactor(false)
                    .message("Login successful")
                    .build();

        } catch (BadCredentialsException e) {
            lockoutService.handleFailedLogin(user);
            userRepository.save(user);
            recordFailedLogin(user, "Invalid credentials");
            throw e;
        }
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenStr = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();

        // Generar nuevo access token
        String newAccessToken = jwtService.generateAccessToken(user);

        // Opcionalmente rotar el refresh token
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        saveRefreshToken(user, newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .build();
    }

    @Transactional
    public void logout(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.revoke();
                        refreshTokenRepository.save(token);
                    });
        } else {
            // Revocar todos los tokens del usuario
            refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
        }
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .ipAddress(requestContextUtil.getClientIp())
                .userAgent(requestContextUtil.getUserAgent())
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private void recordSuccessfulLogin(User user, boolean twoFactorUsed) {
        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .ipAddress(requestContextUtil.getClientIp())
                .userAgent(requestContextUtil.getUserAgent())
                .browser(requestContextUtil.getBrowser())
                .operatingSystem(requestContextUtil.getOperatingSystem())
                .device(requestContextUtil.getDevice())
                .location(requestContextUtil.getLocation())
                .successful(true)
                .twoFactorUsed(twoFactorUsed)
                .build();

        loginHistoryRepository.save(loginHistory);
    }

    private void recordFailedLogin(User user, String reason) {
        log.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        log.info("User {} tried to login with reason {}", user.getUsername(), reason);
        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .ipAddress(requestContextUtil.getClientIp())
                .userAgent(requestContextUtil.getUserAgent())
                .browser(requestContextUtil.getBrowser())
                .operatingSystem(requestContextUtil.getOperatingSystem())
                .device(requestContextUtil.getDevice())
                .successful(false)
                .failureReason(reason)
                .build();

        log.info(loginHistory.toString());
        loginHistoryRepository.save(loginHistory);
    }
}
