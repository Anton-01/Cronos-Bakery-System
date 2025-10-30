-- Initial Schema for Cronos Bakery System
-- Version 1.0 - Core Tables

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================================
-- USERS AND AUTHENTICATION
-- ================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret VARCHAR(500),
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    lockout_time TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    resource VARCHAR(100),
    action VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    expiry_date TIMESTAMP NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(500),
    location VARCHAR(200)
);

CREATE TABLE password_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(500) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- CORE BAKERY ENTITIES
-- ================================================

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    color VARCHAR(7),
    icon VARCHAR(100),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    is_system_category BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE (name, user_id)
);

CREATE TABLE allergens (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    severity VARCHAR(50),
    icon VARCHAR(100),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    is_system_allergen BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, user_id)
);

CREATE TABLE unit_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL
);

CREATE TABLE measurement_units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(20) NOT NULL UNIQUE,
    unit_type_id BIGINT NOT NULL REFERENCES unit_types(id),
    base_unit BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conversion_factors (
    id BIGSERIAL PRIMARY KEY,
    from_unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    to_unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    factor DECIMAL(20, 10) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    is_system_conversion BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (from_unit_id, to_unit_id, user_id)
);

CREATE TABLE raw_materials (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES categories(id),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    unit_cost DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    current_quantity DECIMAL(10, 2),
    min_quantity DECIMAL(10, 2),
    max_quantity DECIMAL(10, 2),
    supplier VARCHAR(500),
    sku VARCHAR(100),
    barcode VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE raw_material_allergens (
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    PRIMARY KEY (raw_material_id, allergen_id)
);

CREATE TABLE material_price_history (
    id BIGSERIAL PRIMARY KEY,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    old_price DECIMAL(10, 2) NOT NULL,
    new_price DECIMAL(10, 2) NOT NULL,
    change_percent DECIMAL(5, 2),
    change_reason VARCHAR(500),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100)
);

-- ================================================
-- RECIPES
-- ================================================

CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES categories(id),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    serving_size DECIMAL(10, 2),
    serving_unit VARCHAR(50),
    preparation_time_minutes INTEGER,
    cooking_time_minutes INTEGER,
    difficulty_level VARCHAR(20),
    instructions TEXT,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE recipe_versions (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    snapshot_data JSONB NOT NULL,
    change_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE (recipe_id, version_number)
);

CREATE TABLE recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id),
    quantity DECIMAL(10, 4) NOT NULL,
    unit_id BIGINT NOT NULL REFERENCES measurement_units(id),
    is_optional BOOLEAN NOT NULL DEFAULT FALSE,
    preparation_notes TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipe_sub_recipes (
    id BIGSERIAL PRIMARY KEY,
    parent_recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    sub_recipe_id BIGINT NOT NULL REFERENCES recipes(id),
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 1,
    is_optional BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredient_substitutes (
    id BIGSERIAL PRIMARY KEY,
    recipe_ingredient_id BIGINT NOT NULL REFERENCES recipe_ingredients(id) ON DELETE CASCADE,
    substitute_material_id BIGINT NOT NULL REFERENCES raw_materials(id),
    conversion_ratio DECIMAL(10, 4) NOT NULL DEFAULT 1.0,
    reason VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipe_fixed_costs (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    cost_type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    calculation_method VARCHAR(50) NOT NULL,
    fixed_amount DECIMAL(10, 2),
    time_based_rate DECIMAL(10, 2),
    percentage_of_cost DECIMAL(5, 2),
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipe_cost_history (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    version_number INTEGER,
    total_cost DECIMAL(10, 2) NOT NULL,
    ingredient_cost DECIMAL(10, 2) NOT NULL,
    fixed_cost DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    calculated_for_quantity DECIMAL(10, 2) NOT NULL DEFAULT 1.0
);

CREATE TABLE recipe_files (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    file_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size_bytes BIGINT,
    mime_type VARCHAR(100),
    description VARCHAR(500),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100)
);

-- ================================================
-- QUOTES
-- ================================================

CREATE TABLE profit_margins (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    margin_percent DECIMAL(5, 2) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_name VARCHAR(200) NOT NULL,
    customer_email VARCHAR(200),
    customer_phone VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2),
    total_amount DECIMAL(10, 2) NOT NULL,
    profit_margin_id BIGINT REFERENCES profit_margins(id),
    custom_margin_percent DECIMAL(5, 2),
    notes TEXT,
    internal_notes TEXT,
    valid_until TIMESTAMP,
    share_token VARCHAR(100) UNIQUE,
    share_expires_at TIMESTAMP,
    is_shared BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE quote_items (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    recipe_id BIGINT REFERENCES recipes(id),
    item_name VARCHAR(200) NOT NULL,
    description TEXT,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_cost DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    margin_percent DECIMAL(5, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quote_access_logs (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    accessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    location VARCHAR(200),
    referrer VARCHAR(500)
);

-- ================================================
-- INDEXES FOR PERFORMANCE
-- ================================================

-- Users indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(is_enabled);

-- Authentication indexes
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_login_history_user ON login_history(user_id);
CREATE INDEX idx_login_history_time ON login_history(login_time);

-- Categories and allergens
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_categories_active ON categories(is_active);
CREATE INDEX idx_allergens_user ON allergens(user_id);

-- Raw materials
CREATE INDEX idx_raw_materials_user ON raw_materials(user_id);
CREATE INDEX idx_raw_materials_category ON raw_materials(category_id);
CREATE INDEX idx_raw_materials_active ON raw_materials(is_active);
CREATE INDEX idx_raw_materials_name ON raw_materials(name);
CREATE INDEX idx_price_history_material ON material_price_history(raw_material_id);
CREATE INDEX idx_price_history_date ON material_price_history(changed_at);

-- Recipes
CREATE INDEX idx_recipes_user ON recipes(user_id);
CREATE INDEX idx_recipes_category ON recipes(category_id);
CREATE INDEX idx_recipes_active ON recipes(is_active);
CREATE INDEX idx_recipes_name ON recipes(name);
CREATE INDEX idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id);
CREATE INDEX idx_recipe_ingredients_material ON recipe_ingredients(raw_material_id);
CREATE INDEX idx_recipe_versions_recipe ON recipe_versions(recipe_id);
CREATE INDEX idx_recipe_cost_history_recipe ON recipe_cost_history(recipe_id);

-- Quotes
CREATE INDEX idx_quotes_user ON quotes(user_id);
CREATE INDEX idx_quotes_number ON quotes(quote_number);
CREATE INDEX idx_quotes_status ON quotes(status);
CREATE INDEX idx_quotes_share_token ON quotes(share_token);
CREATE INDEX idx_quotes_created ON quotes(created_at);
CREATE INDEX idx_quote_items_quote ON quote_items(quote_id);
CREATE INDEX idx_quote_access_logs_quote ON quote_access_logs(quote_id);
CREATE INDEX idx_quote_access_logs_time ON quote_access_logs(accessed_at);

-- Conversion factors
CREATE INDEX idx_conversion_from_unit ON conversion_factors(from_unit_id);
CREATE INDEX idx_conversion_to_unit ON conversion_factors(to_unit_id);
CREATE INDEX idx_conversion_user ON conversion_factors(user_id);

-- ================================================
-- INITIAL SYSTEM DATA
-- ================================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN', 'Administrator role with full access'),
    ('ROLE_USER', 'Standard user role'),
    ('ROLE_VIEWER', 'Read-only access role');

-- Insert unit types
INSERT INTO unit_types (name, description, category) VALUES
    ('WEIGHT', 'Weight measurements', 'MASS'),
    ('VOLUME', 'Volume measurements', 'VOLUME'),
    ('LENGTH', 'Length measurements', 'LENGTH'),
    ('UNIT', 'Counting units', 'COUNT'),
    ('TIME', 'Time measurements', 'TIME');

-- Insert measurement units
INSERT INTO measurement_units (name, abbreviation, unit_type_id, base_unit) VALUES
    -- Weight
    ('Kilogram', 'kg', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), TRUE),
    ('Gram', 'g', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), FALSE),
    ('Milligram', 'mg', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), FALSE),
    ('Pound', 'lb', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), FALSE),
    ('Ounce', 'oz', (SELECT id FROM unit_types WHERE name = 'WEIGHT'), FALSE),
    -- Volume
    ('Liter', 'L', (SELECT id FROM unit_types WHERE name = 'VOLUME'), TRUE),
    ('Milliliter', 'ml', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    ('Cup', 'cup', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    ('Tablespoon', 'tbsp', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    ('Teaspoon', 'tsp', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    ('Gallon', 'gal', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    ('Fluid Ounce', 'fl oz', (SELECT id FROM unit_types WHERE name = 'VOLUME'), FALSE),
    -- Count
    ('Unit', 'unit', (SELECT id FROM unit_types WHERE name = 'UNIT'), TRUE),
    ('Piece', 'pc', (SELECT id FROM unit_types WHERE name = 'UNIT'), FALSE),
    ('Dozen', 'doz', (SELECT id FROM unit_types WHERE name = 'UNIT'), FALSE);

-- Insert standard conversion factors
INSERT INTO conversion_factors (from_unit_id, to_unit_id, factor, is_system_conversion) VALUES
    -- Weight conversions
    ((SELECT id FROM measurement_units WHERE abbreviation = 'kg'), (SELECT id FROM measurement_units WHERE abbreviation = 'g'), 1000, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'g'), (SELECT id FROM measurement_units WHERE abbreviation = 'kg'), 0.001, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'g'), (SELECT id FROM measurement_units WHERE abbreviation = 'mg'), 1000, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'mg'), (SELECT id FROM measurement_units WHERE abbreviation = 'g'), 0.001, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'kg'), (SELECT id FROM measurement_units WHERE abbreviation = 'lb'), 2.20462, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'lb'), (SELECT id FROM measurement_units WHERE abbreviation = 'kg'), 0.453592, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'lb'), (SELECT id FROM measurement_units WHERE abbreviation = 'oz'), 16, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'oz'), (SELECT id FROM measurement_units WHERE abbreviation = 'lb'), 0.0625, TRUE),
    -- Volume conversions
    ((SELECT id FROM measurement_units WHERE abbreviation = 'L'), (SELECT id FROM measurement_units WHERE abbreviation = 'ml'), 1000, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'ml'), (SELECT id FROM measurement_units WHERE abbreviation = 'L'), 0.001, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'cup'), (SELECT id FROM measurement_units WHERE abbreviation = 'ml'), 236.588, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'ml'), (SELECT id FROM measurement_units WHERE abbreviation = 'cup'), 0.00422675, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'tbsp'), (SELECT id FROM measurement_units WHERE abbreviation = 'ml'), 14.7868, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'ml'), (SELECT id FROM measurement_units WHERE abbreviation = 'tbsp'), 0.067628, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'tsp'), (SELECT id FROM measurement_units WHERE abbreviation = 'ml'), 4.92892, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'ml'), (SELECT id FROM measurement_units WHERE abbreviation = 'tsp'), 0.202884, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'gal'), (SELECT id FROM measurement_units WHERE abbreviation = 'L'), 3.78541, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'L'), (SELECT id FROM measurement_units WHERE abbreviation = 'gal'), 0.264172, TRUE),
    -- Count conversions
    ((SELECT id FROM measurement_units WHERE abbreviation = 'doz'), (SELECT id FROM measurement_units WHERE abbreviation = 'unit'), 12, TRUE),
    ((SELECT id FROM measurement_units WHERE abbreviation = 'unit'), (SELECT id FROM measurement_units WHERE abbreviation = 'doz'), 0.0833333, TRUE);

-- Insert common allergens
INSERT INTO allergens (name, description, severity, is_system_allergen) VALUES
    ('Gluten', 'Contains wheat, barley, rye, or related grains', 'HIGH', TRUE),
    ('Dairy', 'Contains milk or milk products', 'MEDIUM', TRUE),
    ('Eggs', 'Contains eggs or egg products', 'MEDIUM', TRUE),
    ('Tree Nuts', 'Contains almonds, walnuts, cashews, etc.', 'HIGH', TRUE),
    ('Peanuts', 'Contains peanuts or peanut products', 'HIGH', TRUE),
    ('Soy', 'Contains soy or soy products', 'MEDIUM', TRUE),
    ('Fish', 'Contains fish or fish products', 'HIGH', TRUE),
    ('Shellfish', 'Contains shellfish (shrimp, crab, lobster, etc.)', 'HIGH', TRUE),
    ('Sesame', 'Contains sesame seeds or sesame oil', 'MEDIUM', TRUE),
    ('Sulfites', 'Contains sulfur dioxide or sulfites', 'LOW', TRUE);

-- Insert common categories
INSERT INTO categories (name, description, color, is_system_category) VALUES
    ('Flour & Grains', 'Flour, grains, and related products', '#8B4513', TRUE),
    ('Sugar & Sweeteners', 'Sugar, honey, syrups, and sweeteners', '#FFB6C1', TRUE),
    ('Fats & Oils', 'Butter, oils, shortening, and fats', '#FFD700', TRUE),
    ('Dairy', 'Milk, cream, cheese, and dairy products', '#87CEEB', TRUE),
    ('Eggs', 'Eggs and egg products', '#FFFACD', TRUE),
    ('Flavorings', 'Vanilla, extracts, spices, and flavorings', '#9370DB', TRUE),
    ('Leavening Agents', 'Baking powder, yeast, baking soda', '#F0E68C', TRUE),
    ('Chocolate & Cocoa', 'Chocolate, cocoa powder, and related', '#8B4513', TRUE),
    ('Nuts & Seeds', 'Nuts, seeds, and nut products', '#D2691E', TRUE),
    ('Fruits', 'Fresh and dried fruits', '#FF6347', TRUE),
    ('Decorations', 'Sprinkles, fondant, and decorations', '#FF69B4', TRUE),
    ('Other', 'Miscellaneous ingredients', '#808080', TRUE);

COMMIT;
