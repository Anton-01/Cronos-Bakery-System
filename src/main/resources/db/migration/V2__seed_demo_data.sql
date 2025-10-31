-- Demo Data for Cronos Bakery System
-- Version 2.0 - Sample data for testing and demonstration

-- Note: This migration can be skipped in production

-- ================================================
-- DEMO USERS
-- ================================================

-- Password: demo123 (BCrypt hash)
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_by, updated_by) VALUES
('demo_baker', 'demo@cronosbakery.com', '$2a$10$rBL.5z5z5z5z5z5z5z5z5uN5N5N5N5N5N5N5N5N5N5N5N5N5N', 'Demo', 'Baker', TRUE, 'SYSTEM', 'SYSTEM'),
('pastry_chef', 'chef@cronosbakery.com', '$2a$10$rBL.5z5z5z5z5z5z5z5z5uN5N5N5N5N5N5N5N5N5N5N5N5N5N', 'Maria', 'Rodriguez', TRUE, 'SYSTEM', 'SYSTEM');

-- Assign roles to demo users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username IN ('demo_baker', 'pastry_chef')
AND r.name = 'ROLE_USER';

-- ================================================
-- DEMO BRANDING SETTINGS
-- ================================================

INSERT INTO branding_settings (user_id, business_name, company_slogan, primary_color, secondary_color, accent_color, phone, email, created_by, updated_by)
SELECT
    id,
    'Sweet Dreams Bakery',
    'Endulzando tus momentos especiales',
    '#FF6B9D',
    '#C44569',
    '#FFA07A',
    '+52 123 456 7890',
    email,
    'SYSTEM',
    'SYSTEM'
FROM users WHERE username = 'demo_baker';

-- ================================================
-- DEMO RAW MATERIALS
-- ================================================

INSERT INTO raw_materials (name, description, brand, supplier, category_id, user_id, purchase_unit_id, purchase_quantity, unit_cost, current_stock, minimum_stock, created_by, updated_by)
SELECT
    'Harina de trigo todo uso',
    'Harina refinada para repostería general',
    'Molinos El Sol',
    'Molinos El Sol',
    (SELECT id FROM categories WHERE name = 'Flour & Grains' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'kg'),
    1.0,
    45.00,
    50.0,
    10.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Azúcar refinada',
    'Azúcar blanca refinada',
    'Azucarera del Valle',
    'Azucarera del Valle',
    (SELECT id FROM categories WHERE name = 'Sugar & Sweeteners' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'kg'),
    1.0,
    28.00,
    40.0,
    10.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Mantequilla sin sal',
    'Mantequilla de alta calidad para repostería',
    'Lácteos Premium',
    'Lácteos Premium',
    (SELECT id FROM categories WHERE name = 'Fats & Oils' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'kg'),
    1.0,
    180.00,
    15.0,
    5.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Huevos frescos',
    'Huevos de gallina tamaño grande',
    'Granja Los Arcos',
    'Granja Los Arcos',
    (SELECT id FROM categories WHERE name = 'Eggs' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'unit'),
    1.0,
    3.50,
    120.0,
    24.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Leche entera',
    'Leche entera pasteurizada',
    'Lácteos del Norte',
    'Lácteos del Norte',
    (SELECT id FROM categories WHERE name = 'Dairy' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'L'),
    1.0,
    22.00,
    20.0,
    5.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Polvo para hornear',
    'Levadura química para repostería',
    'Insumos Baker',
    'Insumos Baker',
    (SELECT id FROM categories WHERE name = 'Leavening Agents' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'g'),
    1000.0,
    0.15,
    2000.0,
    500.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Vainilla natural',
    'Extracto puro de vainilla',
    'Sabores Naturales',
    'Sabores Naturales',
    (SELECT id FROM categories WHERE name = 'Flavorings' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'ml'),
    100.0,
    2.50,
    500.0,
    100.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Chocolate oscuro 70%',
    'Chocolate de cobertura premium',
    'Chocolates Gourmet',
    'Chocolates Gourmet',
    (SELECT id FROM categories WHERE name = 'Chocolate & Cocoa' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'kg'),
    1.0,
    320.00,
    8.0,
    2.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Sal fina',
    'Sal de mesa refinada',
    'Salinera Nacional',
    'Salinera Nacional',
    (SELECT id FROM categories WHERE name = 'Other' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'kg'),
    1.0,
    12.00,
    5.0,
    1.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Crema para batir',
    'Crema para batir 35% grasa',
    'Lácteos Premium',
    'Lácteos Premium',
    (SELECT id FROM categories WHERE name = 'Dairy' LIMIT 1),
    u.id,
    (SELECT id FROM measurement_units WHERE code = 'L'),
    1.0,
    85.00,
    10.0,
    3.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

-- Associate allergens with materials
INSERT INTO raw_material_allergens (raw_material_id, allergen_id)
SELECT rm.id, a.id
FROM raw_materials rm, allergens a
WHERE rm.name = 'Harina de trigo todo uso' AND a.name = 'Gluten'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id)
UNION ALL
SELECT rm.id, a.id
FROM raw_materials rm, allergens a
WHERE rm.name = 'Mantequilla sin sal' AND a.name = 'Dairy'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id)
UNION ALL
SELECT rm.id, a.id
FROM raw_materials rm, allergens a
WHERE rm.name = 'Huevos frescos' AND a.name = 'Eggs'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id)
UNION ALL
SELECT rm.id, a.id
FROM raw_materials rm, allergens a
WHERE rm.name = 'Leche entera' AND a.name = 'Dairy'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id)
UNION ALL
SELECT rm.id, a.id
FROM raw_materials rm, allergens a
WHERE rm.name = 'Crema para batir' AND a.name = 'Dairy'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id);

-- ================================================
-- DEMO RECIPES
-- ================================================

-- Recipe 1: Pastel de Chocolate
INSERT INTO recipes (name, description, category_id, user_id, yield_quantity, yield_unit, preparation_time_minutes, baking_time_minutes, status, instructions, created_by, updated_by)
SELECT
    'Pastel de Chocolate Clásico',
    'Delicioso pastel de chocolate húmedo y esponjoso',
    (SELECT id FROM categories WHERE name = 'Chocolate & Cocoa' LIMIT 1),
    u.id,
    8.0,
    'porciones',
    30,
    45,
    'DRAFT',
    '1. Precalentar el horno a 180°C
2. Mezclar ingredientes secos
3. Agregar ingredientes húmedos
4. Hornear por 45 minutos
5. Enfriar y decorar',
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

-- Recipe ingredients for chocolate cake
INSERT INTO recipe_ingredients (recipe_id, raw_material_id, quantity, unit_id, display_order, created_by, updated_by)
SELECT
    r.id,
    rm.id,
    2.0,
    (SELECT id FROM measurement_units WHERE code = 'cup'),
    1,
    'SYSTEM',
    'SYSTEM'
FROM recipes r, raw_materials rm
WHERE r.name = 'Pastel de Chocolate Clásico'
AND rm.name = 'Harina de trigo todo uso'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id AND id = rm.user_id)
UNION ALL
SELECT
    r.id,
    rm.id,
    1.5,
    (SELECT id FROM measurement_units WHERE code = 'cup'),
    2,
    'SYSTEM',
    'SYSTEM'
FROM recipes r, raw_materials rm
WHERE r.name = 'Pastel de Chocolate Clásico'
AND rm.name = 'Azúcar refinada'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id AND id = rm.user_id)
UNION ALL
SELECT
    r.id,
    rm.id,
    3.0,
    (SELECT id FROM measurement_units WHERE code = 'unit'),
    3,
    'SYSTEM',
    'SYSTEM'
FROM recipes r, raw_materials rm
WHERE r.name = 'Pastel de Chocolate Clásico'
AND rm.name = 'Huevos frescos'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id AND id = rm.user_id)
UNION ALL
SELECT
    r.id,
    rm.id,
    0.5,
    (SELECT id FROM measurement_units WHERE code = 'cup'),
    4,
    'SYSTEM',
    'SYSTEM'
FROM recipes r, raw_materials rm
WHERE r.name = 'Pastel de Chocolate Clásico'
AND rm.name = 'Mantequilla sin sal'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id AND id = rm.user_id)
UNION ALL
SELECT
    r.id,
    rm.id,
    200.0,
    (SELECT id FROM measurement_units WHERE code = 'g'),
    5,
    'SYSTEM',
    'SYSTEM'
FROM recipes r, raw_materials rm
WHERE r.name = 'Pastel de Chocolate Clásico'
AND rm.name = 'Chocolate oscuro 70%'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id AND id = rm.user_id);

-- Fixed costs for chocolate cake
INSERT INTO recipe_fixed_costs (recipe_id, name, description, type, amount, calculation_method, created_by, updated_by)
SELECT
    r.id,
    'Costo eléctrico',
    'Costo de horno eléctrico',
    'ELECTRICITY',
    25.00,
    'FIXED_AMOUNT',
    'SYSTEM',
    'SYSTEM'
FROM recipes r
WHERE r.name = 'Pastel de Chocolate Clásico'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id)
UNION ALL
SELECT
    r.id,
    'Mano de obra',
    'Tiempo de preparación',
    'LABOR',
    50.00,
    'TIME_BASED',
    'SYSTEM',
    'SYSTEM'
FROM recipes r
WHERE r.name = 'Pastel de Chocolate Clásico'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = r.user_id);

-- Recipe 2: Galletas de Vainilla
INSERT INTO recipes (name, description, category_id, user_id, yield_quantity, yield_unit, preparation_time_minutes, baking_time_minutes, status, instructions, created_by, updated_by)
SELECT
    'Galletas de Vainilla',
    'Galletas crujientes con aroma a vainilla natural',
    (SELECT id FROM categories WHERE name = 'Other' LIMIT 1),
    u.id,
    24.0,
    'galletas',
    20,
    15,
    'DRAFT',
    '1. Mezclar mantequilla y azúcar
2. Agregar huevo y vainilla
3. Incorporar harina
4. Formar galletas
5. Hornear 12-15 minutos',
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

-- ================================================
-- DEMO PROFIT MARGINS
-- ================================================

INSERT INTO profit_margins (user_id, name, description, percentage, is_default, created_by, updated_by)
SELECT
    u.id,
    'Margen Estándar',
    'Margen de ganancia estándar para productos regulares',
    50.00,
    TRUE,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    u.id,
    'Margen Premium',
    'Margen para productos premium o eventos especiales',
    75.00,
    FALSE,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    u.id,
    'Margen Mayoreo',
    'Margen reducido para ventas al mayoreo',
    30.00,
    FALSE,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

-- ================================================
-- DEMO QUOTES
-- ================================================

INSERT INTO quotes (quote_number, user_id, client_name, client_email, client_phone, status, subtotal, tax_rate, tax_amount, total, currency, valid_until, created_by, updated_by)
SELECT
    'Q-2024-001',
    u.id,
    'Restaurant El Buen Sabor',
    'contacto@elbuensabor.com',
    '+52 555 123 4567',
    'SENT',
    1500.00,
    16.00,
    240.00,
    1740.00,
    'MXN',
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker'
UNION ALL
SELECT
    'Q-2024-002',
    u.id,
    'Café La Esquina',
    'pedidos@laesquina.com',
    '+52 555 987 6543',
    'ACCEPTED',
    800.00,
    16.00,
    128.00,
    928.00,
    'MXN',
    CURRENT_TIMESTAMP + INTERVAL '3 days',
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

-- Quote items
INSERT INTO quote_items (quote_id, recipe_id, quantity, unit_cost, profit_percentage, unit_price, subtotal, display_order, created_by, updated_by)
SELECT
    q.id,
    r.id,
    10.0,
    150.00,
    50.00,
    225.00,
    2250.00,
    1,
    'SYSTEM',
    'SYSTEM'
FROM quotes q, recipes r
WHERE q.quote_number = 'Q-2024-001'
AND r.name = 'Pastel de Chocolate Clásico'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = q.user_id AND id = r.user_id);

-- ================================================
-- DEMO PRICE HISTORY
-- ================================================

INSERT INTO material_price_history (raw_material_id, previous_cost, new_cost, change_percentage, reason, changed_at, changed_by)
SELECT
    rm.id,
    40.00,
    45.00,
    12.5,
    'Aumento del proveedor',
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    'SYSTEM'
FROM raw_materials rm
WHERE rm.name = 'Harina de trigo todo uso'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id)
UNION ALL
SELECT
    rm.id,
    160.00,
    180.00,
    12.5,
    'Incremento estacional',
    CURRENT_TIMESTAMP - INTERVAL '15 days',
    'SYSTEM'
FROM raw_materials rm
WHERE rm.name = 'Mantequilla sin sal'
AND EXISTS (SELECT 1 FROM users WHERE username = 'demo_baker' AND id = rm.user_id);

-- ================================================
-- DEMO NOTIFICATION PREFERENCES
-- ================================================

INSERT INTO notification_preferences (user_id, notify_price_changes, price_change_threshold_percent, notify_low_stock, low_stock_threshold_percent, created_by, updated_by)
SELECT
    u.id,
    TRUE,
    5.0,
    TRUE,
    20.0,
    'SYSTEM',
    'SYSTEM'
FROM users u WHERE u.username = 'demo_baker';

COMMIT;

-- ================================================
-- DEMO DATA SUMMARY
-- ================================================

-- This migration creates:
-- - 2 demo users (demo_baker, pastry_chef)
-- - 1 branding configuration
-- - 10 raw materials with allergen associations
-- - 2 recipes with ingredients and fixed costs
-- - 3 profit margin configurations
-- - 2 quotes (1 sent, 1 accepted) with quote items
-- - Price history for 2 materials
-- - Default notification preferences
--
-- Default credentials:
-- Username: demo_baker
-- Password: demo123
--
-- Username: pastry_chef
-- Password: demo123
