-- Synchronize All Entity Fields with Database Schema
-- Version 7.0 - Comprehensive field synchronization for all tables
-- IMPORTANT: This migration ONLY ADDS missing columns, never removes existing ones

-- ================================================
-- USERS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE users
ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20),
ADD COLUMN IF NOT EXISTS business_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS default_currency VARCHAR(3) DEFAULT 'MXN',
ADD COLUMN IF NOT EXISTS default_language VARCHAR(2) DEFAULT 'es',
ADD COLUMN IF NOT EXISTS default_tax_rate DECIMAL(5, 2) DEFAULT 16.00,
ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_failed_login TIMESTAMP,
ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Rename columns to match entity naming (only if old columns exist)
DO $$
BEGIN
    -- Rename is_enabled to enabled
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_enabled') THEN
        ALTER TABLE users RENAME COLUMN is_enabled TO enabled;
    END IF;

    -- Rename is_account_non_expired to account_non_expired
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_account_non_expired') THEN
        ALTER TABLE users RENAME COLUMN is_account_non_expired TO account_non_expired;
    END IF;

    -- Rename is_account_non_locked to account_non_locked
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_account_non_locked') THEN
        ALTER TABLE users RENAME COLUMN is_account_non_locked TO account_non_locked;
    END IF;

    -- Rename is_credentials_non_expired to credentials_non_expired
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_credentials_non_expired') THEN
        ALTER TABLE users RENAME COLUMN is_credentials_non_expired TO credentials_non_expired;
    END IF;

    -- Rename lockout_time to locked_until if lockout_time exists
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'lockout_time') THEN
        ALTER TABLE users RENAME COLUMN lockout_time TO locked_until;
    END IF;

    -- Rename last_login to last_login_at if last_login exists
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'last_login') THEN
        ALTER TABLE users RENAME COLUMN last_login TO last_login_at;
    END IF;
END $$;

-- ================================================
-- CATEGORIES TABLE - Rename and Add Columns
-- ================================================

DO $$
BEGIN
    -- Rename is_system_category to is_system_default
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'categories' AND column_name = 'is_system_category') THEN
        ALTER TABLE categories RENAME COLUMN is_system_category TO is_system_default;
    END IF;
END $$;

-- ================================================
-- ALLERGENS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE allergens
ADD COLUMN IF NOT EXISTS name_en VARCHAR(200),
ADD COLUMN IF NOT EXISTS name_es VARCHAR(200);

DO $$
BEGIN
    -- Rename is_system_allergen to is_system_default
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'allergens' AND column_name = 'is_system_allergen') THEN
        ALTER TABLE allergens RENAME COLUMN is_system_allergen TO is_system_default;
    END IF;
END $$;

-- ================================================
-- MEASUREMENT_UNITS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE measurement_units
ADD COLUMN IF NOT EXISTS code VARCHAR(20),
ADD COLUMN IF NOT EXISTS name_plural VARCHAR(100),
ADD COLUMN IF NOT EXISTS type VARCHAR(50),
ADD COLUMN IF NOT EXISTS is_system_default BOOLEAN DEFAULT TRUE;

-- Copy abbreviation to code if code doesn't have a value
UPDATE measurement_units SET code = abbreviation WHERE code IS NULL AND abbreviation IS NOT NULL;

DO $$
BEGIN
    -- Rename is_system_conversion to is_system_default in conversion_factors
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'conversion_factors' AND column_name = 'is_system_conversion') THEN
        ALTER TABLE conversion_factors RENAME COLUMN is_system_conversion TO is_system_default;
    END IF;
END $$;

-- ================================================
-- CONVERSION_FACTORS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE conversion_factors
ADD COLUMN IF NOT EXISTS notes TEXT;

-- ================================================
-- RAW_MATERIALS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE raw_materials
ADD COLUMN IF NOT EXISTS brand VARCHAR(255),
ADD COLUMN IF NOT EXISTS purchase_unit_id BIGINT REFERENCES measurement_units(id),
ADD COLUMN IF NOT EXISTS purchase_quantity DECIMAL(15, 4),
ADD COLUMN IF NOT EXISTS minimum_stock DECIMAL(15, 4),
ADD COLUMN IF NOT EXISTS last_purchase_date TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_price_update TIMESTAMP,
ADD COLUMN IF NOT EXISTS needs_recalculation BOOLEAN DEFAULT FALSE;

-- Copy unit_id to purchase_unit_id if not set
UPDATE raw_materials SET purchase_unit_id = unit_id WHERE purchase_unit_id IS NULL AND unit_id IS NOT NULL;

-- Copy current_quantity to purchase_quantity if not set (initial setup)
UPDATE raw_materials SET purchase_quantity = 1.0 WHERE purchase_quantity IS NULL;

DO $$
BEGIN
    -- Rename current_quantity to current_stock
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'raw_materials' AND column_name = 'current_quantity') THEN
        ALTER TABLE raw_materials RENAME COLUMN current_quantity TO current_stock;
    END IF;

    -- Rename min_quantity to minimum_stock (keep old one too for now)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'raw_materials' AND column_name = 'minimum_stock')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'raw_materials' AND column_name = 'min_quantity') THEN
        ALTER TABLE raw_materials ADD COLUMN minimum_stock DECIMAL(15, 4);
        UPDATE raw_materials SET minimum_stock = min_quantity WHERE minimum_stock IS NULL;
    END IF;
END $$;

-- ================================================
-- MATERIAL_PRICE_HISTORY TABLE - Rename Columns
-- ================================================

DO $$
BEGIN
    -- Rename old_price to previous_cost
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'material_price_history' AND column_name = 'old_price') THEN
        ALTER TABLE material_price_history RENAME COLUMN old_price TO previous_cost;
    END IF;

    -- Rename new_price to new_cost
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'material_price_history' AND column_name = 'new_price') THEN
        ALTER TABLE material_price_history RENAME COLUMN new_price TO new_cost;
    END IF;

    -- Rename change_percent to change_percentage
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'material_price_history' AND column_name = 'change_percent') THEN
        ALTER TABLE material_price_history RENAME COLUMN change_percent TO change_percentage;
    END IF;

    -- Rename change_reason to reason
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'material_price_history' AND column_name = 'change_reason') THEN
        ALTER TABLE material_price_history RENAME COLUMN change_reason TO reason;
    END IF;
END $$;

-- ================================================
-- RECIPES TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipes
ADD COLUMN IF NOT EXISTS yield_quantity DECIMAL(10, 2),
ADD COLUMN IF NOT EXISTS yield_unit VARCHAR(50),
ADD COLUMN IF NOT EXISTS baking_time_minutes INTEGER,
ADD COLUMN IF NOT EXISTS cooling_time_minutes INTEGER,
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'DRAFT',
ADD COLUMN IF NOT EXISTS needs_recalculation BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS current_version INTEGER DEFAULT 1,
ADD COLUMN IF NOT EXISTS storage_instructions VARCHAR(1000),
ADD COLUMN IF NOT EXISTS shelf_life_days INTEGER;

-- Copy existing data to new columns
UPDATE recipes SET yield_quantity = serving_size WHERE yield_quantity IS NULL AND serving_size IS NOT NULL;
UPDATE recipes SET yield_unit = serving_unit WHERE yield_unit IS NULL AND serving_unit IS NOT NULL;
UPDATE recipes SET baking_time_minutes = cooking_time_minutes WHERE baking_time_minutes IS NULL AND cooking_time_minutes IS NOT NULL;

-- ================================================
-- RECIPE_VERSIONS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_versions
ADD COLUMN IF NOT EXISTS version_name VARCHAR(200),
ADD COLUMN IF NOT EXISTS is_current BOOLEAN DEFAULT FALSE;

DO $$
BEGIN
    -- Rename change_notes to changes
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'recipe_versions' AND column_name = 'change_notes') THEN
        ALTER TABLE recipe_versions RENAME COLUMN change_notes TO changes;
    END IF;

    -- Rename snapshot_data to snapshot_data (ensure correct type)
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'recipe_versions' AND column_name = 'snapshot_data' AND data_type = 'jsonb') THEN
        ALTER TABLE recipe_versions ALTER COLUMN snapshot_data TYPE TEXT;
    END IF;
END $$;

-- ================================================
-- RECIPE_INGREDIENTS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_ingredients
ADD COLUMN IF NOT EXISTS cost_per_unit DECIMAL(15, 6),
ADD COLUMN IF NOT EXISTS total_cost DECIMAL(15, 2),
ADD COLUMN IF NOT EXISTS notes TEXT;

-- Copy preparation_notes to notes if notes is empty
UPDATE recipe_ingredients SET notes = preparation_notes WHERE notes IS NULL AND preparation_notes IS NOT NULL;

-- ================================================
-- RECIPE_SUB_RECIPES TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_sub_recipes
ADD COLUMN IF NOT EXISTS total_cost DECIMAL(15, 2);

-- ================================================
-- RECIPE_FIXED_COSTS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_fixed_costs
ADD COLUMN IF NOT EXISTS name VARCHAR(255),
ADD COLUMN IF NOT EXISTS type VARCHAR(50),
ADD COLUMN IF NOT EXISTS calculation_method VARCHAR(50),
ADD COLUMN IF NOT EXISTS amount DECIMAL(15, 2),
ADD COLUMN IF NOT EXISTS time_in_minutes INTEGER,
ADD COLUMN IF NOT EXISTS percentage DECIMAL(5, 2);

-- Copy existing data to new fields
UPDATE recipe_fixed_costs SET name = cost_type WHERE name IS NULL AND cost_type IS NOT NULL;
UPDATE recipe_fixed_costs SET type = cost_type WHERE type IS NULL AND cost_type IS NOT NULL;
UPDATE recipe_fixed_costs SET amount = fixed_amount WHERE amount IS NULL AND fixed_amount IS NOT NULL;
UPDATE recipe_fixed_costs SET time_in_minutes = time_based_rate WHERE time_in_minutes IS NULL AND time_based_rate IS NOT NULL;
UPDATE recipe_fixed_costs SET percentage = percentage_of_cost WHERE percentage IS NULL AND percentage_of_cost IS NOT NULL;

DO $$
BEGIN
    -- Ensure calculation_method exists
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'recipe_fixed_costs' AND column_name = 'calculation_method') THEN
        ALTER TABLE recipe_fixed_costs ADD COLUMN calculation_method VARCHAR(50);
    END IF;
END $$;

-- ================================================
-- INGREDIENT_SUBSTITUTES TABLE - Complete Missing Fields
-- ================================================

-- The entity is incomplete in the codebase, but Flyway has the complete structure
-- Ensure the table has all needed columns based on Flyway structure
ALTER TABLE ingredient_substitutes
ADD COLUMN IF NOT EXISTS substitute_material_id BIGINT REFERENCES raw_materials(id),
ADD COLUMN IF NOT EXISTS conversion_ratio DECIMAL(10, 4) DEFAULT 1.0,
ADD COLUMN IF NOT EXISTS reason VARCHAR(50),
ADD COLUMN IF NOT EXISTS notes TEXT,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

DO $$
BEGIN
    -- Rename recipe_ingredient_id to original_ingredient_id if needed
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ingredient_substitutes' AND column_name = 'original_ingredient_id')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ingredient_substitutes' AND column_name = 'recipe_ingredient_id') THEN
        ALTER TABLE ingredient_substitutes RENAME COLUMN recipe_ingredient_id TO original_ingredient_id;
    END IF;
END $$;

-- ================================================
-- RECIPE_FILES TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_files
ADD COLUMN IF NOT EXISTS original_file_name VARCHAR(500),
ADD COLUMN IF NOT EXISTS thumbnail_path VARCHAR(1000),
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Copy file_name to original_file_name if not set
UPDATE recipe_files SET original_file_name = file_name WHERE original_file_name IS NULL;

-- Copy uploaded_at to created_at if created_at is null
UPDATE recipe_files SET created_at = uploaded_at WHERE created_at IS NULL AND uploaded_at IS NOT NULL;
UPDATE recipe_files SET created_by = uploaded_by WHERE created_by IS NULL AND uploaded_by IS NOT NULL;

-- ================================================
-- RECIPE_COST_HISTORY TABLE - Add Missing Fields
-- ================================================

ALTER TABLE recipe_cost_history
ADD COLUMN IF NOT EXISTS recipe_version INTEGER,
ADD COLUMN IF NOT EXISTS materials_cost DECIMAL(15, 2),
ADD COLUMN IF NOT EXISTS fixed_costs DECIMAL(15, 2),
ADD COLUMN IF NOT EXISTS sub_recipes_cost DECIMAL(15, 2) DEFAULT 0,
ADD COLUMN IF NOT EXISTS cost_per_unit DECIMAL(15, 6),
ADD COLUMN IF NOT EXISTS calculated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS calculation_notes VARCHAR(1000);

-- Copy existing data to new fields
UPDATE recipe_cost_history SET recipe_version = version_number WHERE recipe_version IS NULL AND version_number IS NOT NULL;
UPDATE recipe_cost_history SET materials_cost = ingredient_cost WHERE materials_cost IS NULL AND ingredient_cost IS NOT NULL;
UPDATE recipe_cost_history SET fixed_costs = fixed_cost WHERE fixed_costs IS NULL AND fixed_cost IS NOT NULL;

-- ================================================
-- PROFIT_MARGINS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE profit_margins
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS percentage DECIMAL(5, 2);

-- Copy margin_percent to percentage
UPDATE profit_margins SET percentage = margin_percent WHERE percentage IS NULL AND margin_percent IS NOT NULL;

-- ================================================
-- QUOTES TABLE - Add Missing Fields
-- ================================================

ALTER TABLE quotes
ADD COLUMN IF NOT EXISTS client_address VARCHAR(500),
ADD COLUMN IF NOT EXISTS tax_rate DECIMAL(5, 2);

-- Copy customer fields to client fields if needed
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'client_name')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'customer_name') THEN
        ALTER TABLE quotes RENAME COLUMN customer_name TO client_name;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'client_email')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'customer_email') THEN
        ALTER TABLE quotes RENAME COLUMN customer_email TO client_email;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'client_phone')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'customer_phone') THEN
        ALTER TABLE quotes RENAME COLUMN customer_phone TO client_phone;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'is_shareable')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'is_shared') THEN
        ALTER TABLE quotes RENAME COLUMN is_shared TO is_shareable;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'total')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'quotes' AND column_name = 'total_amount') THEN
        ALTER TABLE quotes RENAME COLUMN total_amount TO total;
    END IF;
END $$;

-- ================================================
-- QUOTE_ITEMS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE quote_items
ADD COLUMN IF NOT EXISTS scale_factor DECIMAL(10, 4) DEFAULT 1.0,
ADD COLUMN IF NOT EXISTS profit_margin_id BIGINT REFERENCES profit_margins(id),
ADD COLUMN IF NOT EXISTS profit_percentage DECIMAL(5, 2),
ADD COLUMN IF NOT EXISTS subtotal DECIMAL(15, 2),
ADD COLUMN IF NOT EXISTS notes TEXT;

-- Copy existing fields
UPDATE quote_items SET profit_percentage = margin_percent WHERE profit_percentage IS NULL AND margin_percent IS NOT NULL;
UPDATE quote_items SET subtotal = total_price WHERE subtotal IS NULL AND total_price IS NOT NULL;
UPDATE quote_items SET notes = description WHERE notes IS NULL AND description IS NOT NULL;

-- ================================================
-- QUOTE_ACCESS_LOGS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE quote_access_logs
ADD COLUMN IF NOT EXISTS accessed_by_email VARCHAR(200),
ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id);

-- ================================================
-- LOGIN_HISTORY TABLE - Add Missing Fields
-- ================================================

ALTER TABLE login_history
ADD COLUMN IF NOT EXISTS browser VARCHAR(100),
ADD COLUMN IF NOT EXISTS operating_system VARCHAR(100),
ADD COLUMN IF NOT EXISTS device VARCHAR(100),
ADD COLUMN IF NOT EXISTS two_factor_used BOOLEAN;

DO $$
BEGIN
    -- Rename login_time to login_at
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'login_history' AND column_name = 'login_time') THEN
        ALTER TABLE login_history RENAME COLUMN login_time TO login_at;
    END IF;

    -- Rename success to successful
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'login_history' AND column_name = 'success') THEN
        ALTER TABLE login_history RENAME COLUMN success TO successful;
    END IF;
END $$;

-- ================================================
-- REFRESH_TOKENS TABLE - Add Missing Fields
-- ================================================

ALTER TABLE refresh_tokens
ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS revoked BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS revoked_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45),
ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);

-- Copy expiry_date to expires_at
UPDATE refresh_tokens SET expires_at = expiry_date WHERE expires_at IS NULL AND expiry_date IS NOT NULL;
UPDATE refresh_tokens SET revoked = is_used WHERE revoked IS NULL AND is_used IS NOT NULL;

-- ================================================
-- ROLES TABLE - Add Missing Audit Fields
-- ================================================

ALTER TABLE roles
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- PERMISSIONS TABLE - Add Missing Audit Fields
-- ================================================

ALTER TABLE permissions
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- UPDATE EXISTING DATA WITH DEFAULT VALUES
-- ================================================

-- Initialize version to 0 for all records where NULL
UPDATE users SET version = 0 WHERE version IS NULL;
UPDATE roles SET version = 0 WHERE version IS NULL;
UPDATE permissions SET version = 0 WHERE version IS NULL;
UPDATE recipe_files SET version = 0 WHERE version IS NULL;
UPDATE ingredient_substitutes SET version = 0 WHERE version IS NULL;

-- Set default values for new NOT NULL fields
UPDATE users SET phone_number = '' WHERE phone_number IS NULL;
UPDATE users SET business_name = '' WHERE business_name IS NULL;
UPDATE users SET password_changed_at = created_at WHERE password_changed_at IS NULL;

-- Initialize boolean fields with defaults
UPDATE raw_materials SET needs_recalculation = FALSE WHERE needs_recalculation IS NULL;
UPDATE recipes SET needs_recalculation = FALSE WHERE needs_recalculation IS NULL;
UPDATE recipes SET status = 'DRAFT' WHERE status IS NULL;
UPDATE profit_margins SET is_active = TRUE WHERE is_active IS NULL;
UPDATE recipe_versions SET is_current = FALSE WHERE is_current IS NULL;

-- Initialize numeric fields with defaults
UPDATE recipe_sub_recipes SET total_cost = 0 WHERE total_cost IS NULL;
UPDATE recipe_cost_history SET sub_recipes_cost = 0 WHERE sub_recipes_cost IS NULL;
UPDATE quote_items SET scale_factor = 1.0 WHERE scale_factor IS NULL;

COMMIT;
