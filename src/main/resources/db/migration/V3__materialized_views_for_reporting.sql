-- Materialized Views for High-Performance Reporting
-- Version 4.0 - Analytics and Reporting Optimization

-- ================================================
-- RECIPE COST ANALYSIS VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_recipe_cost_analysis AS
SELECT
    r.id AS recipe_id,
    r.name AS recipe_name,
    r.user_id,
    u.username,
    c.name AS category_name,
    COUNT(DISTINCT ri.id) AS ingredient_count,
    COUNT(DISTINCT rsr.id) AS sub_recipe_count,
    SUM(rm.unit_cost * ri.quantity * COALESCE(cf.factor, 1.0)) AS total_ingredient_cost,
    SUM(rfc.fixed_amount) AS total_fixed_cost,
    SUM(rm.unit_cost * ri.quantity * COALESCE(cf.factor, 1.0)) + COALESCE(SUM(rfc.fixed_amount), 0) AS total_recipe_cost,
    r.serving_size,
    r.serving_unit,
    (SUM(rm.unit_cost * ri.quantity * COALESCE(cf.factor, 1.0)) + COALESCE(SUM(rfc.fixed_amount), 0)) / NULLIF(r.serving_size, 0) AS cost_per_serving,
    r.created_at,
    r.updated_at,
    r.is_active
FROM recipes r
JOIN users u ON r.user_id = u.id
LEFT JOIN categories c ON r.category_id = c.id
LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id
LEFT JOIN raw_materials rm ON ri.raw_material_id = rm.id
LEFT JOIN conversion_factors cf ON (ri.unit_id = cf.from_unit_id AND rm.unit_id = cf.to_unit_id)
LEFT JOIN recipe_sub_recipes rsr ON r.id = rsr.parent_recipe_id
LEFT JOIN recipe_fixed_costs rfc ON r.id = rfc.recipe_id AND rfc.is_active = TRUE
WHERE r.is_active = TRUE
GROUP BY r.id, r.name, r.user_id, u.username, c.name, r.serving_size, r.serving_unit, r.created_at, r.updated_at, r.is_active;

CREATE UNIQUE INDEX idx_mv_recipe_cost_analysis_id ON mv_recipe_cost_analysis(recipe_id);
CREATE INDEX idx_mv_recipe_cost_analysis_user ON mv_recipe_cost_analysis(user_id);
CREATE INDEX idx_mv_recipe_cost_analysis_cost ON mv_recipe_cost_analysis(total_recipe_cost);

-- ================================================
-- INVENTORY SUMMARY VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_inventory_summary AS
SELECT
    rm.id AS material_id,
    rm.name AS material_name,
    rm.user_id,
    u.username,
    c.name AS category_name,
    rm.unit_cost,
    rm.current_quantity,
    rm.min_quantity,
    rm.max_quantity,
    mu.abbreviation AS unit,
    (rm.current_quantity * rm.unit_cost) AS inventory_value,
    CASE
        WHEN rm.current_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN rm.current_quantity < rm.min_quantity THEN 'LOW_STOCK'
        WHEN rm.current_quantity > rm.max_quantity THEN 'OVERSTOCK'
        ELSE 'NORMAL'
    END AS stock_status,
    CASE
        WHEN rm.min_quantity > 0 THEN (rm.current_quantity / rm.min_quantity) * 100
        ELSE 100
    END AS stock_percent,
    rm.supplier,
    rm.is_active,
    rm.updated_at AS last_updated,
    (SELECT COUNT(*) FROM recipe_ingredients ri WHERE ri.raw_material_id = rm.id) AS used_in_recipes_count,
    (SELECT MAX(changed_at) FROM material_price_history mph WHERE mph.raw_material_id = rm.id) AS last_price_change,
    (SELECT new_price FROM material_price_history mph WHERE mph.raw_material_id = rm.id ORDER BY changed_at DESC LIMIT 1) AS last_known_price
FROM raw_materials rm
JOIN users u ON rm.user_id = u.id
JOIN measurement_units mu ON rm.unit_id = mu.id
LEFT JOIN categories c ON rm.category_id = c.id
WHERE rm.is_active = TRUE;

CREATE UNIQUE INDEX idx_mv_inventory_summary_id ON mv_inventory_summary(material_id);
CREATE INDEX idx_mv_inventory_summary_user ON mv_inventory_summary(user_id);
CREATE INDEX idx_mv_inventory_summary_status ON mv_inventory_summary(stock_status);
CREATE INDEX idx_mv_inventory_summary_value ON mv_inventory_summary(inventory_value);

-- ================================================
-- PRICE HISTORY TRENDS VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_price_trends AS
SELECT
    rm.id AS material_id,
    rm.name AS material_name,
    rm.user_id,
    COUNT(mph.id) AS total_price_changes,
    MIN(mph.new_price) AS min_price_ever,
    MAX(mph.new_price) AS max_price_ever,
    AVG(mph.new_price) AS avg_price,
    rm.unit_cost AS current_price,
    (rm.unit_cost - AVG(mph.new_price)) AS price_variance_from_avg,
    ((rm.unit_cost - AVG(mph.new_price)) / NULLIF(AVG(mph.new_price), 0)) * 100 AS price_variance_percent,
    MAX(mph.changed_at) AS last_change_date,
    (SELECT mph2.new_price FROM material_price_history mph2
     WHERE mph2.raw_material_id = rm.id
     AND mph2.changed_at >= CURRENT_DATE - INTERVAL '30 days'
     ORDER BY mph2.changed_at DESC LIMIT 1) AS price_30_days_ago,
    (SELECT mph2.new_price FROM material_price_history mph2
     WHERE mph2.raw_material_id = rm.id
     AND mph2.changed_at >= CURRENT_DATE - INTERVAL '90 days'
     ORDER BY mph2.changed_at DESC LIMIT 1) AS price_90_days_ago,
    rm.currency
FROM raw_materials rm
LEFT JOIN material_price_history mph ON rm.id = mph.raw_material_id
WHERE rm.is_active = TRUE
GROUP BY rm.id, rm.name, rm.user_id, rm.unit_cost, rm.currency;

CREATE UNIQUE INDEX idx_mv_price_trends_id ON mv_price_trends(material_id);
CREATE INDEX idx_mv_price_trends_user ON mv_price_trends(user_id);
CREATE INDEX idx_mv_price_trends_variance ON mv_price_trends(price_variance_percent);

-- ================================================
-- QUOTE PERFORMANCE VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_quote_performance AS
SELECT
    q.id AS quote_id,
    q.quote_number,
    q.user_id,
    u.username,
    q.customer_name,
    q.status,
    q.currency,
    q.subtotal,
    q.tax_amount,
    q.total_amount,
    q.created_at,
    q.valid_until,
    CASE
        WHEN q.valid_until < CURRENT_TIMESTAMP THEN TRUE
        ELSE FALSE
    END AS is_expired,
    COUNT(DISTINCT qi.id) AS item_count,
    SUM(qi.quantity) AS total_items_quantity,
    AVG(qi.margin_percent) AS avg_margin_percent,
    q.is_shared,
    (SELECT COUNT(*) FROM quote_access_logs qal WHERE qal.quote_id = q.id) AS view_count,
    (SELECT MAX(accessed_at) FROM quote_access_logs qal WHERE qal.quote_id = q.id) AS last_viewed_at,
    CASE
        WHEN q.status = 'ACCEPTED' THEN q.total_amount
        ELSE 0
    END AS revenue,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - q.created_at)) / 3600 AS age_hours,
    CASE
        WHEN q.status = 'DRAFT' AND CURRENT_TIMESTAMP - q.created_at > INTERVAL '7 days' THEN 'STALE_DRAFT'
        WHEN q.status = 'SENT' AND q.valid_until < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN q.status = 'SENT' AND q.valid_until - CURRENT_TIMESTAMP < INTERVAL '24 hours' THEN 'EXPIRING_SOON'
        ELSE 'ACTIVE'
    END AS quote_health
FROM quotes q
JOIN users u ON q.user_id = u.id
LEFT JOIN quote_items qi ON q.id = qi.quote_id
GROUP BY q.id, q.quote_number, q.user_id, u.username, q.customer_name, q.status,
         q.currency, q.subtotal, q.tax_amount, q.total_amount, q.created_at,
         q.valid_until, q.is_shared;

CREATE UNIQUE INDEX idx_mv_quote_performance_id ON mv_quote_performance(quote_id);
CREATE INDEX idx_mv_quote_performance_user ON mv_quote_performance(user_id);
CREATE INDEX idx_mv_quote_performance_status ON mv_quote_performance(status);
CREATE INDEX idx_mv_quote_performance_health ON mv_quote_performance(quote_health);
CREATE INDEX idx_mv_quote_performance_revenue ON mv_quote_performance(revenue);

-- ================================================
-- PROFITABILITY ANALYSIS VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_profitability_analysis AS
SELECT
    r.id AS recipe_id,
    r.name AS recipe_name,
    r.user_id,
    pm.id AS margin_id,
    pm.name AS margin_name,
    pm.margin_percent,
    rca.total_recipe_cost AS base_cost,
    rca.total_recipe_cost * (1 + (pm.margin_percent / 100)) AS selling_price,
    rca.total_recipe_cost * (pm.margin_percent / 100) AS profit_amount,
    pm.margin_percent AS profit_margin_percent,
    rca.cost_per_serving,
    rca.cost_per_serving * (1 + (pm.margin_percent / 100)) AS selling_price_per_serving,
    -- Break-even analysis
    CASE
        WHEN rca.total_fixed_cost > 0 AND (rca.total_recipe_cost * (pm.margin_percent / 100)) > 0
        THEN CEIL(rca.total_fixed_cost / (rca.total_recipe_cost * (pm.margin_percent / 100)))
        ELSE 0
    END AS breakeven_units,
    CURRENT_TIMESTAMP AS calculated_at
FROM mv_recipe_cost_analysis rca
JOIN recipes r ON rca.recipe_id = r.id
CROSS JOIN profit_margins pm
WHERE pm.is_default = TRUE OR pm.user_id = r.user_id;

CREATE INDEX idx_mv_profitability_recipe ON mv_profitability_analysis(recipe_id);
CREATE INDEX idx_mv_profitability_user ON mv_profitability_analysis(user_id);
CREATE INDEX idx_mv_profitability_margin ON mv_profitability_analysis(profit_margin_percent);

-- ================================================
-- USER DASHBOARD SUMMARY VIEW
-- ================================================

CREATE MATERIALIZED VIEW mv_user_dashboard AS
SELECT
    u.id AS user_id,
    u.username,
    -- Inventory metrics
    (SELECT COUNT(*) FROM raw_materials WHERE user_id = u.id AND is_active = TRUE) AS total_materials,
    (SELECT COUNT(*) FROM raw_materials WHERE user_id = u.id AND current_quantity < min_quantity AND is_active = TRUE) AS low_stock_materials,
    (SELECT SUM(current_quantity * unit_cost) FROM raw_materials WHERE user_id = u.id AND is_active = TRUE) AS total_inventory_value,
    -- Recipe metrics
    (SELECT COUNT(*) FROM recipes WHERE user_id = u.id AND is_active = TRUE) AS total_recipes,
    (SELECT AVG(total_recipe_cost) FROM mv_recipe_cost_analysis WHERE user_id = u.id) AS avg_recipe_cost,
    -- Quote metrics
    (SELECT COUNT(*) FROM quotes WHERE user_id = u.id) AS total_quotes,
    (SELECT COUNT(*) FROM quotes WHERE user_id = u.id AND status = 'ACCEPTED') AS accepted_quotes,
    (SELECT SUM(total_amount) FROM quotes WHERE user_id = u.id AND status = 'ACCEPTED') AS total_revenue,
    (SELECT COUNT(*) FROM quotes WHERE user_id = u.id AND status = 'SENT' AND valid_until > CURRENT_TIMESTAMP) AS active_quotes,
    -- Alert metrics
    (SELECT COUNT(*) FROM stock_alerts WHERE user_id = u.id AND status = 'ACTIVE') AS active_alerts,
    -- Recent activity
    (SELECT MAX(updated_at) FROM raw_materials WHERE user_id = u.id) AS last_material_update,
    (SELECT MAX(updated_at) FROM recipes WHERE user_id = u.id) AS last_recipe_update,
    (SELECT MAX(created_at) FROM quotes WHERE user_id = u.id) AS last_quote_created,
    CURRENT_TIMESTAMP AS snapshot_time
FROM users u
WHERE u.is_enabled = TRUE;

CREATE UNIQUE INDEX idx_mv_user_dashboard_user ON mv_user_dashboard(user_id);

-- ================================================
-- REFRESH FUNCTIONS
-- ================================================

-- Function to refresh all materialized views
CREATE OR REPLACE FUNCTION refresh_all_reporting_views()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_recipe_cost_analysis;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_inventory_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_price_trends;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_quote_performance;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_profitability_analysis;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_dashboard;
END;
$$ LANGUAGE plpgsql;

-- Function to refresh views for a specific user
CREATE OR REPLACE FUNCTION refresh_user_reporting_views(p_user_id BIGINT)
RETURNS void AS $$
BEGIN
    -- Note: PostgreSQL doesn't support partial refresh of materialized views
    -- This function is a placeholder for future optimization
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_recipe_cost_analysis;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_inventory_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_price_trends;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_quote_performance;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_profitability_analysis;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_dashboard;
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- SCHEDULED REFRESH (Commented - requires pg_cron extension)
-- ================================================

-- Uncomment these lines if you have pg_cron extension installed:
--
-- SELECT cron.schedule('refresh-reporting-views', '*/30 * * * *', 'SELECT refresh_all_reporting_views()');
--
-- This would refresh all views every 30 minutes

COMMIT;
