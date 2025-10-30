# Cronos Bakery System

## Sistema Integral de Gestión de Costos para Repostería

Sistema empresarial completo para gestión de costos, inventario, recetas y cotizaciones de negocios de repostería. Desarrollado con Spring Boot 3.5.7 y Java 21.

---

## Características Principales

### 1. Gestión de Materia Prima
- ✅ **Control de Inventario** con alertas automáticas de stock bajo
- ✅ **Historial de Precios** con notificaciones de cambios
- ✅ **Categorías** del sistema + personalizadas por usuario
- ✅ **Gestión de Alérgenos** completa
- ✅ **Multi-proveedor** con seguimiento de SKU y códigos de barras
- ✅ **Tracking de lotes** con fechas de vencimiento (FIFO/LIFO)

### 2. Sistema de Conversiones
- ✅ **Catálogo Predefinido** extenso (kg↔g, l↔ml, tazas, cucharadas, etc.)
- ✅ **Conversiones Personalizadas** por usuario
- ✅ **Conversiones Indirectas** automáticas
- ✅ **Conversiones Específicas** por ingrediente
- ✅ **15+ unidades** de medida predefinidas

### 3. Recetas Dinámicas
- ✅ **Sub-recetas Ilimitadas** con anidación
- ✅ **Sustitutos de Ingredientes** (sin azúcar, gluten-free, vegano, etc.)
- ✅ **Escalado Dinámico** (duplicar, triplicar, fraccionar)
- ✅ **Control de Versiones** completo con snapshots JSON
- ✅ **Gestión Automática de Alérgenos**
- ✅ **Carga de Imágenes y PDFs**
- ✅ **Instrucciones paso a paso**

### 4. Costos Inteligentes
- ✅ **Cálculo Automático** con actualización en tiempo real
- ✅ **Costos Fijos Configurables** (gas, luz, tiempo, porcentajes)
- ✅ **Histórico de Costos** por versión
- ✅ **Notificaciones** al cambiar precios
- ✅ **Recálculo Automático** de recetas afectadas
- ✅ **Vistas Materializadas** para reportes de alto rendimiento

### 5. Cotizaciones Profesionales
- ✅ **Generación de PDFs** con branding personalizable
- ✅ **Enlaces Compartibles** con expiración de 72 horas
- ✅ **Tracking Completo** de accesos (IP, navegador, fecha)
- ✅ **Envío por Email** automático
- ✅ **Múltiples Márgenes** de ganancia por cliente
- ✅ **Estados**: Draft, Sent, Accepted, Rejected, Expired

### 6. Análisis Financiero
- ✅ **Punto de Equilibrio** automático
- ✅ **Simulación** con diferentes márgenes
- ✅ **Análisis de Rentabilidad** por producto
- ✅ **Gestión de IVA** por región (México, USA)
- ✅ **Reportes Exportables** (PDF, Excel)

### 7. Multi-idioma y Multi-moneda
- ✅ **Español e Inglés** completamente soportados
- ✅ **USD y MXN** con conversión en tiempo real
- ✅ **Plantillas de Email Bilingües**
- ✅ **Localización** de interfaces

### 8. Notificaciones
- ✅ **Email** al cambiar precios
- ✅ **WebSocket** para notificaciones en tiempo real
- ✅ **Alertas de Stock Bajo** programables
- ✅ **Notificaciones de Cotizaciones** vistas
- ✅ **Resúmenes** diarios, semanales y mensuales

### 9. Personalización Avanzada
- ✅ **ReportTemplate** - Plantillas de reportes personalizables
- ✅ **QuoteTemplate** - Plantillas de cotizaciones personalizables
- ✅ **BrandingSettings** - Logos, colores, fuentes
- ✅ **EmailSettings** - Configuración SMTP personalizada
- ✅ **NotificationPreferences** - Preferencias de notificaciones

### 10. Seguridad Empresarial
- ✅ **Autenticación JWT** (Access + Refresh tokens)
- ✅ **2FA** con Google Authenticator
- ✅ **Control de Sesiones** (máx 5 concurrentes)
- ✅ **Fingerprinting de Dispositivos**
- ✅ **Bloqueo de Cuenta** exponencial
- ✅ **Histórico de Contraseñas** (últimas 5)
- ✅ **Auditoría Completa** (createdAt, updatedAt, createdBy)
- ✅ **Rate Limiting** en endpoints críticos

---

## Stack Tecnológico

### Backend
- **Spring Boot** 3.5.7
- **Java** 21
- **PostgreSQL** 14+ (con índices optimizados)
- **Flyway** - Migraciones versionadas
- **Spring Security** + OAuth2
- **JWT** (jjwt 0.12.3)

### Librerías Principales
- **Caffeine** 3.1.8 - Cache multinivel
- **Bucket4j** 8.7.0 - Rate limiting
- **MapStruct** 1.5.5 - Mapeo de objetos
- **iTextPDF** 5.5.13.3 - Generación de PDFs
- **Thumbnailator** 0.4.20 - Procesamiento de imágenes
- **SpringDoc OpenAPI** 2.3.0 - Documentación Swagger
- **Spring Boot Actuator** - Health checks y métricas

### Base de Datos
- **PostgreSQL** 14+
- **Vistas Materializadas** para reportes
- **Índices Compuestos** optimizados
- **JSONB** para datos flexibles
- **Funciones PL/pgSQL** para cálculos complejos

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│         PRESENTATION (presentation/controller)          │
│  REST API endpoints con validación y seguridad JWT      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│      APPLICATION (application/service)                   │
│  Lógica de negocio, DTOs, mapeos, coordinación          │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         DOMAIN (domain/service + entity)                 │
│  Servicios de dominio, entidades, reglas de negocio     │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│   INFRASTRUCTURE (infrastructure/persistence/config)     │
│  Repositorios JPA, BD, configuraciones, excepciones     │
└─────────────────────────────────────────────────────────┘
```

---

## Instalación y Configuración

### Requisitos Previos
- Java 21+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clonar el repositorio
```bash
git clone https://github.com/Anton-01/Cronos-Bakery-System.git
cd Cronos-Bakery-System
```

### 2. Configurar Base de Datos
```bash
# Crear base de datos
createdb auth_db

# Actualizar credenciales en src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: tu_usuario
    password: tu_contraseña
```

### 3. Compilar y Ejecutar
```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### 4. Acceder a la Aplicación
- **API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator/health

---

## Configuración

### application.yml Principal

```yaml
spring:
  application:
    name: Cronos Bakery System

  # Base de datos
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  # JPA
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 20

  # Flyway
  flyway:
    enabled: true
    baseline-on-migrate: true

  # Cache
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=3600s

  # Mail
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# Security
security:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-here}
    expiration: 900000  # 15 minutes
    refresh-expiration: 604800000  # 7 days

  password-policy:
    min-length: 8
    require-uppercase: true
    require-lowercase: true
    require-digit: true
    require-special-char: true

  lockout:
    max-attempts: 5
    duration-minutes: 60
    increment-factor: 2
    max-lockout-hours: 24

# Rate Limiting
rate-limit:
  login:
    capacity: 10
    tokens: 10
    refill-duration: 60
  register:
    capacity: 5
    tokens: 5
    refill-duration: 300
```

### Variables de Entorno

```bash
# JWT
export JWT_SECRET="your-super-secret-256-bit-key-here"

# Base de Datos
export DB_URL="jdbc:postgresql://localhost:5432/auth_db"
export DB_USERNAME="postgres"
export DB_PASSWORD="postgres"

# Email
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"

# API Externa (Tasas de Cambio)
export EXCHANGE_RATE_API_KEY="your-api-key"
```

---

## Endpoints Principales

### Autenticación
```
POST   /api/v1/auth/register         - Registrar nuevo usuario
POST   /api/v1/auth/login            - Iniciar sesión
POST   /api/v1/auth/refresh          - Refrescar token
POST   /api/v1/auth/logout           - Cerrar sesión
POST   /api/v1/auth/enable-2fa       - Habilitar 2FA
POST   /api/v1/auth/verify-2fa       - Verificar código 2FA
```

### Materias Primas
```
GET    /api/v1/raw-materials         - Listar materiales
POST   /api/v1/raw-materials         - Crear material
GET    /api/v1/raw-materials/{id}    - Obtener material
PUT    /api/v1/raw-materials/{id}    - Actualizar material
DELETE /api/v1/raw-materials/{id}    - Eliminar material
GET    /api/v1/raw-materials/low-stock - Materiales con stock bajo
```

### Recetas
```
GET    /api/v1/recipes               - Listar recetas
POST   /api/v1/recipes               - Crear receta
GET    /api/v1/recipes/{id}          - Obtener receta
PUT    /api/v1/recipes/{id}          - Actualizar receta
DELETE /api/v1/recipes/{id}          - Eliminar receta
POST   /api/v1/recipes/{id}/scale    - Escalar receta
GET    /api/v1/recipes/{id}/cost     - Calcular costo
GET    /api/v1/recipes/{id}/breakeven - Análisis punto de equilibrio
GET    /api/v1/recipes/search        - Buscar recetas
```

### Cotizaciones
```
GET    /api/v1/quotes                - Listar cotizaciones
POST   /api/v1/quotes                - Crear cotización
GET    /api/v1/quotes/{id}           - Obtener cotización
PUT    /api/v1/quotes/{id}           - Actualizar cotización
DELETE /api/v1/quotes/{id}           - Eliminar cotización
POST   /api/v1/quotes/{id}/share     - Compartir cotización
GET    /api/v1/quotes/{id}/access-log - Ver accesos
POST   /api/v1/quotes/{id}/send-email - Enviar por email
GET    /api/v1/quotes/share/{token}  - Ver cotización compartida
```

### Dashboard
```
GET    /api/v1/dashboard             - Resumen general
GET    /api/v1/dashboard/inventory   - Resumen de inventario
GET    /api/v1/dashboard/quotes      - Resumen de cotizaciones
GET    /api/v1/dashboard/recipes     - Resumen de recetas
GET    /api/v1/dashboard/alerts      - Alertas activas
```

---

## Migraciones de Base de Datos

El sistema incluye 5 migraciones de Flyway:

### V1__initial_schema.sql
- Tablas de usuarios y autenticación
- Categorías y alérgenos
- Materias primas
- Recetas y sub-recetas
- Cotizaciones
- Unidades y conversiones
- Datos iniciales del sistema

### V2__add_user_profile_and_session_management.sql
- Perfiles de usuario extendidos
- Gestión de sesiones
- Device fingerprinting

### V3__add_customization_and_advanced_features.sql
- Branding settings
- Email settings
- Notification preferences
- Quote templates
- Report templates
- Email templates
- Stock alerts
- Tax rates
- Plantillas de email bilingües

### V4__add_materialized_views_for_reporting.sql
- Vista: `mv_recipe_cost_analysis`
- Vista: `mv_inventory_summary`
- Vista: `mv_price_trends`
- Vista: `mv_quote_performance`
- Vista: `mv_profitability_analysis`
- Vista: `mv_user_dashboard`
- Funciones de refresco

### V5__seed_demo_data.sql
- Usuarios demo (demo_baker, pastry_chef)
- 10 materiales con alérgenos
- 2 recetas completas
- 3 márgenes de ganancia
- 2 cotizaciones de ejemplo
- Historial de precios
- Datos de prueba completos

---

## Datos Demo

### Usuarios Predefinidos

| Usuario | Email | Contraseña | Descripción |
|---------|-------|------------|-------------|
| demo_baker | demo@cronosbakery.com | demo123 | Usuario demo principal |
| pastry_chef | chef@cronosbakery.com | demo123 | Chef de repostería |

### Recetas Demo
- **Pastel de Chocolate Clásico** - 8 porciones, 45 min cocción
- **Galletas de Vainilla** - 24 galletas, 15 min cocción

### Materiales Demo
- Harina de trigo todo uso
- Azúcar refinada
- Mantequilla sin sal
- Huevos frescos
- Leche entera
- Polvo para hornear
- Vainilla natural
- Chocolate oscuro 70%
- Sal fina
- Crema para batir

---

## Vistas Materializadas

### mv_recipe_cost_analysis
Análisis de costos de recetas con desglose completo:
- Costo total de ingredientes
- Costos fijos
- Costo por porción
- Número de ingredientes y sub-recetas

### mv_inventory_summary
Resumen de inventario en tiempo real:
- Valor del inventario
- Estado de stock (Normal, Low, Out, Overstock)
- Porcentaje de stock restante
- Última actualización de precio
- Uso en recetas

### mv_price_trends
Tendencias de precios históricos:
- Precio mínimo y máximo histórico
- Precio promedio
- Varianza de precio
- Precio hace 30 y 90 días

### mv_quote_performance
Análisis de rendimiento de cotizaciones:
- Número de vistas
- Estado de salud (Active, Expired, Stale)
- Ingresos generados
- Margen promedio

### mv_profitability_analysis
Análisis de rentabilidad por receta:
- Precio de venta sugerido
- Margen de ganancia
- Punto de equilibrio
- Ganancia por unidad

### mv_user_dashboard
Dashboard agregado por usuario:
- Total de materiales, recetas, cotizaciones
- Valor total del inventario
- Materiales con stock bajo
- Alertas activas
- Ingresos totales

---

## Cache y Optimización

### Estrategia de Cache
```java
// Caffeine Cache
@Cacheable(value = "brandingSettings", key = "#userId")
@Cacheable(value = "emailSettings", key = "#userId")
@Cacheable(value = "notificationPreferences", key = "#userId")
@Cacheable(value = "taxRates", key = "#countryCode + '_' + #regionCode")
```

### Rate Limiting
```java
// Bucket4j
- Login: 10 req/60s
- Registro: 5 req/300s
- Refresh Token: 20 req/60s
```

### Refresh de Vistas Materializadas
```sql
-- Manual
SELECT refresh_all_reporting_views();

-- Por usuario
SELECT refresh_user_reporting_views(user_id);

-- Programado (requiere pg_cron)
SELECT cron.schedule('refresh-reporting-views', '*/30 * * * *',
  'SELECT refresh_all_reporting_views()');
```

---

## Testing

### Ejecutar Tests
```bash
# Todos los tests
mvn test

# Tests de integración
mvn verify

# Tests con cobertura
mvn clean test jacoco:report
```

### Coverage Report
```bash
# Ver reporte en:
target/site/jacoco/index.html
```

---

## Deployment

### Docker
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build
docker build -t cronos-bakery-system .

# Run
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://db:5432/auth_db \
  -e JWT_SECRET=your-secret \
  cronos-bakery-system
```

### Docker Compose
```yaml
version: '3.8'
services:
  db:
    image: postgres:14
    environment:
      POSTGRES_DB: auth_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/auth_db
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - db

volumes:
  postgres-data:
```

---

## Monitoreo

### Actuator Endpoints
```
GET /actuator/health        - Estado de salud
GET /actuator/metrics       - Métricas del sistema
GET /actuator/info          - Información de la app
GET /actuator/prometheus    - Métricas para Prometheus
```

### Health Checks
- Database connectivity
- Disk space
- Cache status
- Email service

---

## API Documentation

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

---

## Seguridad

### JWT Flow
```
1. POST /auth/login → AccessToken (15 min) + RefreshToken (7 días)
2. Usar AccessToken en header: Authorization: Bearer <token>
3. Cuando expira → POST /auth/refresh con RefreshToken
4. Obtener nuevo AccessToken
```

### 2FA Flow
```
1. POST /auth/enable-2fa → QR code
2. Escanear con Google Authenticator
3. POST /auth/verify-2fa con código
4. 2FA habilitado
5. Login requiere código 2FA
```

### Rate Limiting
```
429 Too Many Requests
{
  "error": "Rate limit exceeded",
  "retryAfter": 45
}
```

---

## Troubleshooting

### Base de Datos no Conecta
```bash
# Verificar PostgreSQL corriendo
pg_isready

# Verificar credenciales
psql -U postgres -d auth_db
```

### Flyway Falla
```bash
# Limpiar y re-migrar (¡CUIDADO EN PRODUCCIÓN!)
mvn flyway:clean flyway:migrate

# Ver estado
mvn flyway:info
```

### Cache Issues
```bash
# Limpiar cache de Caffeine
# Reiniciar aplicación o
curl -X POST http://localhost:8080/actuator/caches/brandingSettings
```

### Vistas Materializadas Desactualizadas
```sql
-- Refrescar manualmente
SELECT refresh_all_reporting_views();
```

---

## Roadmap

### v2.0 (Planificado)
- [ ] Integración con sistemas de punto de venta
- [ ] App móvil (React Native)
- [ ] Módulo de producción y planificación
- [ ] Integración con contabilidad (SAT México)
- [ ] Machine Learning para predicción de demanda
- [ ] Módulo de empleados y nómina
- [ ] Sistema de facturación electrónica

### v2.1 (Futuro)
- [ ] Multi-tenant architecture
- [ ] Microservicios
- [ ] Kubernetes deployment
- [ ] GraphQL API
- [ ] Real-time collaboration

---

## Contribuir

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## Licencia

Este proyecto está bajo la licencia MIT. Ver `LICENSE` para más detalles.

---

## Contacto

**Cronos Bakery System Team**
- Email: support@cronosbakery.com
- GitHub: https://github.com/Anton-01/Cronos-Bakery-System

---

## Agradecimientos

- Spring Boot Team
- PostgreSQL Community
- Todos los contribuidores de librerías open source utilizadas

---

## Changelog

### v1.0.0 (2024-01-30)
- ✅ Sistema completo de gestión de costos
- ✅ Gestión de inventario con alertas
- ✅ Recetas dinámicas con versiones
- ✅ Cotizaciones profesionales
- ✅ Análisis financiero
- ✅ Multi-idioma (ES/EN)
- ✅ Multi-moneda (MXN/USD)
- ✅ Notificaciones en tiempo real
- ✅ Personalización avanzada
- ✅ Vistas materializadas
- ✅ Datos demo incluidos
- ✅ Documentación completa

---

**Desarrollado con por el equipo de Cronos Bakery System**
