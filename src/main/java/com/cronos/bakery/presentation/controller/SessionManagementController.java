package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.response.DeviceFingerprintResponse;
import com.cronos.bakery.application.dto.response.UserSessionResponse;
import com.cronos.bakery.application.service.SessionManagementService;
import com.cronos.bakery.domain.entity.DeviceFingerprint;
import com.cronos.bakery.domain.entity.UserSession;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import com.cronos.bakery.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "User session management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SessionManagementController {

    private final SessionManagementService sessionService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "Get active sessions", description = "Gets all active sessions for the authenticated user")
    public ResponseEntity<List<UserSessionResponse>> getActiveSessions(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        User user = getUserFromDetails(userDetails);
        List<UserSession> sessions = sessionService.getActiveSessions(user.getId());
        String currentToken = extractToken(request);

        List<UserSessionResponse> response = sessions.stream()
                .map(session -> mapToResponse(session, currentToken))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all sessions", description = "Gets all sessions (active and inactive) for the authenticated user")
    public ResponseEntity<List<UserSessionResponse>> getAllSessions(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        User user = getUserFromDetails(userDetails);
        List<UserSession> sessions = sessionService.getAllSessions(user.getId());
        String currentToken = extractToken(request);

        List<UserSessionResponse> response = sessions.stream()
                .map(session -> mapToResponse(session, currentToken))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Terminate session", description = "Terminates a specific session")
    public ResponseEntity<Void> terminateSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long sessionId) {

        User user = getUserFromDetails(userDetails);
        sessionService.terminateSession(sessionId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/terminate-others")
    @Operation(summary = "Terminate all other sessions", description = "Terminates all sessions except the current one")
    public ResponseEntity<Map<String, Object>> terminateOtherSessions(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        User user = getUserFromDetails(userDetails);
        String currentToken = extractToken(request);

        int terminatedCount = sessionService.terminateOtherSessions(user.getId(), currentToken);

        Map<String, Object> response = new HashMap<>();
        response.put("terminatedCount", terminatedCount);
        response.put("message", terminatedCount + " session(s) terminated successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/devices")
    @Operation(summary = "Get trusted devices", description = "Gets all trusted devices for the authenticated user")
    public ResponseEntity<List<DeviceFingerprintResponse>> getTrustedDevices(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);
        List<DeviceFingerprint> devices = sessionService.getTrustedDevices(user.getId());

        List<DeviceFingerprintResponse> response = devices.stream()
                .map(this::mapToDeviceResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/devices/{deviceId}/trust")
    @Operation(summary = "Trust device", description = "Marks a device as trusted")
    public ResponseEntity<Void> trustDevice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long deviceId) {

        User user = getUserFromDetails(userDetails);
        sessionService.trustDevice(deviceId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/devices/{deviceId}/untrust")
    @Operation(summary = "Untrust device", description = "Removes trust from a device")
    public ResponseEntity<Void> untrustDevice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long deviceId) {

        User user = getUserFromDetails(userDetails);
        sessionService.untrustDevice(deviceId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // Helper methods
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private UserSessionResponse mapToResponse(UserSession session, String currentToken) {
        boolean isCurrent = currentToken != null && currentToken.equals(session.getSessionToken());

        return UserSessionResponse.builder()
                .id(session.getId())
                .deviceId(session.getDeviceId())
                .ipAddress(session.getIpAddress())
                .browser(session.getBrowser())
                .operatingSystem(session.getOperatingSystem())
                .device(session.getDevice())
                .location(session.getLocation())
                .isActive(session.getIsActive())
                .createdAt(session.getCreatedAt())
                .lastActivityAt(session.getLastActivityAt())
                .expiresAt(session.getExpiresAt())
                .terminatedAt(session.getTerminatedAt())
                .terminationReason(session.getTerminationReason())
                .isCurrent(isCurrent)
                .build();
    }

    private DeviceFingerprintResponse mapToDeviceResponse(DeviceFingerprint device) {
        return DeviceFingerprintResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .browser(device.getBrowser())
                .operatingSystem(device.getOperatingSystem())
                .deviceType(device.getDeviceType())
                .ipAddress(device.getIpAddress())
                .location(device.getLocation())
                .isTrusted(device.getIsTrusted())
                .firstSeenAt(device.getFirstSeenAt())
                .lastSeenAt(device.getLastSeenAt())
                .trustedAt(device.getTrustedAt())
                .loginCount(device.getLoginCount())
                .build();
    }
}
