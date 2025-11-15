package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.EmailSettingsRequest;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.EmailSettingsResponse;
import com.cronos.bakery.application.mapper.EmailSettingsMapper;
import com.cronos.bakery.application.service.CustomizationService;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.customization.EmailSettings;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Email Settings", description = "Email configuration management endpoints")
public class EmailSettingsController {

    private final CustomizationService customizationService;
    private final EmailSettingsMapper emailSettingsMapper;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get email settings")
    public ResponseEntity<ApiResponse<EmailSettingsResponse>> getEmailSettings(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailSettings settings = customizationService.getEmailSettings(user.getId());
        EmailSettingsResponse response = emailSettingsMapper.toResponse(settings);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update email settings")
    public ResponseEntity<ApiResponse<EmailSettingsResponse>> updateEmailSettings(@Valid @RequestBody EmailSettingsRequest request, Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailSettings settings = emailSettingsMapper.toEntity(request);
        EmailSettings updated = customizationService.updateEmailSettings(user.getId(), settings);
        EmailSettingsResponse response = emailSettingsMapper.toResponse(updated);

        return ResponseEntity.ok(ApiResponse.success("Email settings updated successfully", response));
    }
}
