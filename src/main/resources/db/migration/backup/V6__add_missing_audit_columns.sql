-- Add Missing Audit Columns to Tables
-- Version 6.0 - Adds created_by, updated_by, and version columns to AuditableEntity tables

-- ================================================
-- ADD MISSING COLUMNS TO ALLERGENS
-- ================================================

ALTER TABLE allergens
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO CATEGORIES
-- ================================================

ALTER TABLE categories
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO MEASUREMENT_UNITS
-- ================================================

ALTER TABLE measurement_units
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO RAW_MATERIALS
-- ================================================

ALTER TABLE raw_materials
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- FIX VERSION COLUMN TYPE IN RECIPES
-- ================================================

-- Change version from INTEGER to BIGINT in recipes table
ALTER TABLE recipes
ALTER COLUMN version TYPE BIGINT;

-- Add version column if it doesn't exist (for safety)
ALTER TABLE recipes
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO RECIPE_INGREDIENTS
-- ================================================

ALTER TABLE recipe_ingredients
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO RECIPE_FIXED_COSTS
-- ================================================

ALTER TABLE recipe_fixed_costs
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO RECIPE_SUB_RECIPES
-- ================================================

ALTER TABLE recipe_sub_recipes
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO PROFIT_MARGINS
-- ================================================

ALTER TABLE profit_margins
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO QUOTES
-- ================================================

ALTER TABLE quotes
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO QUOTE_ITEMS
-- ================================================

ALTER TABLE quote_items
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- ADD MISSING COLUMNS TO CONVERSION_FACTORS
-- ================================================

ALTER TABLE conversion_factors
ADD COLUMN IF NOT EXISTS created_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- ================================================
-- UPDATE EXISTING DATA WITH DEFAULT VALUES
-- ================================================

-- Set created_by to 'SYSTEM' for existing records without it
UPDATE allergens SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE measurement_units SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE recipe_ingredients SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE recipe_fixed_costs SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE recipe_sub_recipes SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE profit_margins SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE quote_items SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE conversion_factors SET created_by = 'SYSTEM' WHERE created_by IS NULL;

-- Set updated_at to created_at for existing records without it
UPDATE measurement_units SET updated_at = created_at WHERE updated_at IS NULL OR updated_at = CURRENT_TIMESTAMP;
UPDATE recipe_ingredients SET updated_at = created_at WHERE updated_at IS NULL OR updated_at = CURRENT_TIMESTAMP;
UPDATE recipe_fixed_costs SET updated_at = created_at WHERE updated_at IS NULL OR updated_at = CURRENT_TIMESTAMP;
UPDATE recipe_sub_recipes SET updated_at = created_at WHERE updated_at IS NULL OR updated_at = CURRENT_TIMESTAMP;
UPDATE quote_items SET updated_at = created_at WHERE updated_at IS NULL OR updated_at = CURRENT_TIMESTAMP;

-- Initialize version to 0 for all records
UPDATE allergens SET version = 0 WHERE version IS NULL;
UPDATE categories SET version = 0 WHERE version IS NULL;
UPDATE measurement_units SET version = 0 WHERE version IS NULL;
UPDATE raw_materials SET version = 0 WHERE version IS NULL;
UPDATE recipes SET version = 0 WHERE version IS NULL;
UPDATE recipe_ingredients SET version = 0 WHERE version IS NULL;
UPDATE recipe_fixed_costs SET version = 0 WHERE version IS NULL;
UPDATE recipe_sub_recipes SET version = 0 WHERE version IS NULL;
UPDATE profit_margins SET version = 0 WHERE version IS NULL;
UPDATE quotes SET version = 0 WHERE version IS NULL;
UPDATE quote_items SET version = 0 WHERE version IS NULL;
UPDATE conversion_factors SET version = 0 WHERE version IS NULL;

COMMIT;
