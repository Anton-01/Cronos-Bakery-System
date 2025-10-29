package com.cronos.bakery.application.service;

import com.cronos.bakery.domain.entity.DeviceFingerprint;
import com.cronos.bakery.domain.entity.SecurityNotification;
import com.cronos.bakery.domain.entity.UserSession;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.infrastructure.persistence.DeviceFingerprintRepository;
import com.cronos.bakery.infrastructure.persistence.SecurityNotificationRepository;
import com.cronos.bakery.infrastructure.persistence.UserSessionRepository;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final UserSessionRepository sessionRepository;
    private final DeviceFingerprintRepository deviceFingerprintRepository;
    private final SecurityNotificationRepository notificationRepository;

    private static final int MAX_ACTIVE_SESSIONS = 5;

    /**
     * Creates a new session for the user
     */
    @Transactional
    public UserSession createSession(User user, String sessionToken, HttpServletRequest request) {
        // Parse user agent
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ipAddress = getClientIpAddress(request);
        String fingerprintHash = generateDeviceFingerprint(request);

        // Check device fingerprint
        Optional<DeviceFingerprint> deviceOpt = deviceFingerprintRepository
                .findByUserIdAndFingerprintHash(user.getId(), fingerprintHash);

        boolean isNewDevice = deviceOpt.isEmpty();
        DeviceFingerprint device;

        if (isNewDevice) {
            // Create new device fingerprint
            device = DeviceFingerprint.builder()
                    .user(user)
                    .fingerprintHash(fingerprintHash)
                    .deviceName(userAgent.getOperatingSystem().getName() + " - " + userAgent.getBrowser().getName())
                    .userAgent(request.getHeader("User-Agent"))
                    .browser(userAgent.getBrowser().getName())
                    .operatingSystem(userAgent.getOperatingSystem().getName())
                    .deviceType(userAgent.getOperatingSystem().getDeviceType().getName())
                    .ipAddress(ipAddress)
                    .build();
            device = deviceFingerprintRepository.save(device);

            // Send notification about new device
            sendNewDeviceNotification(user, device, ipAddress);
        } else {
            device = deviceOpt.get();
            device.updateLastSeen();
            deviceFingerprintRepository.save(device);
        }

        // Check active sessions count
        long activeSessions = sessionRepository.countActiveSessionsByUserId(user.getId());

        if (activeSessions >= MAX_ACTIVE_SESSIONS) {
            // Terminate oldest session
            List<UserSession> sessions = sessionRepository.findActiveSessionsByUserId(user.getId());
            UserSession oldestSession = sessions.get(sessions.size() - 1);
            oldestSession.terminate("Maximum sessions limit reached");
            sessionRepository.save(oldestSession);

            log.info("Terminated oldest session for user: {} due to max sessions limit", user.getId());
        }

        // Create new session
        UserSession session = UserSession.builder()
                .user(user)
                .sessionToken(sessionToken)
                .deviceId(fingerprintHash)
                .ipAddress(ipAddress)
                .userAgent(request.getHeader("User-Agent"))
                .browser(userAgent.getBrowser().getName())
                .operatingSystem(userAgent.getOperatingSystem().getName())
                .device(userAgent.getOperatingSystem().getDeviceType().getName())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        session = sessionRepository.save(session);
        log.info("Created new session for user: {} from device: {}", user.getId(), device.getDeviceName());

        return session;
    }

    /**
     * Updates session activity
     */
    @Transactional
    public void updateSessionActivity(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken)
                .ifPresent(session -> {
                    session.updateActivity();
                    sessionRepository.save(session);
                });
    }

    /**
     * Gets all active sessions for a user
     */
    public List<UserSession> getActiveSessions(Long userId) {
        return sessionRepository.findActiveSessionsByUserId(userId);
    }

    /**
     * Gets all sessions (active and inactive) for a user
     */
    public List<UserSession> getAllSessions(Long userId) {
        return sessionRepository.findAllSessionsByUserId(userId);
    }

    /**
     * Terminates a specific session
     */
    @Transactional
    public void terminateSession(Long sessionId, Long userId) {
        UserSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to user");
        }

        session.terminate("Terminated by user");
        sessionRepository.save(session);

        log.info("Session {} terminated by user {}", sessionId, userId);
    }

    /**
     * Terminates all sessions except the current one
     */
    @Transactional
    public int terminateOtherSessions(Long userId, String currentSessionToken) {
        int terminatedCount = sessionRepository.terminateOtherUserSessions(
                userId,
                currentSessionToken,
                LocalDateTime.now(),
                "Terminated by user - logout from all other devices"
        );

        if (terminatedCount > 0) {
            // Send notification
            sendSessionsTerminatedNotification(userId, terminatedCount);
        }

        log.info("Terminated {} sessions for user {}", terminatedCount, userId);
        return terminatedCount;
    }

    /**
     * Scheduled task to clean up expired sessions
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void cleanupExpiredSessions() {
        int terminated = sessionRepository.terminateExpiredSessions(LocalDateTime.now());
        if (terminated > 0) {
            log.info("Cleaned up {} expired sessions", terminated);
        }
    }

    /**
     * Gets all trusted devices for a user
     */
    public List<DeviceFingerprint> getTrustedDevices(Long userId) {
        return deviceFingerprintRepository.findTrustedDevicesByUserId(userId);
    }

    /**
     * Trusts a device
     */
    @Transactional
    public void trustDevice(Long deviceId, Long userId) {
        DeviceFingerprint device = deviceFingerprintRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        if (!device.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Device does not belong to user");
        }

        device.trust();
        deviceFingerprintRepository.save(device);

        log.info("Device {} trusted by user {}", deviceId, userId);
    }

    /**
     * Untrusts a device
     */
    @Transactional
    public void untrustDevice(Long deviceId, Long userId) {
        DeviceFingerprint device = deviceFingerprintRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        if (!device.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Device does not belong to user");
        }

        device.untrust();
        deviceFingerprintRepository.save(device);

        log.info("Device {} untrusted by user {}", deviceId, userId);
    }

    /**
     * Generates device fingerprint hash
     */
    private String generateDeviceFingerprint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");

        String fingerprintData = userAgent + "|" + acceptLanguage + "|" + acceptEncoding;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fingerprintData.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating device fingerprint", e);
            return fingerprintData.hashCode() + "";
        }
    }

    /**
     * Gets client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Sends notification about new device login
     */
    private void sendNewDeviceNotification(User user, DeviceFingerprint device, String ipAddress) {
        SecurityNotification notification = SecurityNotification.builder()
                .user(user)
                .type(SecurityNotification.NotificationType.NEW_DEVICE_LOGIN)
                .title("New Device Login Detected")
                .message("A new login was detected from a device you haven't used before.")
                .severity(SecurityNotification.NotificationSeverity.WARNING)
                .deviceName(device.getDeviceName())
                .ipAddress(ipAddress)
                .browser(device.getBrowser())
                .operatingSystem(device.getOperatingSystem())
                .build();

        notificationRepository.save(notification);
        log.info("Sent new device notification to user: {}", user.getId());
    }

    /**
     * Sends notification about sessions termination
     */
    private void sendSessionsTerminatedNotification(Long userId, int count) {
        SecurityNotification notification = SecurityNotification.builder()
                .user(new User()) // Will be fetched by userId
                .type(SecurityNotification.NotificationType.SESSION_TERMINATED)
                .title("Sessions Terminated")
                .message(count + " session(s) were terminated at your request.")
                .severity(SecurityNotification.NotificationSeverity.INFO)
                .build();

        notification.getUser().setId(userId);
        notificationRepository.save(notification);
    }
}
