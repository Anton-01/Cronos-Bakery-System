package com.cronos.bakery.domain.service;

import com.cronos.bakery.domain.entity.PasswordHistory;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityProperties securityProperties;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        var policy = securityProperties.getPassword();

        if (password == null || password.length() < policy.getMinLength()) {
            errors.add(String.format("Password must be at least %d characters long",
                    policy.getMinLength()));
        }

        if (password != null && password.length() > policy.getMaxLength()) {
            errors.add(String.format("Password must not exceed %d characters",
                    policy.getMaxLength()));
        }

        if (policy.getRequireUppercase() && !UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (policy.getRequireLowercase() && !LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (policy.getRequireDigit() && !DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }

        if (policy.getRequireSpecialChar() && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character");
        }

        return errors;
    }

    public boolean isPasswordReused(User user, String newPassword) {
        int historyCount = securityProperties.getPassword().getHistoryCount();

        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findLastNPasswordsByUser(user, PageRequest.of(0, historyCount));

        return passwordHistories.stream()
                .anyMatch(ph -> passwordEncoder.matches(newPassword, ph.getPasswordHash()));
    }

    public void savePasswordHistory(User user, String encodedPassword) {
        PasswordHistory passwordHistory = PasswordHistory.builder()
                .user(user)
                .passwordHash(encodedPassword)
                .build();

        passwordHistoryRepository.save(passwordHistory);
    }
}
