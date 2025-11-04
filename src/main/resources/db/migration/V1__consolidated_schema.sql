-- Cronos Bakery System - Consolidated Database Schema
-- Version 1.0 - All tables synchronized with JPA entities
-- This migration creates the complete database schema with all fields

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================================
-- USERS AND AUTHENTICATION
-- ================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    business_name VARCHAR(255),
    default_currency VARCHAR(3) DEFAULT 'MXN',
    default_language VARCHAR(2) DEFAULT 'es',
    default_tax_rate DECIMAL(5, 2) DEFAULT 16.00,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret VARCHAR(500),
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    last_failed_login TIMESTAMP,
    password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500)
);

CREATE TABLE login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    device VARCHAR(100),
    location VARCHAR(100),
    successful BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),
    two_factor_used BOOLEAN
);

CREATE TABLE password_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(500) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE,
    gender VARCHAR(10),
    bio VARCHAR(500),
    profile_picture_url VARCHAR(500),
    cover_picture_url VARCHAR(500),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
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
    linkedin_url VARCHAR(255),
    twitter_url VARCHAR(255),
    facebook_url VARCHAR(255),
    instagram_url VARCHAR(255),
    language VARCHAR(10),
    timezone VARCHAR(50),
    currency VARCHAR(10),
    email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    sms_notifications BOOLEAN NOT NULL DEFAULT FALSE,
    push_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
    termination_reason VARCHAR(255)
);

CREATE TABLE device_fingerprints (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
    login_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE security_notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
    email_sent_at TIMESTAMP
);

-- ================================================
-- CORE BAKERY ENTITIES
-- ================================================

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    is_system_default BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    UNIQUE (name, user_id)
);

CREATE TABLE allergens (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    name_en VARCHAR(200),
    name_es VARCHAR(200),
    description TEXT,
    is_system_default BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE unit_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL
);

CREATE TABLE measurement_units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    name_plural VARCHAR(100),
    type VARCHAR(50) NOT NULL,
    unit_type_id BIGINT REFERENCES unit_types(id),
    is_system_default BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE conversion_factors (
    id BIGSERIAL PRIMARY KEY,
    from_unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    to_unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    factor DECIMAL(20, 10) NOT NULL,
    is_system_default BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    UNIQUE (from_unit_id, to_unit_id, user_id)
);

CREATE TABLE raw_materials (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    brand VARCHAR(255) NOT NULL,
    supplier VARCHAR(500) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    purchase_unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    purchase_quantity DECIMAL(15, 4) NOT NULL,
    unit_cost DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    current_stock DECIMAL(15, 4) DEFAULT 0,
    minimum_stock DECIMAL(15, 4),
    last_purchase_date TIMESTAMP,
    last_price_update TIMESTAMP,
    needs_recalculation BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE raw_material_allergens (
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    PRIMARY KEY (raw_material_id, allergen_id)
);

CREATE TABLE material_price_history (
    id BIGSERIAL PRIMARY KEY,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    previous_cost DECIMAL(15, 2),
    new_cost DECIMAL(15, 2) NOT NULL,
    change_percentage DECIMAL(5, 2),
    reason VARCHAR(500),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100)
);

-- ================================================
-- RECIPES
-- ================================================

CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id),
    yield_quantity DECIMAL(10, 2) NOT NULL,
    yield_unit VARCHAR(50) NOT NULL,
    preparation_time_minutes INTEGER,
    baking_time_minutes INTEGER,
    cooling_time_minutes INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    needs_recalculation BOOLEAN DEFAULT FALSE,
    current_version INTEGER DEFAULT 1,
    instructions VARCHAR(5000),
    storage_instructions VARCHAR(1000),
    shelf_life_days INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE recipe_allergens (
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    PRIMARY KEY (recipe_id, allergen_id)
);

CREATE TABLE recipe_versions (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    version_name VARCHAR(200),
    changes VARCHAR(5000),
    snapshot_data TEXT,
    is_current BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE (recipe_id, version_number)
);

CREATE TABLE recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id),
    quantity DECIMAL(15, 4) NOT NULL,
    unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    display_order INTEGER,
    is_optional BOOLEAN DEFAULT FALSE,
    notes TEXT,
    cost_per_unit DECIMAL(15, 6),
    total_cost DECIMAL(15, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE recipe_sub_recipes (
    id BIGSERIAL PRIMARY KEY,
    parent_recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    sub_recipe_id BIGINT NOT NULL REFERENCES recipes(id),
    quantity DECIMAL(15, 4) NOT NULL,
    display_order INTEGER,
    notes TEXT,
    total_cost DECIMAL(15, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE ingredient_substitutes (
    id BIGSERIAL PRIMARY KEY,
    original_ingredient_id BIGINT NOT NULL REFERENCES recipe_ingredients(id) ON DELETE CASCADE,
    substitute_material_id BIGINT NOT NULL REFERENCES raw_materials(id),
    conversion_ratio DECIMAL(10, 4) NOT NULL DEFAULT 1.0,
    reason VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE recipe_fixed_costs (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    calculation_method VARCHAR(50) NOT NULL,
    time_in_minutes INTEGER,
    percentage DECIMAL(5, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE recipe_cost_history (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    recipe_version INTEGER,
    materials_cost DECIMAL(15, 2) NOT NULL,
    fixed_costs DECIMAL(15, 2) NOT NULL,
    sub_recipes_cost DECIMAL(15, 2) DEFAULT 0,
    total_cost DECIMAL(15, 2) NOT NULL,
    cost_per_unit DECIMAL(15, 6) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    calculated_by VARCHAR(100),
    calculation_notes VARCHAR(1000)
);

CREATE TABLE recipe_files (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    file_name VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100),
    is_primary BOOLEAN DEFAULT FALSE,
    thumbnail_path VARCHAR(1000),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- ================================================
-- PROFIT MARGINS AND QUOTES
-- ================================================

CREATE TABLE profit_margins (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    percentage DECIMAL(5, 2) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    UNIQUE (user_id, name)
);

CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    client_name VARCHAR(200) NOT NULL,
    client_email VARCHAR(200),
    client_phone VARCHAR(50),
    client_address VARCHAR(500),
    notes VARCHAR(2000),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    valid_until TIMESTAMP,
    subtotal DECIMAL(15, 2) NOT NULL,
    tax_rate DECIMAL(5, 2) NOT NULL,
    tax_amount DECIMAL(15, 2) NOT NULL,
    total DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    share_token VARCHAR(100) UNIQUE,
    share_expires_at TIMESTAMP,
    is_shareable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE quote_items (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id),
    quantity DECIMAL(15, 4) NOT NULL,
    scale_factor DECIMAL(10, 4) DEFAULT 1.0,
    profit_margin_id BIGINT REFERENCES profit_margins(id),
    unit_cost DECIMAL(15, 6) NOT NULL,
    profit_percentage DECIMAL(5, 2) NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    notes TEXT,
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE quote_access_logs (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    accessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    accessed_by_email VARCHAR(200),
    user_id BIGINT REFERENCES users(id)
);

-- ================================================
-- CUSTOMIZATION AND SETTINGS
-- ================================================

CREATE TABLE branding_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(200),
    logo_url VARCHAR(500),
    logo_small_url VARCHAR(500),
    primary_color VARCHAR(7) NOT NULL DEFAULT '#007bff',
    secondary_color VARCHAR(7) NOT NULL DEFAULT '#6c757d',
    accent_color VARCHAR(7) NOT NULL DEFAULT '#28a745',
    text_color VARCHAR(7) DEFAULT '#212529',
    background_color VARCHAR(7) DEFAULT '#ffffff',
    font_family VARCHAR(100) DEFAULT 'Arial, sans-serif',
    font_size_base INTEGER DEFAULT 14,
    header_font_family VARCHAR(100) DEFAULT 'Georgia, serif',
    company_slogan VARCHAR(500),
    footer_text VARCHAR(1000),
    website_url VARCHAR(500),
    phone VARCHAR(50),
    email VARCHAR(200),
    address TEXT,
    tax_id VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE email_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    sender_email VARCHAR(200),
    sender_name VARCHAR(200) NOT NULL,
    reply_to_email VARCHAR(200),
    smtp_host VARCHAR(200),
    smtp_port INTEGER,
    smtp_username VARCHAR(200),
    smtp_password VARCHAR(500),
    use_tls BOOLEAN NOT NULL DEFAULT TRUE,
    use_ssl BOOLEAN NOT NULL DEFAULT FALSE,
    email_signature TEXT,
    auto_send_quotes BOOLEAN NOT NULL DEFAULT FALSE,
    use_custom_smtp BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    notify_price_changes BOOLEAN NOT NULL DEFAULT TRUE,
    price_change_threshold_percent DECIMAL(5, 2) DEFAULT 5.0,
    notify_price_increase_only BOOLEAN NOT NULL DEFAULT FALSE,
    notify_low_stock BOOLEAN NOT NULL DEFAULT TRUE,
    low_stock_threshold_percent DECIMAL(5, 2) DEFAULT 20.0,
    notify_quote_viewed BOOLEAN NOT NULL DEFAULT TRUE,
    notify_quote_expiring BOOLEAN NOT NULL DEFAULT TRUE,
    quote_expiry_notice_hours INTEGER DEFAULT 24,
    notify_recipe_cost_change BOOLEAN NOT NULL DEFAULT TRUE,
    recipe_cost_change_threshold_percent DECIMAL(5, 2) DEFAULT 10.0,
    notify_daily_summary BOOLEAN NOT NULL DEFAULT FALSE,
    notify_weekly_report BOOLEAN NOT NULL DEFAULT FALSE,
    notify_monthly_report BOOLEAN NOT NULL DEFAULT TRUE,
    email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    websocket_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_hours_start INTEGER,
    quiet_hours_end INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- ================================================
-- TEMPLATES
-- ================================================

CREATE TABLE quote_templates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    language VARCHAR(10) NOT NULL DEFAULT 'ES',
    format VARCHAR(20) NOT NULL DEFAULT 'PDF',
    header_html TEXT,
    body_html TEXT,
    footer_html TEXT,
    css_styles TEXT,
    show_logo BOOLEAN NOT NULL DEFAULT TRUE,
    show_item_images BOOLEAN NOT NULL DEFAULT FALSE,
    show_allergens BOOLEAN NOT NULL DEFAULT TRUE,
    show_tax_breakdown BOOLEAN NOT NULL DEFAULT TRUE,
    show_payment_terms BOOLEAN NOT NULL DEFAULT TRUE,
    payment_terms TEXT,
    terms_and_conditions TEXT,
    signature_text VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE report_templates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    report_type VARCHAR(50) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'ES',
    format VARCHAR(20) NOT NULL DEFAULT 'PDF',
    header_html TEXT,
    body_html TEXT,
    footer_html TEXT,
    css_styles TEXT,
    include_charts BOOLEAN NOT NULL DEFAULT TRUE,
    include_summary BOOLEAN NOT NULL DEFAULT TRUE,
    include_detailed_breakdown BOOLEAN NOT NULL DEFAULT TRUE,
    show_logo BOOLEAN NOT NULL DEFAULT TRUE,
    chart_color_scheme VARCHAR(50) DEFAULT 'default',
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE TABLE email_templates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    template_type VARCHAR(50) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'ES',
    subject VARCHAR(500) NOT NULL,
    html_body TEXT,
    text_body TEXT,
    description VARCHAR(500),
    variables_help TEXT,
    is_system_template BOOLEAN NOT NULL DEFAULT FALSE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- ================================================
-- INVENTORY AND ALERTS
-- ================================================

CREATE TABLE stock_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    alert_type VARCHAR(30) NOT NULL,
    current_quantity DECIMAL(15, 2) NOT NULL,
    threshold_quantity DECIMAL(15, 2) NOT NULL,
    threshold_percent DECIMAL(5, 2),
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    triggered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP,
    resolved_at TIMESTAMP,
    email_sent BOOLEAN NOT NULL DEFAULT FALSE,
    email_sent_at TIMESTAMP,
    auto_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- ================================================
-- TAX MANAGEMENT
-- ================================================

CREATE TABLE tax_rates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    country_code VARCHAR(2) NOT NULL,
    region_code VARCHAR(10),
    region_name VARCHAR(200) NOT NULL,
    tax_name VARCHAR(100) NOT NULL,
    tax_rate_percent DECIMAL(5, 2) NOT NULL,
    reduced_rate_percent DECIMAL(5, 2),
    super_reduced_rate_percent DECIMAL(5, 2),
    effective_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date DATE,
    description VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    applies_to_food BOOLEAN NOT NULL DEFAULT TRUE,
    tax_id_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- ================================================
-- INDEXES FOR PERFORMANCE
-- ================================================

-- Users indexes
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_users_enabled ON users(enabled);

-- Authentication indexes
CREATE INDEX idx_token ON refresh_tokens(token);
CREATE INDEX idx_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_user_login ON login_history(user_id, login_at);
CREATE INDEX idx_ip_address ON login_history(ip_address);
CREATE INDEX idx_user_password ON password_history(user_id);

-- Session and security indexes
CREATE INDEX idx_user_session ON user_sessions(user_id, created_at);
CREATE INDEX idx_session_token ON user_sessions(session_token);
CREATE INDEX idx_session_active ON user_sessions(user_id, is_active);
CREATE INDEX idx_user_device ON device_fingerprints(user_id, fingerprint_hash);
CREATE INDEX idx_device_trusted ON device_fingerprints(user_id, is_trusted);
CREATE INDEX idx_user_notification ON security_notifications(user_id, created_at);
CREATE INDEX idx_notification_read ON security_notifications(user_id, is_read);

-- Categories and allergens
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_allergens_user ON allergens(user_id);

-- Raw materials
CREATE INDEX idx_user_material ON raw_materials(user_id, name);
CREATE INDEX idx_category ON raw_materials(category_id);
CREATE INDEX idx_raw_materials_active ON raw_materials(user_id);
CREATE INDEX idx_material_date ON material_price_history(raw_material_id, changed_at);

-- Recipes
CREATE INDEX idx_user_recipe ON recipes(user_id, name);
CREATE INDEX idx_recipes_category ON recipes(category_id);
CREATE INDEX idx_recipes_active ON recipes(is_active);
CREATE INDEX idx_recipe_ingredient ON recipe_ingredients(recipe_id, raw_material_id);
CREATE INDEX idx_recipe_version ON recipe_versions(recipe_id, version_number);
CREATE INDEX idx_recipe_cost_date ON recipe_cost_history(recipe_id, calculated_at);

-- Quotes
CREATE INDEX idx_user_quote ON quotes(user_id, created_at);
CREATE INDEX idx_share_token ON quotes(share_token);
CREATE INDEX idx_quotes_status ON quotes(status);
CREATE INDEX idx_quote_items_quote ON quote_items(quote_id);
CREATE INDEX idx_quote_access ON quote_access_logs(quote_id, accessed_at);

-- Profit margins
CREATE INDEX idx_profit_margin_user ON profit_margins(user_id);

-- Templates
CREATE INDEX idx_quote_template_user ON quote_templates(user_id);
CREATE INDEX idx_quote_template_active ON quote_templates(is_active);
CREATE INDEX idx_report_template_user ON report_templates(user_id);
CREATE INDEX idx_report_template_type ON report_templates(report_type);
CREATE INDEX idx_report_template_active ON report_templates(is_active);
CREATE INDEX idx_email_template_user ON email_templates(user_id);
CREATE INDEX idx_email_template_type ON email_templates(template_type);
CREATE INDEX idx_email_template_lang ON email_templates(language);
CREATE INDEX idx_email_template_active ON email_templates(is_active);

-- Stock alerts
CREATE INDEX idx_stock_alert_user ON stock_alerts(user_id);
CREATE INDEX idx_stock_alert_material ON stock_alerts(raw_material_id);
CREATE INDEX idx_stock_alert_status ON stock_alerts(status);
CREATE INDEX idx_stock_alert_created ON stock_alerts(created_at);

-- Tax rates
CREATE INDEX idx_tax_rate_user ON tax_rates(user_id);
CREATE INDEX idx_tax_rate_region ON tax_rates(region_code);
CREATE INDEX idx_tax_rate_active ON tax_rates(is_active, effective_date);

-- ================================================
-- INITIAL SYSTEM DATA
-- ================================================

-- Insert default roles
INSERT INTO roles (name, description, created_by, updated_by) VALUES
    ('ROLE_ADMIN', 'Administrator role with full access', 'SYSTEM', 'SYSTEM'),
    ('ROLE_USER', 'Standard user role', 'SYSTEM', 'SYSTEM'),
    ('ROLE_VIEWER', 'Read-only access role', 'SYSTEM', 'SYSTEM');

-- Insert unit types
INSERT INTO unit_types (name, description, category) VALUES
    ('WEIGHT', 'Weight measurements', 'MASS'),
    ('VOLUME', 'Volume measurements', 'VOLUME'),
    ('LENGTH', 'Length measurements', 'LENGTH'),
    ('UNIT', 'Counting units', 'COUNT'),
    ('TIME', 'Time measurements', 'TIME');

-- Insert measurement units
INSERT INTO measurement_units (code, name, name_plural, type, unit_type_id, is_system_default, created_by, updated_by) VALUES
    -- Weight
    ('kg', 'Kilogram', 'Kilograms', 'WEIGHT', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('g', 'Gram', 'Grams', 'WEIGHT', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('mg', 'Milligram', 'Milligrams', 'WEIGHT', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('lb', 'Pound', 'Pounds', 'WEIGHT', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('oz', 'Ounce', 'Ounces', 'WEIGHT', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE, 'SYSTEM', 'SYSTEM'),
    -- Volume
    ('L', 'Liter', 'Liters', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('ml', 'Milliliter', 'Milliliters', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('cup', 'Cup', 'Cups', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('tbsp', 'Tablespoon', 'Tablespoons', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('tsp', 'Teaspoon', 'Teaspoons', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('gal', 'Gallon', 'Gallons', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('fl_oz', 'Fluid Ounce', 'Fluid Ounces', 'VOLUME', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE, 'SYSTEM', 'SYSTEM'),
    -- Count
    ('unit', 'Unit', 'Units', 'PIECE', (SELECT id FROM unit_types WHERE name = 'UNIT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('pc', 'Piece', 'Pieces', 'PIECE', (SELECT id FROM unit_types WHERE name = 'UNIT'), TRUE, 'SYSTEM', 'SYSTEM'),
    ('doz', 'Dozen', 'Dozens', 'PIECE', (SELECT id FROM unit_types WHERE name = 'UNIT'), TRUE, 'SYSTEM', 'SYSTEM');

-- Insert standard conversion factors
INSERT INTO conversion_factors (from_unit_id, to_unit_id, factor, is_system_default, created_by, updated_by) VALUES
    -- Weight conversions
    ((SELECT id FROM measurement_units WHERE code = 'kg'), (SELECT id FROM measurement_units WHERE code = 'g'), 1000, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'g'), (SELECT id FROM measurement_units WHERE code = 'kg'), 0.001, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'g'), (SELECT id FROM measurement_units WHERE code = 'mg'), 1000, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'mg'), (SELECT id FROM measurement_units WHERE code = 'g'), 0.001, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'kg'), (SELECT id FROM measurement_units WHERE code = 'lb'), 2.20462, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'lb'), (SELECT id FROM measurement_units WHERE code = 'kg'), 0.453592, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'lb'), (SELECT id FROM measurement_units WHERE code = 'oz'), 16, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'oz'), (SELECT id FROM measurement_units WHERE code = 'lb'), 0.0625, TRUE, 'SYSTEM', 'SYSTEM'),
    -- Volume conversions
    ((SELECT id FROM measurement_units WHERE code = 'L'), (SELECT id FROM measurement_units WHERE code = 'ml'), 1000, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'ml'), (SELECT id FROM measurement_units WHERE code = 'L'), 0.001, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'cup'), (SELECT id FROM measurement_units WHERE code = 'ml'), 236.588, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'ml'), (SELECT id FROM measurement_units WHERE code = 'cup'), 0.00422675, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'tbsp'), (SELECT id FROM measurement_units WHERE code = 'ml'), 14.7868, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'ml'), (SELECT id FROM measurement_units WHERE code = 'tbsp'), 0.067628, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'tsp'), (SELECT id FROM measurement_units WHERE code = 'ml'), 4.92892, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'ml'), (SELECT id FROM measurement_units WHERE code = 'tsp'), 0.202884, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'gal'), (SELECT id FROM measurement_units WHERE code = 'L'), 3.78541, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'L'), (SELECT id FROM measurement_units WHERE code = 'gal'), 0.264172, TRUE, 'SYSTEM', 'SYSTEM'),
    -- Count conversions
    ((SELECT id FROM measurement_units WHERE code = 'doz'), (SELECT id FROM measurement_units WHERE code = 'unit'), 12, TRUE, 'SYSTEM', 'SYSTEM'),
    ((SELECT id FROM measurement_units WHERE code = 'unit'), (SELECT id FROM measurement_units WHERE code = 'doz'), 0.0833333, TRUE, 'SYSTEM', 'SYSTEM');

-- Insert common allergens
INSERT INTO allergens (name, name_en, name_es, description, is_system_default, created_by, updated_by) VALUES
    ('Gluten', 'Gluten', 'Gluten', 'Contains wheat, barley, rye, or related grains', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Dairy', 'Dairy', 'Lácteos', 'Contains milk or milk products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Eggs', 'Eggs', 'Huevos', 'Contains eggs or egg products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Tree Nuts', 'Tree Nuts', 'Frutos Secos', 'Contains almonds, walnuts, cashews, etc.', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Peanuts', 'Peanuts', 'Cacahuates', 'Contains peanuts or peanut products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Soy', 'Soy', 'Soya', 'Contains soy or soy products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Fish', 'Fish', 'Pescado', 'Contains fish or fish products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Shellfish', 'Shellfish', 'Mariscos', 'Contains shellfish (shrimp, crab, lobster, etc.)', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Sesame', 'Sesame', 'Ajonjolí', 'Contains sesame seeds or sesame oil', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Sulfites', 'Sulfites', 'Sulfitos', 'Contains sulfur dioxide or sulfites', TRUE, 'SYSTEM', 'SYSTEM');

-- Insert common categories
INSERT INTO categories (name, description, is_system_default, created_by, updated_by) VALUES
    ('Flour & Grains', 'Flour, grains, and related products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Sugar & Sweeteners', 'Sugar, honey, syrups, and sweeteners', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Fats & Oils', 'Butter, oils, shortening, and fats', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Dairy', 'Milk, cream, cheese, and dairy products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Eggs', 'Eggs and egg products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Flavorings', 'Vanilla, extracts, spices, and flavorings', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Leavening Agents', 'Baking powder, yeast, baking soda', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Chocolate & Cocoa', 'Chocolate, cocoa powder, and related', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Nuts & Seeds', 'Nuts, seeds, and nut products', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Fruits', 'Fresh and dried fruits', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Decorations', 'Sprinkles, fondant, and decorations', TRUE, 'SYSTEM', 'SYSTEM'),
    ('Other', 'Miscellaneous ingredients', TRUE, 'SYSTEM', 'SYSTEM');

-- Insert default email templates (Spanish)
INSERT INTO email_templates (name, template_type, language, subject, html_body, text_body, is_system_template, is_default, variables_help, created_by, updated_by) VALUES
(
    'Cotización Enviada - ES',
    'QUOTE_SENT',
    'ES',
    'Su cotización {{quoteNumber}} está lista',
    '<html><body><h1>¡Hola {{customerName}}!</h1><p>Su cotización <strong>{{quoteNumber}}</strong> está lista.</p><p>Puede verla en el siguiente enlace: <a href="{{quoteLink}}">Ver Cotización</a></p><p>Esta cotización es válida hasta: {{validUntil}}</p><p>Saludos,<br>{{businessName}}</p></body></html>',
    'Hola {{customerName}}! Su cotización {{quoteNumber}} está lista. Puede verla en: {{quoteLink}}. Válida hasta: {{validUntil}}',
    TRUE,
    TRUE,
    '{"customerName":"Nombre del cliente","quoteNumber":"Número de cotización","quoteLink":"Enlace a la cotización","validUntil":"Fecha de validez","businessName":"Nombre del negocio"}',
    'SYSTEM',
    'SYSTEM'
),
(
    'Cambio de Precio - ES',
    'PRICE_CHANGE',
    'ES',
    'Alerta: Cambio de precio en {{materialName}}',
    '<html><body><h2>Cambio de Precio</h2><p>El material <strong>{{materialName}}</strong> ha tenido un cambio de precio:</p><ul><li>Precio anterior: {{oldPrice}}</li><li>Precio nuevo: {{newPrice}}</li><li>Cambio: {{changePercent}}%</li></ul><p>Esto puede afectar el costo de las siguientes recetas:</p>{{affectedRecipes}}</body></html>',
    'Cambio de precio en {{materialName}}. Precio anterior: {{oldPrice}}, Nuevo: {{newPrice}}, Cambio: {{changePercent}}%',
    TRUE,
    TRUE,
    '{"materialName":"Nombre del material","oldPrice":"Precio anterior","newPrice":"Precio nuevo","changePercent":"Porcentaje de cambio","affectedRecipes":"Lista de recetas afectadas"}',
    'SYSTEM',
    'SYSTEM'
),
(
    'Stock Bajo - ES',
    'LOW_STOCK',
    'ES',
    'Alerta: Stock bajo de {{materialName}}',
    '<html><body><h2>⚠️ Alerta de Stock Bajo</h2><p>El material <strong>{{materialName}}</strong> tiene stock bajo:</p><ul><li>Cantidad actual: {{currentQuantity}} {{unit}}</li><li>Cantidad mínima: {{minQuantity}} {{unit}}</li><li>Proveedor: {{supplier}}</li></ul><p>Se recomienda reabastecer pronto.</p></body></html>',
    'Stock bajo de {{materialName}}. Actual: {{currentQuantity}} {{unit}}, Mínimo: {{minQuantity}} {{unit}}',
    TRUE,
    TRUE,
    '{"materialName":"Nombre del material","currentQuantity":"Cantidad actual","minQuantity":"Cantidad mínima","unit":"Unidad de medida","supplier":"Proveedor"}',
    'SYSTEM',
    'SYSTEM'
);

-- Insert default email templates (English)
INSERT INTO email_templates (name, template_type, language, subject, html_body, text_body, is_system_template, is_default, variables_help, created_by, updated_by) VALUES
(
    'Quote Sent - EN',
    'QUOTE_SENT',
    'EN',
    'Your quote {{quoteNumber}} is ready',
    '<html><body><h1>Hello {{customerName}}!</h1><p>Your quote <strong>{{quoteNumber}}</strong> is ready.</p><p>You can view it at: <a href="{{quoteLink}}">View Quote</a></p><p>This quote is valid until: {{validUntil}}</p><p>Best regards,<br>{{businessName}}</p></body></html>',
    'Hello {{customerName}}! Your quote {{quoteNumber}} is ready. View at: {{quoteLink}}. Valid until: {{validUntil}}',
    TRUE,
    TRUE,
    '{"customerName":"Customer name","quoteNumber":"Quote number","quoteLink":"Quote link","validUntil":"Valid until date","businessName":"Business name"}',
    'SYSTEM',
    'SYSTEM'
),
(
    'Price Change - EN',
    'PRICE_CHANGE',
    'EN',
    'Alert: Price change in {{materialName}}',
    '<html><body><h2>Price Change Alert</h2><p>The material <strong>{{materialName}}</strong> has had a price change:</p><ul><li>Old price: {{oldPrice}}</li><li>New price: {{newPrice}}</li><li>Change: {{changePercent}}%</li></ul><p>This may affect the cost of the following recipes:</p>{{affectedRecipes}}</body></html>',
    'Price change in {{materialName}}. Old: {{oldPrice}}, New: {{newPrice}}, Change: {{changePercent}}%',
    TRUE,
    TRUE,
    '{"materialName":"Material name","oldPrice":"Old price","newPrice":"New price","changePercent":"Change percentage","affectedRecipes":"Affected recipes list"}',
    'SYSTEM',
    'SYSTEM'
),
(
    'Low Stock - EN',
    'LOW_STOCK',
    'EN',
    'Alert: Low stock of {{materialName}}',
    '<html><body><h2>⚠️ Low Stock Alert</h2><p>The material <strong>{{materialName}}</strong> has low stock:</p><ul><li>Current quantity: {{currentQuantity}} {{unit}}</li><li>Minimum quantity: {{minQuantity}} {{unit}}</li><li>Supplier: {{supplier}}</li></ul><p>Restocking is recommended soon.</p></body></html>',
    'Low stock of {{materialName}}. Current: {{currentQuantity}} {{unit}}, Minimum: {{minQuantity}} {{unit}}',
    TRUE,
    TRUE,
    '{"materialName":"Material name","currentQuantity":"Current quantity","minQuantity":"Minimum quantity","unit":"Unit of measure","supplier":"Supplier"}',
    'SYSTEM',
    'SYSTEM'
);

-- Insert default tax rates
INSERT INTO tax_rates (country_code, region_code, region_name, tax_name, tax_rate_percent, description, is_default, applies_to_food, created_by, updated_by) VALUES
('MX', NULL, 'México', 'IVA', 16.00, 'Impuesto al Valor Agregado general', TRUE, TRUE, 'SYSTEM', 'SYSTEM'),
('MX', 'BCN', 'Baja California Norte', 'IVA Frontera', 8.00, 'IVA reducido zona fronteriza', FALSE, TRUE, 'SYSTEM', 'SYSTEM'),
('US', 'CA', 'California', 'Sales Tax', 7.25, 'California state sales tax', FALSE, FALSE, 'SYSTEM', 'SYSTEM'),
('US', 'TX', 'Texas', 'Sales Tax', 6.25, 'Texas state sales tax', FALSE, FALSE, 'SYSTEM', 'SYSTEM'),
('US', 'NY', 'New York', 'Sales Tax', 4.00, 'New York state sales tax', FALSE, FALSE, 'SYSTEM', 'SYSTEM');

COMMIT;
