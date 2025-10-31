-- Script de inicialización para PostgreSQL
-- Este script se ejecuta automáticamente cuando el contenedor se inicia por primera vez

-- Crear el usuario para la aplicación Spring si no existe
DO
$$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'cronos_system_user_price') THEN
        CREATE USER cronos_system_user_price WITH PASSWORD 'cronos_system_254_8765_csmy-dd_0987';
    END IF;
END
$$;

-- Crear la base de datos si no existe
SELECT 'CREATE DATABASE cronos_system_price_data'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cronos_system_price_data')\gexec

-- Otorgar privilegios al usuario
GRANT ALL PRIVILEGES ON DATABASE cronos_system_price_data TO cronos_system_user_price;

-- Conectar a la nueva base de datos y otorgar permisos en el schema public
\c cronos_system_price_data

-- Otorgar todos los privilegios en el schema public
GRANT ALL ON SCHEMA public TO cronos_system_user_price;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cronos_system_user_price;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cronos_system_user_price;

-- Establecer permisos por defecto para objetos futuros
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO cronos_system_user_price;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO cronos_system_user_price;
