# ESPECIFICACIONES TÉCNICAS: CRONOS BAKERY SYSTEM - FRONTEND ANGULAR

## 🎯 OBJETIVO DEL PROYECTO

Desarrollar una aplicación web frontend moderna y profesional en **Angular 18+** que consuma la API RESTful del backend **Cronos Bakery System** (Spring Boot). La aplicación debe proporcionar una interfaz completa para la gestión integral de panaderías, incluyendo autenticación segura, gestión de materias primas, recetas, cotizaciones, inventario y personalización.

---

## 📋 TABLA DE CONTENIDOS

1. [Stack Tecnológico](#stack-tecnológico)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Especificaciones de la API Backend](#especificaciones-de-la-api-backend)
4. [Estructura del Proyecto Angular](#estructura-del-proyecto-angular)
5. [Módulos y Funcionalidades](#módulos-y-funcionalidades)
6. [Configuración Docker](#configuración-docker)
7. [Mejores Prácticas](#mejores-prácticas)
8. [Seguridad](#seguridad)
9. [Testing](#testing)
10. [Deployment](#deployment)

---

## 🛠 STACK TECNOLÓGICO

### Framework y Versiones
- **Angular**: 18+ (última versión estable)
- **TypeScript**: 5.4+
- **Node.js**: 20+ LTS
- **Package Manager**: npm 10+

### Librerías Principales
- **UI Framework**: Angular Material 18+ o PrimeNG 17+
- **State Management**: NgRx 18+ (Store, Effects, Entity)
- **HTTP Client**: Angular HttpClient con Interceptors
- **Routing**: Angular Router con Guards
- **Forms**: Reactive Forms con validación custom
- **Charts**: Chart.js o ngx-charts
- **Date/Time**: date-fns o dayjs
- **PDF Generation**: jspdf + html2canvas
- **File Upload**: ng2-file-upload o custom implementation
- **Notifications**: ngx-toastr
- **Loading**: ngx-spinner
- **Icons**: Material Icons o Font Awesome
- **QR Code**: angularx-qrcode (para 2FA)

### Herramientas de Desarrollo
- **Linting**: ESLint + Angular ESLint
- **Formatting**: Prettier
- **Testing**: Jasmine + Karma + Jest
- **E2E Testing**: Cypress o Playwright
- **Build Tool**: Angular CLI + esbuild
- **Containerization**: Docker + Docker Compose
- **Git Hooks**: Husky + lint-staged

---

## 🏗 ARQUITECTURA DEL PROYECTO

### Patrón Arquitectónico
**Clean Architecture** con separación de capas:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Components, Pages, Guards, Pipes)     │
├─────────────────────────────────────────┤
│         Application Layer               │
│    (Services, State Management)         │
├─────────────────────────────────────────┤
│         Domain Layer                    │
│    (Models, Interfaces, Enums)          │
├─────────────────────────────────────────┤
│         Infrastructure Layer            │
│  (HTTP, Interceptors, Local Storage)    │
└─────────────────────────────────────────┘
```

### Principios de Diseño
- **SOLID Principles**
- **DRY (Don't Repeat Yourself)**
- **KISS (Keep It Simple, Stupid)**
- **Separation of Concerns**
- **Single Responsibility Principle**
- **Dependency Injection**
- **Reactive Programming** (RxJS)

---

## 🔌 ESPECIFICACIONES DE LA API BACKEND

### Información del Servidor
- **Base URL**: `http://localhost:8080/api/v1`
- **Protocolo**: HTTP/HTTPS
- **Formato**: JSON (application/json)
- **Autenticación**: JWT (Bearer Token)
- **CORS**: Habilitado para `http://localhost:4200`

### Sistema de Autenticación

#### Flujo de Autenticación JWT
1. **Login**: `POST /auth/login`
2. **Refresh Token**: `POST /auth/refresh`
3. **Logout**: `POST /auth/logout`
4. **Register**: `POST /auth/register`

#### Estructura del Token
```typescript
interface LoginResponse {
  accessToken: string;      // JWT válido por 15 minutos
  refreshToken: string;     // Token válido por 7 días
  tokenType: 'Bearer';
  expiresIn: number;        // Segundos hasta expiración
  username: string;
  email: string;
  roles: string[];
  requiresTwoFactor: boolean;
}
```

#### Headers de Autenticación
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

### Formato de Respuesta Genérico

Todas las respuestas del backend siguen este patrón:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string; // ISO 8601 format
}
```

### Endpoints Principales

#### 1. Autenticación (`/auth`)
```typescript
// Login
POST /auth/login
Body: { username: string; password: string; twoFactorCode?: number }
Response: ApiResponse<LoginResponse>

// Register
POST /auth/register
Body: {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  roles: string[];
}
Response: ApiResponse<UserResponse>

// Refresh Token
POST /auth/refresh
Body: { refreshToken: string }
Response: ApiResponse<TokenResponse>

// Logout
POST /auth/logout
Headers: Authorization
Response: ApiResponse<void>
```

#### 2. Materias Primas (`/raw-materials`)
```typescript
// Crear Material
POST /raw-materials
Body: CreateRawMaterialRequest
Response: ApiResponse<RawMaterialResponse> (status: 201)

// Actualizar Material
PUT /raw-materials/{id}
Body: UpdateRawMaterialRequest
Response: ApiResponse<RawMaterialResponse>

// Listar Materiales (paginado)
GET /raw-materials?page=0&size=20&sort=name,asc
Response: ApiResponse<Page<RawMaterialResponse>>

// Filtrar por Categoría (paginado)
GET /raw-materials/category/{categoryId}?page=0&size=20
Response: ApiResponse<Page<RawMaterialResponse>>

// Buscar Materiales (paginado)
GET /raw-materials/search?query=harina&page=0&size=20
Response: ApiResponse<Page<RawMaterialResponse>>

// Obtener Detalles
GET /raw-materials/{id}
Response: ApiResponse<RawMaterialResponse>

// Actualizar Stock
PATCH /raw-materials/{id}/stock?quantity=100&operation=INLET|OUTLET
Response: ApiResponse<RawMaterialResponse>

// Obtener Stock Bajo
GET /raw-materials/low-stock
Response: ApiResponse<List<RawMaterialResponse>>

// Historial de Precios (paginado)
GET /raw-materials/{id}/price-history?page=0&size=20
Response: ApiResponse<Page<PriceHistoryResponse>>

// Eliminar Material
DELETE /raw-materials/{id}
Response: ApiResponse<void>
```

#### 3. Recetas (`/recipes`)
```typescript
// Crear Receta
POST /recipes
Body: CreateRecipeRequest
Response: ApiResponse<RecipeResponse> (status: 201)

// Actualizar Receta
PUT /recipes/{id}?changes=descripción de cambios
Body: UpdateRecipeRequest
Response: ApiResponse<RecipeResponse>

// Listar Recetas (paginado)
GET /recipes?page=0&size=20&sort=name,asc
Response: ApiResponse<Page<RecipeResponse>>

// Buscar Recetas (paginado)
GET /recipes/search?query=pan&page=0&size=20
Response: ApiResponse<Page<RecipeResponse>>

// Obtener Detalles (con ingredientes)
GET /recipes/{id}
Response: ApiResponse<RecipeResponse>

// Calcular Costo (con escala)
POST /recipes/{id}/calculate-cost
Body: { scaleFactor: 2.0 }
Response: ApiResponse<RecipeCostResponse>

// Historial de Versiones
GET /recipes/{id}/versions?page=0&size=20
Response: ApiResponse<Page<RecipeVersionResponse>>

// Recalcular Costos por Material
POST /recipes/recalculate-material/{materialId}
Response: ApiResponse<List<RecipeResponse>>
```

#### 4. Cotizaciones (`/quotes`)
```typescript
// Crear Cotización
POST /quotes
Body: CreateQuoteRequest
Response: ApiResponse<QuoteResponse> (status: 201)

// Actualizar Cotización
PUT /quotes/{id}
Body: UpdateQuoteRequest
Response: ApiResponse<QuoteResponse>

// Listar Cotizaciones (paginado)
GET /quotes?page=0&size=20&status=DRAFT&sort=createdAt,desc
Response: ApiResponse<Page<QuoteResponse>>

// Compartir Cotización (generar enlace público)
POST /quotes/{id}/share
Response: ApiResponse<ShareQuoteResponse>

// Ver Cotización Pública (SIN AUTENTICACIÓN)
GET /quotes/shared/{token}
Response: ApiResponse<QuoteResponse>

// Enviar por Email
POST /quotes/{id}/send-email
Body: { recipientEmail: string; customMessage?: string }
Response: ApiResponse<void>

// Estadísticas de Acceso
GET /quotes/{id}/access-stats
Response: ApiResponse<QuoteAccessStats>
```

#### 5. Categorías (`/categories`)
```typescript
// Crear Categoría
POST /categories
Body: { name: string; description?: string }
Response: ApiResponse<CategoryResponse> (status: 201)

// Listar Categorías del Usuario
GET /categories
Response: ApiResponse<List<CategoryResponse>>

// Listar Categorías del Sistema
GET /categories/system
Response: ApiResponse<List<CategoryResponse>>
```

#### 6. Conversiones de Unidades (`/conversions`)
```typescript
// Crear Factor de Conversión Personalizado
POST /conversions
Body: {
  fromUnitId: number;
  toUnitId: number;
  factor: number;
  notes?: string
}
Response: ApiResponse<ConversionFactorResponse> (status: 201)

// Convertir entre Unidades
POST /conversions/convert
Body: {
  quantity: number;
  fromUnitId: number;
  toUnitId: number
}
Response: ApiResponse<ConversionResult>

// Obtener Conversiones del Usuario
GET /conversions
Response: ApiResponse<List<ConversionFactorResponse>>

// Obtener Todas las Unidades de Medida
GET /conversions/units
Response: ApiResponse<List<MeasurementUnitResponse>>
```

#### 7. Perfil de Usuario (`/profile`)
```typescript
// Obtener Perfil
GET /profile
Response: ApiResponse<UserProfileResponse>

// Actualizar Datos Personales
PUT /profile/personal
Body: PersonalDataRequest
Response: ApiResponse<UserProfileResponse>

// Actualizar Datos Empresariales
PUT /profile/business
Body: BusinessDataRequest
Response: ApiResponse<UserProfileResponse>

// Actualizar Preferencias
PUT /profile/preferences
Body: PreferencesRequest
Response: ApiResponse<UserProfileResponse>

// Subir Foto de Perfil
POST /profile/picture
Content-Type: multipart/form-data
Body: FormData { file: File }
Response: ApiResponse<{ profilePictureUrl: string }>

// Subir Foto de Portada
POST /profile/cover
Content-Type: multipart/form-data
Body: FormData { file: File }
Response: ApiResponse<{ coverPictureUrl: string }>

// Eliminar Foto de Perfil
DELETE /profile/picture
Response: ApiResponse<void>

// Eliminar Foto de Portada
DELETE /profile/cover
Response: ApiResponse<void>
```

#### 8. Gestión de Sesiones (`/sessions`)
```typescript
// Listar Sesiones Activas
GET /sessions
Response: ApiResponse<List<UserSessionResponse>>

// Listar Todas las Sesiones
GET /sessions/all
Response: ApiResponse<List<UserSessionResponse>>

// Terminar Sesión Específica
DELETE /sessions/{sessionId}
Response: ApiResponse<void>

// Terminar Todas las Demás Sesiones
POST /sessions/terminate-others
Response: ApiResponse<{ terminatedCount: number }>

// Listar Dispositivos de Confianza
GET /sessions/devices
Response: ApiResponse<List<DeviceFingerprintResponse>>

// Marcar Dispositivo como Confiable
POST /sessions/devices/{deviceId}/trust
Response: ApiResponse<DeviceFingerprintResponse>

// Quitar Confianza de Dispositivo
POST /sessions/devices/{deviceId}/untrust
Response: ApiResponse<DeviceFingerprintResponse>
```

#### 9. Dashboard (`/dashboard`)
```typescript
// Obtener Estadísticas del Dashboard
GET /dashboard
Response: ApiResponse<DashboardStats>

interface DashboardStats {
  totalRawMaterials: number;
  totalRecipes: number;
  totalQuotes: number;
  lowStockItems: number;
  pendingQuotes: number;
  totalRevenue: number;
  recentActivities: Activity[];
}
```

### Modelos de Datos TypeScript

#### Autenticación
```typescript
interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  roles: string[];
}

interface LoginRequest {
  username: string;
  password: string;
  twoFactorCode?: number;
}

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  username: string;
  email: string;
  roles: string[];
  requiresTwoFactor: boolean;
  message?: string;
}

interface RefreshTokenRequest {
  refreshToken: string;
}

interface TokenResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
}
```

#### Materias Primas
```typescript
interface CreateRawMaterialRequest {
  name: string;
  description?: string;
  brand: string;
  supplier: string;
  categoryId: number;
  purchaseUnitId: number;
  purchaseQuantity: number;
  unitCost: number;
  currency?: string;
  minimumStock?: number;
  allergenIds?: number[];
}

interface RawMaterialResponse {
  id: number;
  name: string;
  description?: string;
  brand: string;
  supplier: string;
  category: CategoryResponse;
  user: string;
  purchaseUnit: MeasurementUnitResponse;
  purchaseQuantity: number;
  unitCost: number;
  currency: string;
  currentStock: number;
  minimumStock: number;
  lastPurchaseDate?: string;
  lastPriceUpdate?: string;
  allergens: AllergenResponse[];
  needsRecalculation: boolean;
  isActive: boolean;
}

interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  isSystemDefault: boolean;
}

interface MeasurementUnitResponse {
  id: number;
  code: string;
  name: string;
  namePlural: string;
  type: 'WEIGHT' | 'VOLUME' | 'PIECE' | 'CONTAINER';
  isSystemDefault: boolean;
}

interface AllergenResponse {
  id: number;
  name: string;
  nameEn?: string;
  nameEs?: string;
  description?: string;
  isSystemDefault: boolean;
}
```

#### Recetas
```typescript
interface CreateRecipeRequest {
  name: string;
  description?: string;
  categoryId?: number;
  yieldQuantity: number;
  yieldUnit: string;
  preparationTimeMinutes?: number;
  bakingTimeMinutes?: number;
  coolingTimeMinutes?: number;
  instructions?: string;
  ingredients: RecipeIngredientRequest[];
  subRecipes?: RecipeSubRecipeRequest[];
  fixedCosts?: RecipeFixedCostRequest[];
}

interface RecipeIngredientRequest {
  rawMaterialId: number;
  quantity: number;
  unitId: number;
  displayOrder?: number;
  isOptional?: boolean;
  notes?: string;
}

interface RecipeSubRecipeRequest {
  subRecipeId: number;
  quantity: number;
  displayOrder?: number;
  notes?: string;
}

interface RecipeFixedCostRequest {
  name: string;
  description?: string;
  type: string;
  amount: number;
  calculationMethod: 'FIXED' | 'PER_MINUTE' | 'PERCENTAGE';
  timeInMinutes?: number;
  percentage?: number;
}

interface RecipeResponse {
  id: number;
  name: string;
  description?: string;
  user: string;
  category?: CategoryResponse;
  yieldQuantity: number;
  yieldUnit: string;
  preparationTimeMinutes?: number;
  bakingTimeMinutes?: number;
  coolingTimeMinutes?: number;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  ingredients: RecipeIngredientResponse[];
  subRecipes?: RecipeSubRecipeResponse[];
  fixedCosts?: RecipeFixedCostResponse[];
  allergens: AllergenResponse[];
  currentVersion: number;
  totalCost: number;
  instructions?: string;
  storageInstructions?: string;
  shelfLifeDays?: number;
  isActive: boolean;
  needsRecalculation: boolean;
}

interface RecipeCostResponse {
  materialsCost: number;
  fixedCosts: number;
  subRecipesCost: number;
  totalCost: number;
  costPerUnit: number;
  currency: string;
  scaleFactor: number;
}
```

#### Cotizaciones
```typescript
interface CreateQuoteRequest {
  clientName: string;
  clientEmail: string;
  clientPhone?: string;
  clientAddress?: string;
  notes?: string;
  validityDays?: number;
  items: QuoteItemRequest[];
}

interface QuoteItemRequest {
  recipeId: number;
  quantity: number;
  scaleFactor?: number;
  profitMarginId?: number;
}

interface QuoteResponse {
  id: number;
  quoteNumber: string;
  user: string;
  clientName: string;
  clientEmail: string;
  clientPhone?: string;
  clientAddress?: string;
  notes?: string;
  status: 'DRAFT' | 'SENT' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED';
  validUntil: string;
  subtotal: number;
  taxRate: number;
  taxAmount: number;
  total: number;
  currency: string;
  items: QuoteItemResponse[];
  shareToken?: string;
  isShareable: boolean;
  createdAt: string;
}

interface QuoteItemResponse {
  id: number;
  recipe: RecipeResponse;
  quantity: number;
  scaleFactor: number;
  unitCost: number;
  unitPrice: number;
  profitPercentage: number;
  subtotal: number;
  notes?: string;
  displayOrder: number;
}

interface ShareQuoteResponse {
  shareToken: string;
  shareUrl: string;
  expiresAt: string;
}
```

### Paginación

El backend implementa paginación estándar de Spring Data:

```typescript
interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}
```

### Manejo de Errores

El backend devuelve errores en formato consistente:

```typescript
interface ErrorResponse {
  success: false;
  message: string;
  data: null;
  timestamp: string;
  // Campos adicionales en caso de errores de validación
  errors?: {
    field: string;
    message: string;
  }[];
}
```

**Códigos HTTP comunes**:
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado
- `400 Bad Request`: Validación fallida
- `401 Unauthorized`: No autenticado o token inválido
- `403 Forbidden`: Sin permisos
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto (ej. username duplicado)
- `500 Internal Server Error`: Error del servidor

---

## 📁 ESTRUCTURA DEL PROYECTO ANGULAR

```
cronos-bakery-frontend/
├── .husky/                           # Git hooks
├── .vscode/                          # Configuración VSCode
├── docker/                           # Archivos Docker
│   ├── Dockerfile
│   ├── Dockerfile.prod
│   ├── nginx.conf
│   └── docker-compose.yml
├── src/
│   ├── app/
│   │   ├── core/                     # Singleton services, guards, interceptors
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   ├── role.guard.ts
│   │   │   │   └── unsaved-changes.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   ├── error.interceptor.ts
│   │   │   │   ├── loading.interceptor.ts
│   │   │   │   └── retry.interceptor.ts
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── storage.service.ts
│   │   │   │   ├── notification.service.ts
│   │   │   │   └── theme.service.ts
│   │   │   └── core.module.ts
│   │   │
│   │   ├── shared/                   # Shared components, directives, pipes
│   │   │   ├── components/
│   │   │   │   ├── confirmation-dialog/
│   │   │   │   ├── data-table/
│   │   │   │   ├── file-upload/
│   │   │   │   ├── loading-spinner/
│   │   │   │   ├── page-header/
│   │   │   │   └── breadcrumb/
│   │   │   ├── directives/
│   │   │   │   ├── has-role.directive.ts
│   │   │   │   ├── debounce-click.directive.ts
│   │   │   │   └── highlight.directive.ts
│   │   │   ├── pipes/
│   │   │   │   ├── currency-format.pipe.ts
│   │   │   │   ├── date-format.pipe.ts
│   │   │   │   ├── truncate.pipe.ts
│   │   │   │   └── safe-html.pipe.ts
│   │   │   ├── models/
│   │   │   │   ├── api-response.model.ts
│   │   │   │   ├── page.model.ts
│   │   │   │   └── common.models.ts
│   │   │   ├── validators/
│   │   │   │   ├── custom-validators.ts
│   │   │   │   └── async-validators.ts
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── features/                 # Feature modules (lazy loaded)
│   │   │   ├── auth/
│   │   │   │   ├── components/
│   │   │   │   │   ├── login/
│   │   │   │   │   ├── register/
│   │   │   │   │   ├── forgot-password/
│   │   │   │   │   └── two-factor-auth/
│   │   │   │   ├── auth-routing.module.ts
│   │   │   │   └── auth.module.ts
│   │   │   │
│   │   │   ├── dashboard/
│   │   │   │   ├── components/
│   │   │   │   │   ├── dashboard-home/
│   │   │   │   │   ├── stats-card/
│   │   │   │   │   ├── recent-activity/
│   │   │   │   │   └── charts/
│   │   │   │   ├── services/
│   │   │   │   │   └── dashboard.service.ts
│   │   │   │   ├── dashboard-routing.module.ts
│   │   │   │   └── dashboard.module.ts
│   │   │   │
│   │   │   ├── raw-materials/
│   │   │   │   ├── components/
│   │   │   │   │   ├── material-list/
│   │   │   │   │   ├── material-detail/
│   │   │   │   │   ├── material-form/
│   │   │   │   │   ├── material-search/
│   │   │   │   │   ├── stock-adjustment/
│   │   │   │   │   ├── price-history/
│   │   │   │   │   └── low-stock-alert/
│   │   │   │   ├── services/
│   │   │   │   │   └── raw-material.service.ts
│   │   │   │   ├── models/
│   │   │   │   │   └── raw-material.model.ts
│   │   │   │   ├── state/
│   │   │   │   │   ├── raw-material.actions.ts
│   │   │   │   │   ├── raw-material.reducer.ts
│   │   │   │   │   ├── raw-material.effects.ts
│   │   │   │   │   └── raw-material.selectors.ts
│   │   │   │   ├── raw-materials-routing.module.ts
│   │   │   │   └── raw-materials.module.ts
│   │   │   │
│   │   │   ├── recipes/
│   │   │   │   ├── components/
│   │   │   │   │   ├── recipe-list/
│   │   │   │   │   ├── recipe-detail/
│   │   │   │   │   ├── recipe-form/
│   │   │   │   │   ├── recipe-search/
│   │   │   │   │   ├── ingredient-selector/
│   │   │   │   │   ├── sub-recipe-selector/
│   │   │   │   │   ├── fixed-costs-editor/
│   │   │   │   │   ├── cost-calculator/
│   │   │   │   │   ├── recipe-versions/
│   │   │   │   │   ├── allergen-viewer/
│   │   │   │   │   └── recipe-print/
│   │   │   │   ├── services/
│   │   │   │   │   └── recipe.service.ts
│   │   │   │   ├── models/
│   │   │   │   │   └── recipe.model.ts
│   │   │   │   ├── state/
│   │   │   │   │   ├── recipe.actions.ts
│   │   │   │   │   ├── recipe.reducer.ts
│   │   │   │   │   ├── recipe.effects.ts
│   │   │   │   │   └── recipe.selectors.ts
│   │   │   │   ├── recipes-routing.module.ts
│   │   │   │   └── recipes.module.ts
│   │   │   │
│   │   │   ├── quotes/
│   │   │   │   ├── components/
│   │   │   │   │   ├── quote-list/
│   │   │   │   │   ├── quote-detail/
│   │   │   │   │   ├── quote-form/
│   │   │   │   │   ├── quote-preview/
│   │   │   │   │   ├── quote-share/
│   │   │   │   │   ├── quote-public-view/
│   │   │   │   │   ├── quote-pdf-export/
│   │   │   │   │   ├── quote-send-email/
│   │   │   │   │   └── quote-stats/
│   │   │   │   ├── services/
│   │   │   │   │   └── quote.service.ts
│   │   │   │   ├── models/
│   │   │   │   │   └── quote.model.ts
│   │   │   │   ├── state/
│   │   │   │   │   ├── quote.actions.ts
│   │   │   │   │   ├── quote.reducer.ts
│   │   │   │   │   ├── quote.effects.ts
│   │   │   │   │   └── quote.selectors.ts
│   │   │   │   ├── quotes-routing.module.ts
│   │   │   │   └── quotes.module.ts
│   │   │   │
│   │   │   ├── inventory/
│   │   │   │   ├── components/
│   │   │   │   │   ├── stock-overview/
│   │   │   │   │   ├── stock-alerts/
│   │   │   │   │   ├── stock-movements/
│   │   │   │   │   └── stock-reports/
│   │   │   │   ├── services/
│   │   │   │   │   └── inventory.service.ts
│   │   │   │   ├── inventory-routing.module.ts
│   │   │   │   └── inventory.module.ts
│   │   │   │
│   │   │   ├── settings/
│   │   │   │   ├── components/
│   │   │   │   │   ├── profile-settings/
│   │   │   │   │   ├── business-settings/
│   │   │   │   │   ├── branding-settings/
│   │   │   │   │   ├── email-settings/
│   │   │   │   │   ├── notification-settings/
│   │   │   │   │   ├── session-management/
│   │   │   │   │   ├── device-management/
│   │   │   │   │   ├── categories-settings/
│   │   │   │   │   ├── conversions-settings/
│   │   │   │   │   ├── security-settings/
│   │   │   │   │   └── two-factor-setup/
│   │   │   │   ├── services/
│   │   │   │   │   ├── profile.service.ts
│   │   │   │   │   └── settings.service.ts
│   │   │   │   ├── settings-routing.module.ts
│   │   │   │   └── settings.module.ts
│   │   │   │
│   │   │   └── reports/
│   │   │       ├── components/
│   │   │       │   ├── reports-dashboard/
│   │   │       │   ├── cost-analysis/
│   │   │       │   ├── sales-report/
│   │   │       │   └── inventory-report/
│   │   │       ├── services/
│   │   │       │   └── reports.service.ts
│   │   │       ├── reports-routing.module.ts
│   │   │       └── reports.module.ts
│   │   │
│   │   ├── layout/                   # Layout components
│   │   │   ├── main-layout/
│   │   │   │   ├── main-layout.component.ts
│   │   │   │   ├── main-layout.component.html
│   │   │   │   └── main-layout.component.scss
│   │   │   ├── header/
│   │   │   │   ├── header.component.ts
│   │   │   │   ├── header.component.html
│   │   │   │   └── header.component.scss
│   │   │   ├── sidebar/
│   │   │   │   ├── sidebar.component.ts
│   │   │   │   ├── sidebar.component.html
│   │   │   │   └── sidebar.component.scss
│   │   │   ├── footer/
│   │   │   │   ├── footer.component.ts
│   │   │   │   ├── footer.component.html
│   │   │   │   └── footer.component.scss
│   │   │   └── layout.module.ts
│   │   │
│   │   ├── store/                    # NgRx root store
│   │   │   ├── app.state.ts
│   │   │   ├── app.effects.ts
│   │   │   ├── router/
│   │   │   │   └── router.selectors.ts
│   │   │   └── index.ts
│   │   │
│   │   ├── app-routing.module.ts
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   └── app.module.ts
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   ├── fonts/
│   │   ├── i18n/
│   │   │   ├── en.json
│   │   │   └── es.json
│   │   └── styles/
│   │       └── themes/
│   │
│   ├── environments/
│   │   ├── environment.ts
│   │   ├── environment.development.ts
│   │   └── environment.production.ts
│   │
│   ├── styles/
│   │   ├── _variables.scss
│   │   ├── _mixins.scss
│   │   ├── _typography.scss
│   │   ├── _utilities.scss
│   │   └── styles.scss
│   │
│   ├── index.html
│   ├── main.ts
│   └── polyfills.ts
│
├── .editorconfig
├── .eslintrc.json
├── .prettierrc
├── .gitignore
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
├── karma.conf.js
├── cypress.config.ts
└── README.md
```

---

## 🎨 MÓDULOS Y FUNCIONALIDADES

### 1. Módulo de Autenticación (`AuthModule`)

**Componentes**:
- **LoginComponent**: Formulario de login con validación
  - Usuario/Email
  - Contraseña
  - Opción "Recordarme"
  - Enlace a "Olvidé mi contraseña"
  - Enlace a registro
  - Soporte para 2FA

- **RegisterComponent**: Formulario de registro
  - Validación de contraseña (PolicyValidator)
  - Confirmación de contraseña
  - Validación de email
  - Selección de roles
  - Términos y condiciones

- **TwoFactorAuthComponent**: Verificación 2FA
  - Input de 6 dígitos para código TOTP
  - QR Code para configurar Google Authenticator
  - Códigos de respaldo

- **ForgotPasswordComponent**: Recuperación de contraseña

**Servicios**:
- **AuthService**: Gestión de autenticación
  ```typescript
  class AuthService {
    login(credentials: LoginRequest): Observable<LoginResponse>
    register(user: CreateUserRequest): Observable<UserResponse>
    logout(): Observable<void>
    refreshToken(): Observable<TokenResponse>
    isAuthenticated(): boolean
    getAccessToken(): string | null
    getRefreshToken(): string | null
    getCurrentUser(): Observable<User>
    hasRole(role: string): boolean
    hasAnyRole(roles: string[]): boolean
  }
  ```

**Guards**:
- **AuthGuard**: Proteger rutas que requieren autenticación
- **RoleGuard**: Proteger rutas por roles
- **GuestGuard**: Redirigir usuarios autenticados

**Interceptors**:
- **AuthInterceptor**: Añadir token JWT a headers
- **ErrorInterceptor**: Manejar errores 401/403
- **RetryInterceptor**: Reintentar requests fallidos

---

### 2. Módulo de Dashboard (`DashboardModule`)

**Componentes**:
- **DashboardHomeComponent**: Vista principal
  - Cards de estadísticas (total materiales, recetas, cotizaciones)
  - Gráficos de costos y ventas
  - Alertas de stock bajo
  - Actividad reciente
  - Accesos rápidos

- **StatsCardComponent**: Card reutilizable para métricas
- **RecentActivityComponent**: Lista de actividades recientes
- **ChartsComponent**: Gráficos (Chart.js)

**Servicios**:
- **DashboardService**: Obtener estadísticas

---

### 3. Módulo de Materias Primas (`RawMaterialsModule`)

**Componentes**:
- **MaterialListComponent**: Tabla paginada de materiales
  - Búsqueda y filtros (categoría, stock bajo)
  - Ordenamiento
  - Acciones: Ver, Editar, Eliminar, Ajustar stock
  - Indicadores de stock bajo

- **MaterialDetailComponent**: Vista de detalles
  - Información completa
  - Historial de precios (gráfico)
  - Alérgenos
  - Recetas que usan este material

- **MaterialFormComponent**: Formulario crear/editar
  - Reactive Forms con validación
  - Selector de categoría
  - Selector de unidad de medida
  - Multi-selector de alérgenos
  - Campos de stock

- **StockAdjustmentComponent**: Ajustar stock (INLET/OUTLET)

- **PriceHistoryComponent**: Gráfico y tabla de historial

**Servicios**:
- **RawMaterialService**: CRUD y operaciones

**State Management (NgRx)**:
- Actions: Load, Create, Update, Delete, AdjustStock
- Reducer: Manejo de estado
- Effects: Side effects (HTTP)
- Selectors: Queries al estado

---

### 4. Módulo de Recetas (`RecipesModule`)

**Componentes**:
- **RecipeListComponent**: Tabla paginada de recetas
  - Búsqueda
  - Filtros (categoría, estado)
  - Vista de cards/lista
  - Indicador de necesita recalculación

- **RecipeDetailComponent**: Vista detallada
  - Información general
  - Lista de ingredientes con cantidades
  - Sub-recetas
  - Costos fijos
  - Costo total y por unidad
  - Instrucciones
  - Alérgenos
  - Tiempos de preparación/cocción
  - Versiones

- **RecipeFormComponent**: Formulario complejo
  - Wizard multi-paso:
    1. Información general
    2. Ingredientes (selector dinámico)
    3. Sub-recetas (selector dinámico)
    4. Costos fijos
    5. Instrucciones
    6. Archivos adjuntos
  - Validación compleja
  - Vista previa de costo

- **IngredientSelectorComponent**: Selector de ingredientes
  - Búsqueda de materiales
  - Cantidad y unidad
  - Reordenar (drag & drop)
  - Marcador de opcional

- **CostCalculatorComponent**: Calculadora de costos
  - Cambiar factor de escala
  - Ver desglose de costos
  - Exportar a PDF

- **RecipeVersionsComponent**: Historial de versiones
  - Comparar versiones
  - Restaurar versión

**Servicios**:
- **RecipeService**: CRUD y cálculos

**State Management (NgRx)**:
- Similar a RawMaterials

---

### 5. Módulo de Cotizaciones (`QuotesModule`)

**Componentes**:
- **QuoteListComponent**: Tabla de cotizaciones
  - Filtros (estado, cliente, fecha)
  - Estados con badges de color
  - Acciones: Ver, Editar, Compartir, Enviar email

- **QuoteDetailComponent**: Vista detallada
  - Información del cliente
  - Items con recetas
  - Totales (subtotal, impuestos, total)
  - Estado
  - Validez
  - Enlace compartible (si existe)
  - Estadísticas de acceso

- **QuoteFormComponent**: Formulario crear/editar
  - Wizard:
    1. Información del cliente
    2. Items (selector de recetas, cantidad, margen)
    3. Revisión
  - Cálculo automático de totales
  - Vista previa

- **QuotePreviewComponent**: Vista previa de PDF

- **QuoteShareComponent**: Generar y compartir enlace
  - Botón copiar enlace
  - QR Code del enlace
  - Fecha de expiración

- **QuotePublicViewComponent**: Vista pública (sin auth)
  - Diseño limpio para clientes
  - Opción imprimir/descargar PDF

- **QuotePdfExportComponent**: Exportar a PDF
  - Plantilla personalizada
  - Logo y branding

- **QuoteSendEmailComponent**: Enviar por email
  - Formulario de email
  - Mensaje personalizado

**Servicios**:
- **QuoteService**: CRUD y operaciones

**State Management (NgRx)**:
- Similar a anteriores

---

### 6. Módulo de Inventario (`InventoryModule`)

**Componentes**:
- **StockOverviewComponent**: Vista general de stock
- **StockAlertsComponent**: Alertas activas
- **StockMovementsComponent**: Historial de movimientos
- **StockReportsComponent**: Reportes de inventario

---

### 7. Módulo de Configuración (`SettingsModule`)

**Componentes**:
- **ProfileSettingsComponent**: Configuración de perfil
  - Datos personales
  - Foto de perfil
  - Foto de portada
  - Preferencias (idioma, moneda, zona horaria)

- **BusinessSettingsComponent**: Datos empresariales
  - Información de la empresa
  - Dirección fiscal
  - Redes sociales

- **BrandingSettingsComponent**: Personalización
  - Logo
  - Colores
  - Fuentes

- **EmailSettingsComponent**: Configuración de email
  - SMTP personalizado
  - Plantillas de email

- **NotificationSettingsComponent**: Preferencias de notificaciones
  - Tipos de notificaciones
  - Canales
  - Quiet hours

- **SessionManagementComponent**: Gestión de sesiones
  - Sesiones activas
  - Terminar sesiones
  - Dispositivos de confianza

- **SecuritySettingsComponent**: Configuración de seguridad
  - Cambiar contraseña
  - Configurar 2FA
  - Historial de login

- **CategoriesSettingsComponent**: Gestión de categorías

- **ConversionsSettingsComponent**: Factores de conversión

**Servicios**:
- **ProfileService**: Gestión de perfil
- **SettingsService**: Configuraciones

---

### 8. Módulo de Reportes (`ReportsModule`)

**Componentes**:
- **ReportsDashboardComponent**: Dashboard de reportes
- **CostAnalysisComponent**: Análisis de costos
- **SalesReportComponent**: Reporte de ventas
- **InventoryReportComponent**: Reporte de inventario

---

## 🐳 CONFIGURACIÓN DOCKER

### Dockerfile (Desarrollo)

```dockerfile
# Etapa de construcción
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Build de la aplicación
RUN npm run build

# Etapa de producción
FROM nginx:1.25-alpine

# Copiar archivos compilados
COPY --from=build /app/dist/cronos-bakery-frontend /usr/share/nginx/html

# Copiar configuración de nginx
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### Dockerfile.prod (Producción optimizada)

```dockerfile
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build:prod

FROM nginx:1.25-alpine

COPY --from=build /app/dist/cronos-bakery-frontend /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

# Optimizaciones de seguridad
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

### nginx.conf

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Compresión GZIP
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied expired no-cache no-store private auth;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/javascript application/json;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Cacheo de assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Ruteo de SPA (Single Page Application)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    # Proxy para API (opcional si se quiere evitar CORS)
    location /api/ {
        proxy_pass http://backend:8080/api/v1/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  frontend:
    build:
      context: .
      dockerfile: docker/Dockerfile
    container_name: cronos-frontend
    ports:
      - "4200:80"
    environment:
      - API_URL=http://backend:8080/api/v1
    networks:
      - cronos-network
    depends_on:
      - backend
    restart: unless-stopped

  backend:
    image: cronos-bakery-backend:latest
    container_name: cronos-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cronos_bakery
      - SPRING_DATASOURCE_USERNAME=cronos
      - SPRING_DATASOURCE_PASSWORD=cronos123
      - JWT_SECRET=your-secret-key-here-min-256-bits
    networks:
      - cronos-network
    depends_on:
      - postgres
    restart: unless-stopped

  postgres:
    image: postgres:15-alpine
    container_name: cronos-postgres
    environment:
      - POSTGRES_DB=cronos_bakery
      - POSTGRES_USER=cronos
      - POSTGRES_PASSWORD=cronos123
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - cronos-network
    restart: unless-stopped

networks:
  cronos-network:
    driver: bridge

volumes:
  postgres-data:
```

### .dockerignore

```
node_modules
npm-debug.log
dist
.angular
.git
.gitignore
.editorconfig
README.md
.vscode
coverage
e2e
*.md
.env
```

---

## ✅ MEJORES PRÁCTICAS

### 1. Arquitectura y Organización

- **Feature Modules**: Módulos separados por funcionalidad
- **Lazy Loading**: Cargar módulos bajo demanda
- **Smart/Dumb Components**: Componentes inteligentes (container) vs presentacionales
- **Shared Module**: Componentes, directivas y pipes reutilizables
- **Core Module**: Servicios singleton (importar solo en AppModule)
- **One-Time Binding**: Usar estrategia OnPush cuando sea posible

### 2. Estado y Datos

- **NgRx**: State management para datos complejos
- **Inmutabilidad**: Nunca mutar estado directamente
- **Selectors**: Memoización de queries
- **Effects**: Side effects aislados
- **Entity Adapter**: Normalizar colecciones

### 3. Performance

- **TrackBy**: En *ngFor para optimizar renders
- **OnPush Strategy**: Reducir change detection
- **Lazy Loading**: Módulos y componentes
- **Virtual Scrolling**: Para listas largas (CDK)
- **Debounce**: En búsquedas y filtros
- **Unsubscribe**: Prevenir memory leaks (async pipe, takeUntil)

### 4. Formularios

- **Reactive Forms**: Preferir sobre Template-driven
- **FormBuilder**: Para simplificar creación
- **Custom Validators**: Validación reutilizable
- **Async Validators**: Para validación en backend
- **Dynamic Forms**: Generar formularios desde configuración

### 5. HTTP y API

- **Interceptors**: Para logging, auth, error handling
- **Retry Logic**: Reintentar requests fallidos
- **Caching**: Cache de datos inmutables
- **Type Safety**: Interfaces TypeScript para requests/responses
- **Error Handling**: Manejo centralizado de errores

### 6. Seguridad

- **Sanitización**: Usar DomSanitizer para HTML dinámico
- **CSRF Protection**: Tokens en forms
- **XSS Prevention**: Evitar innerHTML sin sanitizar
- **Secure Storage**: No guardar datos sensibles en localStorage
- **Environment Variables**: Para configuración sensible

### 7. Estilos

- **SCSS**: Usar preprocessor
- **BEM Methodology**: Nombrado de clases
- **Component Styles**: Encapsulación por componente
- **Theme System**: Variables CSS o SCSS
- **Responsive Design**: Mobile-first
- **Accessibility**: ARIA labels, roles, keyboard navigation

### 8. Testing

- **Unit Tests**: Mínimo 80% coverage
- **Integration Tests**: Para flujos críticos
- **E2E Tests**: Para user journeys principales
- **Mock Services**: Para tests aislados
- **Test Utilities**: Helpers reutilizables

### 9. Código Limpio

- **TypeScript Strict**: Habilitar strict mode
- **ESLint Rules**: Configuración estricta
- **Prettier**: Formateo automático
- **Naming Conventions**: Angular style guide
- **Comments**: JSDoc para funciones públicas
- **Magic Numbers**: Usar constantes

### 10. Deployment

- **Environment Files**: Para cada entorno
- **Build Optimization**: AOT, tree-shaking, minification
- **Bundle Analysis**: webpack-bundle-analyzer
- **CI/CD**: Automatización de build y deploy
- **Monitoring**: Error tracking (Sentry, etc.)

---

## 🔐 SEGURIDAD

### 1. Autenticación y Autorización

#### Almacenamiento de Tokens
```typescript
// NUNCA en localStorage (vulnerable a XSS)
// USAR: httpOnly cookies o memoria

// Opción 1: Memoria (más seguro)
class AuthService {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  setTokens(access: string, refresh: string) {
    this.accessToken = access;
    this.refreshToken = refresh;
  }
}

// Opción 2: httpOnly cookie (configurar en backend)
```

#### Auth Interceptor
```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getAccessToken();

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req);
  }
}
```

#### Error Interceptor (Auto-refresh)
```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !req.url.includes('/auth/')) {
          // Intentar refresh token
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              // Reintentar request original con nuevo token
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${this.authService.getAccessToken()}`
                }
              });
              return next.handle(newReq);
            }),
            catchError(() => {
              // Refresh falló, logout
              this.authService.logout();
              return throwError(() => error);
            })
          );
        }
        return throwError(() => error);
      })
    );
  }
}
```

### 2. Guards de Ruta

#### Auth Guard
```typescript
@Injectable()
export class AuthGuard implements CanActivate {
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    this.router.navigate(['/auth/login'], {
      queryParams: { returnUrl: state.url }
    });
    return false;
  }
}
```

#### Role Guard
```typescript
@Injectable()
export class RoleGuard implements CanActivate {
  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];

    if (this.authService.hasAnyRole(requiredRoles)) {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
  }
}
```

### 3. Sanitización

```typescript
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({ name: 'safeHtml' })
export class SafeHtmlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(html: string): SafeHtml {
    return this.sanitizer.sanitize(SecurityContext.HTML, html) || '';
  }
}
```

### 4. Validación de Input

```typescript
export class CustomValidators {
  static noScriptTags(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (value && /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi.test(value)) {
      return { scriptTag: true };
    }
    return null;
  }

  static strongPassword(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /[0-9]/.test(value);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    const isLengthValid = value.length >= 8;

    const passwordValid = hasUpperCase && hasLowerCase && hasNumeric && hasSpecial && isLengthValid;

    return !passwordValid ? { weakPassword: true } : null;
  }
}
```

---

## 🧪 TESTING

### 1. Configuración de Jasmine/Karma

**karma.conf.js**:
```javascript
module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {
        random: false
      },
      clearContext: false
    },
    jasmineHtmlReporter: {
      suppressAll: true
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcovonly' }
      ],
      check: {
        global: {
          statements: 80,
          branches: 80,
          functions: 80,
          lines: 80
        }
      }
    },
    reporters: ['progress', 'kjhtml', 'coverage'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
```

### 2. Test de Componente

```typescript
describe('MaterialListComponent', () => {
  let component: MaterialListComponent;
  let fixture: ComponentFixture<MaterialListComponent>;
  let mockMaterialService: jasmine.SpyObj<RawMaterialService>;

  beforeEach(async () => {
    mockMaterialService = jasmine.createSpyObj('RawMaterialService', ['getMaterials']);

    await TestBed.configureTestingModule({
      declarations: [MaterialListComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: RawMaterialService, useValue: mockMaterialService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MaterialListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load materials on init', () => {
    const mockMaterials = [/* mock data */];
    mockMaterialService.getMaterials.and.returnValue(of(mockMaterials));

    component.ngOnInit();

    expect(mockMaterialService.getMaterials).toHaveBeenCalled();
    expect(component.materials).toEqual(mockMaterials);
  });
});
```

### 3. Test de Servicio

```typescript
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should login successfully', () => {
    const mockResponse: LoginResponse = {
      accessToken: 'token',
      refreshToken: 'refresh',
      tokenType: 'Bearer',
      expiresIn: 900,
      username: 'test',
      email: 'test@test.com',
      roles: ['ROLE_USER'],
      requiresTwoFactor: false
    };

    service.login({ username: 'test', password: 'pass' }).subscribe(response => {
      expect(response.accessToken).toBe('token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, data: mockResponse });
  });
});
```

### 4. Test E2E con Cypress

```typescript
describe('Login Flow', () => {
  beforeEach(() => {
    cy.visit('/auth/login');
  });

  it('should login successfully with valid credentials', () => {
    cy.get('[data-cy=username]').type('testuser');
    cy.get('[data-cy=password]').type('Password123!');
    cy.get('[data-cy=login-btn]').click();

    cy.url().should('include', '/dashboard');
    cy.get('[data-cy=user-menu]').should('contain', 'testuser');
  });

  it('should show error with invalid credentials', () => {
    cy.get('[data-cy=username]').type('invalid');
    cy.get('[data-cy=password]').type('wrong');
    cy.get('[data-cy=login-btn]').click();

    cy.get('[data-cy=error-message]').should('be.visible');
  });
});
```

---

## 🚀 DEPLOYMENT

### 1. Build de Producción

```json
// package.json scripts
{
  "scripts": {
    "start": "ng serve",
    "build": "ng build",
    "build:prod": "ng build --configuration production",
    "test": "ng test",
    "test:ci": "ng test --watch=false --code-coverage",
    "lint": "ng lint",
    "e2e": "cypress run",
    "analyze": "ng build --stats-json && webpack-bundle-analyzer dist/stats.json"
  }
}
```

### 2. Variables de Entorno

**environment.ts** (desarrollo):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  appName: 'Cronos Bakery System',
  appVersion: '1.0.0',
  enableDebug: true,
  enableServiceWorker: false
};
```

**environment.production.ts**:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.cronos-bakery.com/api/v1',
  appName: 'Cronos Bakery System',
  appVersion: '1.0.0',
  enableDebug: false,
  enableServiceWorker: true
};
```

### 3. CI/CD Pipeline (GitHub Actions)

**.github/workflows/ci.yml**:
```yaml
name: CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '20'

      - name: Install dependencies
        run: npm ci

      - name: Lint
        run: npm run lint

      - name: Test
        run: npm run test:ci

      - name: Build
        run: npm run build:prod

      - name: Upload coverage
        uses: codecov/codecov-action@v3

      - name: Build Docker image
        run: docker build -f docker/Dockerfile.prod -t cronos-frontend:${{ github.sha }} .

      - name: Push to registry
        if: github.ref == 'refs/heads/main'
        run: |
          docker tag cronos-frontend:${{ github.sha }} cronos-frontend:latest
          docker push cronos-frontend:latest
```

---

## 📦 DEPENDENCIAS PRINCIPALES

### package.json

```json
{
  "name": "cronos-bakery-frontend",
  "version": "1.0.0",
  "scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "build:prod": "ng build --configuration production",
    "test": "ng test",
    "lint": "ng lint",
    "e2e": "cypress run",
    "prepare": "husky install"
  },
  "dependencies": {
    "@angular/animations": "^18.0.0",
    "@angular/common": "^18.0.0",
    "@angular/compiler": "^18.0.0",
    "@angular/core": "^18.0.0",
    "@angular/forms": "^18.0.0",
    "@angular/material": "^18.0.0",
    "@angular/platform-browser": "^18.0.0",
    "@angular/platform-browser-dynamic": "^18.0.0",
    "@angular/router": "^18.0.0",
    "@ngrx/effects": "^18.0.0",
    "@ngrx/entity": "^18.0.0",
    "@ngrx/router-store": "^18.0.0",
    "@ngrx/store": "^18.0.0",
    "@ngrx/store-devtools": "^18.0.0",
    "rxjs": "^7.8.0",
    "tslib": "^2.6.0",
    "zone.js": "^0.14.0",
    "chart.js": "^4.4.0",
    "ng2-charts": "^6.0.0",
    "date-fns": "^3.0.0",
    "ngx-toastr": "^18.0.0",
    "ngx-spinner": "^17.0.0",
    "angularx-qrcode": "^18.0.0",
    "jspdf": "^2.5.0",
    "html2canvas": "^1.4.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^18.0.0",
    "@angular/cli": "^18.0.0",
    "@angular/compiler-cli": "^18.0.0",
    "@types/jasmine": "^5.1.0",
    "@types/node": "^20.0.0",
    "@typescript-eslint/eslint-plugin": "^7.0.0",
    "@typescript-eslint/parser": "^7.0.0",
    "eslint": "^8.57.0",
    "prettier": "^3.2.0",
    "husky": "^9.0.0",
    "lint-staged": "^15.0.0",
    "jasmine-core": "^5.1.0",
    "karma": "^6.4.0",
    "karma-chrome-launcher": "^3.2.0",
    "karma-coverage": "^2.2.0",
    "karma-jasmine": "^5.1.0",
    "karma-jasmine-html-reporter": "^2.1.0",
    "cypress": "^13.6.0",
    "typescript": "~5.4.0"
  }
}
```

---

## 📝 NOTAS FINALES

### Prioridades de Implementación

1. **Fase 1 - Core**:
   - Setup inicial del proyecto
   - Configuración de Angular Material/PrimeNG
   - Módulo de autenticación completo
   - Layout principal (header, sidebar, footer)
   - Interceptors (auth, error, loading)
   - Guards (auth, role)

2. **Fase 2 - Funcionalidades Básicas**:
   - Dashboard con estadísticas básicas
   - CRUD de Materias Primas
   - CRUD de Categorías
   - Gestión de unidades y conversiones

3. **Fase 3 - Funcionalidades Avanzadas**:
   - CRUD de Recetas (complejo)
   - Calculadora de costos
   - Gestión de versiones de recetas

4. **Fase 4 - Cotizaciones**:
   - CRUD de Cotizaciones
   - Vista pública compartible
   - Exportación a PDF
   - Envío por email

5. **Fase 5 - Configuración y Personalización**:
   - Perfil de usuario
   - Configuración de branding
   - Plantillas de email
   - Notificaciones

6. **Fase 6 - Inventario y Reportes**:
   - Gestión de inventario
   - Alertas de stock
   - Reportes y análisis

7. **Fase 7 - Optimización**:
   - Performance tuning
   - Testing exhaustivo
   - Documentación
   - Docker y deployment

### Consideraciones Importantes

1. **Responsive Design**: La aplicación debe funcionar perfectamente en móviles, tablets y desktop
2. **Accesibilidad**: Seguir WCAG 2.1 AA
3. **Internacionalización**: Preparar para soporte multi-idioma (ES, EN)
4. **PWA**: Considerar convertir en Progressive Web App
5. **Offline Support**: Considerar soporte offline básico
6. **Monitoreo**: Integrar Sentry o similar para error tracking
7. **Analytics**: Considerar Google Analytics o alternativa
8. **Documentation**: Documentar componentes con Compodoc

### Recursos Adicionales

- **Angular Style Guide**: https://angular.io/guide/styleguide
- **Angular Material**: https://material.angular.io/
- **NgRx**: https://ngrx.io/
- **RxJS**: https://rxjs.dev/
- **Chart.js**: https://www.chartjs.org/

---

## 🎯 RESULTADO ESPERADO

Un proyecto Angular profesional, escalable y mantenible que:

✅ Consume completamente la API del backend Cronos Bakery System
✅ Implementa autenticación JWT con refresh token
✅ Soporta 2FA
✅ Gestiona materias primas, recetas y cotizaciones
✅ Calcula costos automáticamente
✅ Genera y comparte cotizaciones
✅ Exporta PDFs
✅ Gestiona inventario con alertas
✅ Permite personalización completa (branding, plantillas)
✅ Es responsive y accesible
✅ Tiene cobertura de tests >80%
✅ Está containerizado con Docker
✅ Sigue mejores prácticas de Angular
✅ Es fácil de mantener y escalar

---

**Versión**: 1.0.0
**Fecha**: Noviembre 2024
**Autor**: Especificaciones generadas para Cronos Bakery System Frontend
