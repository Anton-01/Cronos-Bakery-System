package com.cronos.bakery.domain.service;

import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountLockoutService {

    private final SecurityProperties securityProperties;

    public void handleFailedLogin(User user) {
        user.incrementFailedAttempts();

        int maxAttempts = securityProperties.getAccountLockout().getMaxAttempts();

        if (user.getFailedLoginAttempts() >= maxAttempts) {
            int lockoutDuration = calculateLockoutDuration(user);
            user.lockAccount(lockoutDuration);
            log.warn("Account locked for user: {} for {} minutes",
                    user.getUsername(), lockoutDuration);
        }
    }

    public void handleSuccessfulLogin(User user) {
        if (user.isAccountLocked()) {
            log.info("Account was locked but lockout period expired for user: {}",
                    user.getUsername());
        }
        user.updateLastLogin();
    }

    private int calculateLockoutDuration(User user) {
        int baseDuration = securityProperties.getAccountLockout().getLockoutDurationMinutes();
        int incrementFactor = securityProperties.getAccountLockout().getIncrementFactor();
        int maxDuration = securityProperties.getAccountLockout().getMaxLockoutDurationHours() * 60;

        // Calcular cuántos bloqueos previos ha tenido
        int lockoutCount = (user.getFailedLoginAttempts() /
                securityProperties.getAccountLockout().getMaxAttempts()) - 1;

        // Incremento exponencial: baseDuration * (incrementFactor ^ lockoutCount)
        int calculatedDuration = (int) (baseDuration * Math.pow(incrementFactor, lockoutCount));

        return Math.min(calculatedDuration, maxDuration);
    }

    public boolean isAccountLocked(User user) {
        if (Boolean.TRUE.equals(user.getAccountNonLocked())) {
            return false;
        }

        if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
            user.resetFailedAttempts();
            return false;
        }

        return true;
    }

    public long getRemainingLockoutTime(User user) {
        if (user.getLockedUntil() == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(user.getLockedUntil())) {
            return 0;
        }

        return ChronoUnit.MINUTES.between(now, user.getLockedUntil());
    }
}
