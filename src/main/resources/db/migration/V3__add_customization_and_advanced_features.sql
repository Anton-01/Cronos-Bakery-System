-- Customization and Advanced Features
-- Version 3.0 - Templates, Branding, Alerts, Tax, and Email Templates

-- ================================================
-- BRANDING AND CUSTOMIZATION
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
    updated_by VARCHAR(100)
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
    updated_by VARCHAR(100)
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
    updated_by VARCHAR(100)
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
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
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
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
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
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ================================================
-- STOCK ALERTS
-- ================================================

CREATE TABLE stock_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    alert_type VARCHAR(30) NOT NULL,
    current_quantity DECIMAL(10, 2) NOT NULL,
    threshold_quantity DECIMAL(10, 2) NOT NULL,
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ================================================
-- INDEXES
-- ================================================

-- Branding and customization
CREATE INDEX idx_branding_settings_user ON branding_settings(user_id);
CREATE INDEX idx_email_settings_user ON email_settings(user_id);
CREATE INDEX idx_notification_preferences_user ON notification_preferences(user_id);

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
-- DEFAULT EMAIL TEMPLATES (SPANISH)
-- ================================================

INSERT INTO email_templates (name, template_type, language, subject, html_body, text_body, is_system_template, is_default, variables_help) VALUES
(
    'Cotización Enviada - ES',
    'QUOTE_SENT',
    'ES',
    'Su cotización {{quoteNumber}} está lista',
    '<html><body><h1>¡Hola {{customerName}}!</h1><p>Su cotización <strong>{{quoteNumber}}</strong> está lista.</p><p>Puede verla en el siguiente enlace: <a href="{{quoteLink}}">Ver Cotización</a></p><p>Esta cotización es válida hasta: {{validUntil}}</p><p>Saludos,<br>{{businessName}}</p></body></html>',
    'Hola {{customerName}}! Su cotización {{quoteNumber}} está lista. Puede verla en: {{quoteLink}}. Válida hasta: {{validUntil}}',
    TRUE,
    TRUE,
    '{"customerName":"Nombre del cliente","quoteNumber":"Número de cotización","quoteLink":"Enlace a la cotización","validUntil":"Fecha de validez","businessName":"Nombre del negocio"}'
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
    '{"materialName":"Nombre del material","oldPrice":"Precio anterior","newPrice":"Precio nuevo","changePercent":"Porcentaje de cambio","affectedRecipes":"Lista de recetas afectadas"}'
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
    '{"materialName":"Nombre del material","currentQuantity":"Cantidad actual","minQuantity":"Cantidad mínima","unit":"Unidad de medida","supplier":"Proveedor"}'
);

-- ================================================
-- DEFAULT EMAIL TEMPLATES (ENGLISH)
-- ================================================

INSERT INTO email_templates (name, template_type, language, subject, html_body, text_body, is_system_template, is_default, variables_help) VALUES
(
    'Quote Sent - EN',
    'QUOTE_SENT',
    'EN',
    'Your quote {{quoteNumber}} is ready',
    '<html><body><h1>Hello {{customerName}}!</h1><p>Your quote <strong>{{quoteNumber}}</strong> is ready.</p><p>You can view it at: <a href="{{quoteLink}}">View Quote</a></p><p>This quote is valid until: {{validUntil}}</p><p>Best regards,<br>{{businessName}}</p></body></html>',
    'Hello {{customerName}}! Your quote {{quoteNumber}} is ready. View at: {{quoteLink}}. Valid until: {{validUntil}}',
    TRUE,
    TRUE,
    '{"customerName":"Customer name","quoteNumber":"Quote number","quoteLink":"Quote link","validUntil":"Valid until date","businessName":"Business name"}'
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
    '{"materialName":"Material name","oldPrice":"Old price","newPrice":"New price","changePercent":"Change percentage","affectedRecipes":"Affected recipes list"}'
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
    '{"materialName":"Material name","currentQuantity":"Current quantity","minQuantity":"Minimum quantity","unit":"Unit of measure","supplier":"Supplier"}'
);

-- ================================================
-- DEFAULT TAX RATES
-- ================================================

-- Mexico - IVA
INSERT INTO tax_rates (country_code, region_code, region_name, tax_name, tax_rate_percent, description, is_default, applies_to_food) VALUES
('MX', NULL, 'México', 'IVA', 16.00, 'Impuesto al Valor Agregado general', TRUE, TRUE),
('MX', 'BCN', 'Baja California Norte', 'IVA Frontera', 8.00, 'IVA reducido zona fronteriza', FALSE, TRUE);

-- USA - Sales Tax (examples)
INSERT INTO tax_rates (country_code, region_code, region_name, tax_name, tax_rate_percent, description, is_default, applies_to_food) VALUES
('US', 'CA', 'California', 'Sales Tax', 7.25, 'California state sales tax', FALSE, FALSE),
('US', 'TX', 'Texas', 'Sales Tax', 6.25, 'Texas state sales tax', FALSE, FALSE),
('US', 'NY', 'New York', 'Sales Tax', 4.00, 'New York state sales tax', FALSE, FALSE);

COMMIT;
