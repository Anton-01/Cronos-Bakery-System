# Database Migration Consolidation

## Summary

Las migraciones de Flyway han sido consolidadas de 7 archivos a 3 archivos más organizados:

### Archivos Anteriores (ELIMINADOS)
- `V1__initial_schema.sql` - Schema inicial
- `V2__add_user_profile_and_session_management.sql` - Perfiles y sesiones
- `V3__add_customization_and_advanced_features.sql` - Customización
- `V4__add_materialized_views_for_reporting.sql` - Vistas materializadas
- `V5__seed_demo_data.sql` - Datos demo
- `V6__add_missing_audit_columns.sql` - Columnas de auditoría
- `V7__sync_all_entity_fields.sql` - Sincronización de campos

### Nuevos Archivos (CONSOLIDADOS)
- `V1__consolidated_schema.sql` - **Schema completo** con todas las tablas y datos de sistema
- `V2__seed_demo_data.sql` - Datos de demostración
- `V3__materialized_views_for_reporting.sql` - Vistas materializadas para reportes

## Cambios Importantes

### 1. Schema Consolidado (V1)
El archivo V1 ahora incluye:
- ✅ Todas las tablas con **todos** sus campos correctamente sincronizados con las entidades JPA
- ✅ Todos los índices necesarios
- ✅ Datos iniciales del sistema (roles, unidades, categorías, alérgenos, etc.)
- ✅ Plantillas de email por defecto
- ✅ Tasas de impuesto por defecto

### 2. Sincronización Completa
Se corrigieron las siguientes discrepancias entre entidades y schema:

#### Renombramientos de columnas:
- `users.is_enabled` → `users.enabled`
- `users.is_account_non_locked` → `users.account_non_locked`
- `users.is_account_non_expired` → `users.account_non_expired`
- `users.is_credentials_non_expired` → `users.credentials_non_expired`
- `users.lockout_time` → `users.locked_until`
- `users.last_login` → `users.last_login_at`
- `quotes.customer_name` → `quotes.client_name`
- `quotes.customer_email` → `quotes.client_email`
- `quotes.customer_phone` → `quotes.client_phone`
- `quotes.is_shared` → `quotes.is_shareable`
- `quotes.total_amount` → `quotes.total`
- `categories.is_system_category` → `categories.is_system_default`
- `allergens.is_system_allergen` → `allergens.is_system_default`
- `conversion_factors.is_system_conversion` → `conversion_factors.is_system_default`
- `material_price_history.old_price` → `material_price_history.previous_cost`
- `material_price_history.new_price` → `material_price_history.new_cost`
- `material_price_history.change_percent` → `material_price_history.change_percentage`
- `material_price_history.change_reason` → `material_price_history.reason`
- `raw_materials.current_quantity` → `raw_materials.current_stock`
- `login_history.login_time` → `login_history.login_at`
- `login_history.success` → `login_history.successful`
- `refresh_tokens.expiry_date` → `refresh_tokens.expires_at`
- `recipe_versions.change_notes` → `recipe_versions.changes`
- `measurement_units.abbreviation` → `measurement_units.code`

#### Nuevos campos agregados:
- `users`: phone_number, business_name, default_currency, default_language, default_tax_rate, last_failed_login
- `allergens`: name_en, name_es
- `measurement_units`: name_plural, type
- `raw_materials`: brand, purchase_unit_id, purchase_quantity, last_purchase_date, last_price_update, needs_recalculation
- `recipes`: yield_quantity, yield_unit, baking_time_minutes, cooling_time_minutes, status, needs_recalculation, current_version, storage_instructions, shelf_life_days
- `recipe_ingredients`: cost_per_unit, total_cost, notes
- `recipe_sub_recipes`: total_cost
- `recipe_fixed_costs`: name, type, amount, calculation_method, time_in_minutes, percentage
- `recipe_files`: original_file_name, thumbnail_path
- `recipe_cost_history`: recipe_version, materials_cost, fixed_costs, sub_recipes_cost, cost_per_unit, calculated_by, calculation_notes
- `recipe_versions`: version_name, is_current
- `quote_items`: scale_factor, profit_margin_id, profit_percentage, notes
- `quotes`: client_address, tax_rate
- `profit_margins`: is_active, percentage
- `conversion_factors`: notes
- `login_history`: browser, operating_system, device, two_factor_used
- `refresh_tokens`: revoked, revoked_at, ip_address, user_agent
- Y muchos más...

#### Campos de auditoría agregados:
Todos las entidades que extienden `AuditableEntity` ahora tienen:
- `created_at` TIMESTAMP NOT NULL
- `updated_at` TIMESTAMP NOT NULL
- `created_by` VARCHAR(100)
- `updated_by` VARCHAR(100)
- `version` BIGINT (para optimistic locking)

### 3. Datos Demo Mejorados (V2)
- Usuarios de demostración con passwords hasheados correctamente
- Materias primas con alérgenos asociados
- Recetas con ingredientes y costos fijos
- Cotizaciones con items
- Historial de precios
- Preferencias de notificación

## ⚠️ Instrucciones de Migración

Si ya tienes una base de datos existente con las migraciones antiguas (V1-V7), necesitas hacer lo siguiente:

### Opción 1: Base de Datos Nueva (Recomendado para desarrollo)
```sql
-- Eliminar la base de datos actual y crear una nueva
DROP DATABASE IF EXISTS cronos_bakery;
CREATE DATABASE cronos_bakery;
```

### Opción 2: Limpiar Historial de Flyway (Para bases de datos con datos importantes)
```sql
-- CUIDADO: Esto eliminará el historial de migraciones de Flyway
-- Solo usa esto si entiendes las implicaciones
DELETE FROM flyway_schema_history;
```

### Opción 3: Baseline Manual (Más seguro)
```sql
-- Si ya tienes las migraciones V1-V7 aplicadas, haz baseline en V3
-- Esto le dice a Flyway que ignore V1, V2, V3 y solo aplique nuevas migraciones
DELETE FROM flyway_schema_history WHERE version IN ('1', '2', '3', '4', '5', '6', '7');

-- Luego ejecuta la aplicación con spring.flyway.baseline-version=0
-- o configura baseline-on-migrate=true en application.yml
```

## Verificación

Después de aplicar las migraciones, verifica que:

1. Todas las tablas existen:
```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
```

2. Todas las columnas de auditoría existen:
```sql
SELECT table_name, column_name
FROM information_schema.columns
WHERE table_schema = 'public'
AND column_name IN ('created_at', 'updated_at', 'created_by', 'updated_by', 'version')
ORDER BY table_name, column_name;
```

3. Los datos de sistema se cargaron correctamente:
```sql
SELECT COUNT(*) FROM roles;              -- Debe ser 3
SELECT COUNT(*) FROM measurement_units;  -- Debe ser 15
SELECT COUNT(*) FROM allergens;          -- Debe ser 10
SELECT COUNT(*) FROM categories;         -- Debe ser 12
SELECT COUNT(*) FROM email_templates;    -- Debe ser 6
SELECT COUNT(*) FROM tax_rates;          -- Debe ser 5
```

## Sincronización con Entidades

El schema ahora está 100% sincronizado con las entidades JPA. No deberías ver errores de Hibernate al iniciar la aplicación con `ddl-auto: validate`.

## Notas Adicionales

- Los archivos antiguos fueron respaldados en `backup/`
- El archivo V3 (vistas materializadas) no cambió, solo se renumeró
- Todos los campos ahora usan convenciones de nomenclatura consistentes
- Se agregaron índices para mejorar el rendimiento
- Los datos demo incluyen usuarios con password `demo123` (hasheado)

## Soporte

Si encuentras algún problema con la migración, verifica:
1. Que PostgreSQL esté corriendo
2. Que las credenciales de base de datos sean correctas
3. Que la base de datos tenga permisos suficientes
4. Que no haya conflictos con el historial de Flyway

Para más información, consulta la documentación de Flyway: https://flywaydb.org/documentation/
