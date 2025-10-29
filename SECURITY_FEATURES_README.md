# Security Features Documentation

## Overview

This document describes the security features implemented in the Cronos Bakery System, including user profile management, session management, device fingerprinting, and security notifications.

## Features Implemented

### 1. User Profile Management

Users can manage their complete profile information including:

#### Personal Information
- Date of birth
- Gender
- Biography
- Profile picture (max 5MB, auto-resized to 400x400)
- Cover picture (max 10MB, resized to 1200x400)
- Address (street, city, state, postal code, country)
- Social media links (LinkedIn, Twitter, Facebook, Instagram)

#### Business Information
- Business name
- Business type
- Tax ID
- Business address
- Business contact (phone, email, website)

#### Preferences
- Language
- Timezone
- Currency
- Notification preferences (email, SMS, push)

#### API Endpoints

```
GET    /api/v1/profile                 - Get user profile
PUT    /api/v1/profile/personal        - Update personal information
PUT    /api/v1/profile/business        - Update business information
PUT    /api/v1/profile/preferences     - Update preferences
POST   /api/v1/profile/picture         - Upload profile picture
POST   /api/v1/profile/cover           - Upload cover picture
DELETE /api/v1/profile/picture         - Delete profile picture
DELETE /api/v1/profile/cover           - Delete cover picture
```

### 2. Session Management

The system now manages user sessions with the following features:

- **Maximum 5 concurrent sessions per user**
- When a 6th session is created, the oldest session is automatically terminated
- Track session information: device, browser, OS, IP address, location
- View all active and inactive sessions
- Terminate specific sessions
- Terminate all sessions except the current one

#### API Endpoints

```
GET    /api/v1/sessions                - Get active sessions
GET    /api/v1/sessions/all            - Get all sessions (active + inactive)
DELETE /api/v1/sessions/{sessionId}    - Terminate specific session
POST   /api/v1/sessions/terminate-others - Terminate all other sessions
```

### 3. Device Fingerprinting

The system tracks devices used to access the account:

- Generates unique device fingerprint based on browser characteristics
- Tracks device information (browser, OS, device type)
- Records first seen and last seen timestamps
- Login count per device
- Ability to trust/untrust devices

#### API Endpoints

```
GET    /api/v1/sessions/devices                - Get all trusted devices
POST   /api/v1/sessions/devices/{id}/trust     - Trust a device
POST   /api/v1/sessions/devices/{id}/untrust   - Untrust a device
```

### 4. Security Notifications

Users receive notifications for security-related events:

#### Notification Types
- `NEW_DEVICE_LOGIN` - Login from a new device (WARNING)
- `UNKNOWN_LOCATION_LOGIN` - Login from unknown location (WARNING)
- `PASSWORD_CHANGED` - Password was changed (INFO)
- `TWO_FACTOR_ENABLED` - 2FA was enabled (INFO)
- `TWO_FACTOR_DISABLED` - 2FA was disabled (WARNING)
- `SESSION_TERMINATED` - Session was terminated (INFO)
- `ACCOUNT_LOCKED` - Account was locked (CRITICAL)
- `FAILED_LOGIN_ATTEMPT` - Failed login attempt (WARNING)
- `PROFILE_UPDATED` - Profile was updated (INFO)
- `EMAIL_CHANGED` - Email was changed (WARNING)
- `SUSPICIOUS_ACTIVITY` - Suspicious activity detected (CRITICAL)

#### Notification Severity Levels
- `INFO` - Informational
- `WARNING` - Warning level
- `CRITICAL` - Critical security alert

#### API Endpoints

```
GET    /api/v1/notifications                     - Get paginated notifications
GET    /api/v1/notifications/unread              - Get unread notifications
GET    /api/v1/notifications/unread/count        - Get unread count
PUT    /api/v1/notifications/{id}/read           - Mark notification as read
PUT    /api/v1/notifications/mark-all-read       - Mark all as read
DELETE /api/v1/notifications/{id}                - Delete notification
```

## Database Schema

### New Tables

#### user_profiles
Stores user profile information including personal data, business information, and preferences.

#### user_sessions
Stores active and inactive user sessions with device information and activity tracking.

#### device_fingerprints
Stores known devices for each user with trust status and usage statistics.

#### security_notifications
Stores security-related notifications for users.

## Security Configuration

### Session Management
- Sessions are stateless (JWT-based)
- Maximum 5 concurrent sessions per user
- Automatic cleanup of expired sessions (runs hourly via scheduled task)
- Session tokens are unique and stored securely

### Device Fingerprinting
- Fingerprints generated using: User-Agent, Accept-Language, Accept-Encoding
- SHA-256 hashing for fingerprint storage
- Automatic detection of new devices
- Notifications sent for new device logins

### Image Upload Security
- Only allowed formats: JPEG, JPG, PNG, WEBP
- File size limits enforced
- Images automatically processed and resized
- Secure file storage with unique filenames (UUID-based)

## Configuration

### Application Properties

```yaml
app:
  upload:
    profile-pictures: uploads/profile-pictures
    cover-pictures: uploads/cover-pictures
    max-profile-picture-size: 5242880  # 5MB
    max-cover-picture-size: 10485760   # 10MB
```

## Services

### ProfileImageService
Handles profile and cover picture uploads with automatic image processing and resizing.

### SessionManagementService
Manages user sessions, device fingerprinting, and session lifecycle.

### UserProfileService
Manages user profile information including personal, business, and preference data.

### SecurityNotificationService
Manages security notifications including creation, retrieval, and status updates.

## Scheduled Tasks

### Session Cleanup
- **Schedule**: Every hour (0 0 * * * *)
- **Function**: Terminates expired sessions
- **Purpose**: Maintain database hygiene and security

## Testing the Features

### 1. Get User Profile
```bash
curl -X GET http://localhost:8080/api/v1/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Update Profile
```bash
curl -X PUT http://localhost:8080/api/v1/profile/personal \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "bio": "Software Developer"
  }'
```

### 3. Upload Profile Picture
```bash
curl -X POST http://localhost:8080/api/v1/profile/picture \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@profile.jpg"
```

### 4. Get Active Sessions
```bash
curl -X GET http://localhost:8080/api/v1/sessions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Terminate All Other Sessions
```bash
curl -X POST http://localhost:8080/api/v1/sessions/terminate-others \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Get Notifications
```bash
curl -X GET http://localhost:8080/api/v1/notifications \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Best Practices

1. **Always use HTTPS in production**
2. **Keep JWT tokens secure** - Never expose in logs or client-side storage
3. **Monitor security notifications** - Pay attention to critical alerts
4. **Regularly review active sessions** - Terminate suspicious sessions
5. **Trust only known devices** - Review and manage trusted devices periodically
6. **Enable 2FA** - Additional layer of security (feature already available in the system)

## Integration with Existing Features

This implementation integrates seamlessly with:
- Existing JWT authentication
- Two-factor authentication (2FA)
- Account lockout mechanism
- Password history tracking
- Rate limiting
- Email notifications

## Future Enhancements

Potential improvements:
1. Geolocation integration for more accurate location tracking
2. Email notifications for critical security events
3. SMS notifications for critical alerts
4. Push notifications via WebSocket
5. Security dashboard with activity analytics
6. Export security audit logs
7. Custom notification preferences per event type

## Support

For issues or questions, please refer to the main project documentation or contact the development team.
