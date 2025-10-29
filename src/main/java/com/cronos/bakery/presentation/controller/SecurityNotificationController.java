package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.response.SecurityNotificationResponse;
import com.cronos.bakery.application.service.SecurityNotificationService;
import com.cronos.bakery.domain.entity.SecurityNotification;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Security Notifications", description = "Security notification management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SecurityNotificationController {

    private final SecurityNotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Gets paginated notifications for the authenticated user")
    public ResponseEntity<Page<SecurityNotificationResponse>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        User user = getUserFromDetails(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SecurityNotification> notifications = notificationService.getUserNotifications(user.getId(), pageable);

        Page<SecurityNotificationResponse> response = notifications.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Gets all unread notifications for the authenticated user")
    public ResponseEntity<List<SecurityNotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);
        List<SecurityNotification> notifications = notificationService.getUnreadNotifications(user.getId());

        List<SecurityNotificationResponse> response = notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Gets count of unread notifications")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);
        long count = notificationService.getUnreadCount(user.getId());

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark as read", description = "Marks a notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "Mark all as read", description = "Marks all notifications as read")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);
        int count = notificationService.markAllAsRead(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("markedCount", count);
        response.put("message", count + " notification(s) marked as read");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Deletes a specific notification")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId) {

        User user = getUserFromDetails(userDetails);
        notificationService.deleteNotification(notificationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // Helper methods
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private SecurityNotificationResponse mapToResponse(SecurityNotification notification) {
        return SecurityNotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .severity(notification.getSeverity().name())
                .deviceName(notification.getDeviceName())
                .ipAddress(notification.getIpAddress())
                .location(notification.getLocation())
                .browser(notification.getBrowser())
                .operatingSystem(notification.getOperatingSystem())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
