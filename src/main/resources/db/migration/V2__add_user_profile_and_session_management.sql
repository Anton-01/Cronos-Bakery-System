-- User Profile Table
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,

    -- Personal Information
    date_of_birth DATE,
    gender VARCHAR(10),
    bio VARCHAR(500),
    profile_picture_url VARCHAR(500),
    cover_picture_url VARCHAR(500),

    -- Address Information
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),

    -- Business Information
    business_name VARCHAR(200),
    business_type VARCHAR(100),
    tax_id VARCHAR(50),
    business_address VARCHAR(500),
    business_city VARCHAR(100),
    business_state VARCHAR(100),
    business_postal_code VARCHAR(20),
    business_country VARCHAR(100),
    business_phone VARCHAR(20),
    business_email VARCHAR(255),
    business_website VARCHAR(255),

    -- Social Links
    linkedin_url VARCHAR(255),
    twitter_url VARCHAR(255),
    facebook_url VARCHAR(255),
    instagram_url VARCHAR(255),

    -- Preferences
    language VARCHAR(10),
    timezone VARCHAR(50),
    currency VARCHAR(10),
    email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    sms_notifications BOOLEAN NOT NULL DEFAULT FALSE,
    push_notifications BOOLEAN NOT NULL DEFAULT TRUE,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User Sessions Table
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(500) NOT NULL UNIQUE,
    device_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    device VARCHAR(100),
    location VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    terminated_at TIMESTAMP,
    termination_reason VARCHAR(255),

    CONSTRAINT fk_user_session_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_session ON user_sessions(user_id, created_at);
CREATE INDEX idx_session_token ON user_sessions(session_token);
CREATE INDEX idx_session_active ON user_sessions(user_id, is_active);

-- Device Fingerprints Table
CREATE TABLE IF NOT EXISTS device_fingerprints (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fingerprint_hash VARCHAR(255) NOT NULL,
    device_name VARCHAR(200),
    user_agent VARCHAR(500),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    device_type VARCHAR(50),
    ip_address VARCHAR(45),
    location VARCHAR(100),
    is_trusted BOOLEAN NOT NULL DEFAULT FALSE,
    first_seen_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP,
    trusted_at TIMESTAMP,
    login_count INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_device_fingerprint_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_device ON device_fingerprints(user_id, fingerprint_hash);
CREATE INDEX idx_device_trusted ON device_fingerprints(user_id, is_trusted);

-- Security Notifications Table
CREATE TABLE IF NOT EXISTS security_notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    device_name VARCHAR(200),
    ip_address VARCHAR(45),
    location VARCHAR(100),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    email_sent BOOLEAN NOT NULL DEFAULT FALSE,
    email_sent_at TIMESTAMP,

    CONSTRAINT fk_security_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_notification ON security_notifications(user_id, created_at);
CREATE INDEX idx_notification_read ON security_notifications(user_id, is_read);
