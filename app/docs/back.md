BACKEND

# API Documentation - UnTigrito

Esta documentación describe todos los endpoints disponibles en la API de UnTigrito, una plataforma que conecta clientes con profesionales calificados.

## Tabla de Contenidos

- [Autenticación](#autenticación)
- [Profesionales](#profesionales)
- [Usuarios](#usuarios)
- [Servicios](#servicios)
- [Verificación](#verificación)
- [Schemas](#schemas)

## Autenticación

### POST /api/auth/login

**Descripción**: Autentica a un usuario con email/teléfono y contraseña.

**Parámetros del Body**:
```json
{
  "email": "string? (opcional)",
  "phone": "string? (opcional)",
  "password": "string (requerido)"
}
```

**Nota**: Debe proporcionar email O teléfono, pero no ambos.

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "string",
      "email": "string|null",
      "phone": "string|null",
      "name": "string|null",
      "role": "CLIENT|PROFESSIONAL|ADMIN",
      "isVerified": "boolean",
      "isIDVerified": "boolean",
      "balance": "number",
      "isSuspended": "boolean",
      "createdAt": "string",
      "updatedAt": "string",
      "locationLat": "number|null",
      "locationLng": "number|null",
      "locationAddress": "string|null"
    },
    "token": "string",
    "refreshToken": "string"
  }
}
```

**Códigos de Error**:
- `400`: Datos de validación inválidos
- `401`: Credenciales inválidas
- `403`: Cuenta suspendida
- `500`: Error interno del servidor

**Schema**: `LoginRequestSchema`, `AuthResponseSchema`

---

### POST /api/auth/register

**Descripción**: Registra una nueva cuenta de usuario.

**Parámetros del Body**:
```json
{
  "email": "string? (opcional)",
  "phone": "string? (opcional)",
  "password": "string (mínimo 8 caracteres)",
  "name": "string (mínimo 2 caracteres)",
  "role": "CLIENT|PROFESSIONAL|ADMIN (default: CLIENT)"
}
```

**Nota**: Debe proporcionar email O teléfono, pero no ambos.

**Respuesta Exitosa (201)**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": "string",
      "email": "string|null",
      "phone": "string|null",
      "name": "string|null",
      "role": "CLIENT|PROFESSIONAL|ADMIN",
      "isVerified": "boolean",
      "isIDVerified": "boolean",
      "balance": "number",
      "isSuspended": "boolean",
      "createdAt": "string",
      "updatedAt": "string",
      "locationLat": "number|null",
      "locationLng": "number|null",
      "locationAddress": "string|null"
    },
    "token": "string",
    "refreshToken": "string"
  }
}
```

**Códigos de Error**:
- `400`: Datos de validación inválidos
- `409`: Email o teléfono ya existe
- `500`: Error interno del servidor

**Schema**: `RegisterRequestSchema`, `AuthResponseSchema`

---

### POST /api/auth/logout

**Descripción**: Cierra la sesión del usuario actual.

**Autenticación**: Bearer Token requerido

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": {
    "message": "string"
  }
}
```

---

### POST /api/auth/refresh

**Descripción**: Refresca el token de acceso usando un refresh token.

**Parámetros del Body**:
```json
{
  "token": "string (refresh token)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "string"
  }
}
```

**Schema**: `RefreshTokenRequestSchema`, `RefreshTokenResponseSchema`

---

### POST /api/auth/forgot-password

**Descripción**: Envía un email para restablecer la contraseña.

**Parámetros del Body**:
```json
{
  "email": "string (email válido)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Password reset email sent",
  "data": {
    "message": "string"
  }
}
```

**Schema**: `ForgotPasswordRequestSchema`, `MessageResponseSchema`

---

### POST /api/auth/reset-password

**Descripción**: Restablece la contraseña usando un token.

**Parámetros del Body**:
```json
{
  "token": "string",
  "newPassword": "string (mínimo 8 caracteres)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": {
    "message": "string"
  }
}
```

**Schema**: `ResetPasswordRequestSchema`, `MessageResponseSchema`

---

### POST /api/auth/verify-email

**Descripción**: Verifica el email del usuario usando un token.

**Parámetros del Body**:
```json
{
  "token": "string"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": {
    "message": "string"
  }
}
```

**Schema**: `EmailVerificationRequestSchema`, `MessageResponseSchema`

---

### POST /api/auth/google

**Descripción**: Autentica usando Google OAuth.

**Parámetros del Body**:
```json
{
  "token": "string (Google token)",
  "idToken": "string? (opcional)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Google authentication successful",
  "data": {
    "user": { /* User object */ },
    "token": "string",
    "refreshToken": "string"
  }
}
```

**Schema**: `GoogleAuthRequestSchema`, `AuthResponseSchema`

## Profesionales

### GET /api/professionals/list

**Descripción**: Obtiene una lista paginada de profesionales con filtros opcionales.

**Parámetros de Query**:
```json
{
  "page": "number (default: 1)",
  "limit": "number (default: 10, max: 100)",
  "specialty": "string? (opcional)",
  "minRating": "number? (0-5)",
  "maxHourlyRate": "number? (opcional)",
  "minExperience": "number? (opcional)",
  "isVerified": "boolean? (opcional)",
  "search": "string? (opcional)",
  "locationLat": "number? (opcional)",
  "locationLng": "number? (opcional)",
  "radius": "number (default: 10)",
  "sortBy": "rating|experience|hourlyRate|recent",
  "sortDirection": "asc|desc (default: desc)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Professionals retrieved successfully",
  "data": {
    "professionals": [
      {
        "id": "string",
        "userId": "string",
        "bio": "string|null",
        "rating": "number|null",
        "totalReviews": "number|null",
        "yearsOfExperience": "number|null",
        "certifications": "string|null",
        "specialties": "string[]",
        "responseTime": "number|null",
        "completionRate": "number|null",
        "hourlyRate": "number|null",
        "bankAccount": "string|null",
        "taxId": "string|null",
        "isVerified": "boolean",
        "createdAt": "string",
        "updatedAt": "string",
        "user": { /* User object */ }
      }
    ],
    "total": "number",
    "page": "number",
    "limit": "number",
    "totalPages": "number"
  }
}
```

**Schema**: `GetProfessionalsQuerySchema`, `ProfessionalsListResponseSchema`

---

### GET /api/professionals/[id]

**Descripción**: Obtiene el perfil de un profesional específico por ID.

**Parámetros de Ruta**:
- `id`: string (ID del profesional)

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Professional retrieved successfully",
  "data": {
    "professional": {
      "id": "string",
      "userId": "string",
      "bio": "string|null",
      "rating": "number|null",
      "totalReviews": "number|null",
      "yearsOfExperience": "number|null",
      "certifications": "string|null",
      "specialties": "string[]",
      "responseTime": "number|null",
      "completionRate": "number|null",
      "hourlyRate": "number|null",
      "bankAccount": "string|null",
      "taxId": "string|null",
      "isVerified": "boolean",
      "createdAt": "string",
      "updatedAt": "string",
      "user": { /* User object */ }
    }
  }
}
```

**Códigos de Error**:
- `404`: Profesional no encontrado
- `422`: ID inválido

**Schema**: `ProfessionalProfileResponseSchema`

---

### GET /api/professionals/[id]/stats

**Descripción**: Obtiene las estadísticas de un profesional.

**Parámetros de Ruta**:
- `id`: string (ID del profesional)

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Professional stats retrieved successfully",
  "data": {
    "stats": {
      "totalClients": "number",
      "totalCompletedServices": "number",
      "totalEarnings": "number",
      "averageRating": "number",
      "totalReviews": "number",
      "successRate": "number"
    }
  }
}
```

**Schema**: `ProfessionalStatsResponseSchema`

---

### POST /api/professionals/profile/create

**Descripción**: Crea un perfil de profesional para el usuario autenticado.

**Autenticación**: Bearer Token requerido

**Parámetros del Body**:
```json
{
  "bio": "string? (máximo 1000 caracteres)",
  "yearsOfExperience": "number? (0-70)",
  "certifications": "string? (opcional)",
  "specialties": "string[]? (opcional)",
  "hourlyRate": "number? (opcional)",
  "bankAccount": "string? (opcional)",
  "taxId": "string? (opcional)"
}
```

**Schema**: `CreateProfessionalProfileRequestSchema`

---

### GET /api/professionals/profile

**Descripción**: Obtiene el perfil del profesional del usuario autenticado.

**Autenticación**: Bearer Token requerido

---

### PUT /api/professionals/profile/update

**Descripción**: Actualiza el perfil del profesional del usuario autenticado.

**Autenticación**: Bearer Token requerido

**Parámetros del Body**:
```json
{
  "bio": "string? (máximo 1000 caracteres)",
  "yearsOfExperience": "number? (0-70)",
  "certifications": "string? (opcional)",
  "specialties": "string[]? (opcional)",
  "hourlyRate": "number? (opcional)",
  "bankAccount": "string? (opcional)"
}
```

**Schema**: `UpdateProfessionalProfileRequestSchema`

---

### GET /api/professionals/search

**Descripción**: Busca profesionales por texto.

**Parámetros de Query**:
- `q`: string (término de búsqueda)

## Usuarios

### GET /api/users/list

**Descripción**: Obtiene una lista paginada de usuarios con filtros opcionales.

**Autenticación**: Bearer Token requerido

**Parámetros de Query**:
```json
{
  "page": "number (default: 1)",
  "limit": "number (default: 10, max: 100)",
  "role": "CLIENT|PROFESSIONAL|ADMIN? (opcional)",
  "isVerified": "boolean? (opcional)",
  "search": "string? (opcional)",
  "locationLat": "number? (opcional)",
  "locationLng": "number? (opcional)",
  "radius": "number (default: 10)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "users": [
      {
        "id": "string",
        "email": "string|null",
        "phone": "string|null",
        "name": "string|null",
        "role": "CLIENT|PROFESSIONAL|ADMIN",
        "isVerified": "boolean",
        "isIDVerified": "boolean",
        "balance": "number",
        "isSuspended": "boolean",
        "createdAt": "string",
        "updatedAt": "string",
        "deletedAt": "string|null",
        "locationLat": "number|null",
        "locationLng": "number|null",
        "locationAddress": "string|null"
      }
    ],
    "total": "number",
    "page": "number",
    "limit": "number",
    "totalPages": "number"
  }
}
```

**Schema**: `GetUsersQuerySchema`, `UsersListResponseSchema`

---

### GET /api/users/[id]

**Descripción**: Obtiene la información de un usuario específico por ID.

**Parámetros de Ruta**:
- `id`: string (ID del usuario)

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "user": { /* User with relations object */ }
  }
}
```

**Schema**: `UserProfileResponseSchema`

---

### GET /api/users/[id]/stats

**Descripción**: Obtiene las estadísticas de un usuario.

**Parámetros de Ruta**:
- `id`: string (ID del usuario)

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "User stats retrieved successfully",
  "data": {
    "stats": {
      "totalPostings": "number",
      "totalOffers": "number",
      "totalTransactions": "number",
      "totalReviews": "number",
      "averageRating": "number"
    }
  }
}
```

**Schema**: `UserStatsResponseSchema`

---

### GET /api/users/profile

**Descripción**: Obtiene el perfil del usuario autenticado.

**Autenticación**: Bearer Token requerido

---

### PUT /api/users/profile/update

**Descripción**: Actualiza el perfil del usuario autenticado.

**Autenticación**: Bearer Token requerido

**Parámetros del Body**:
```json
{
  "name": "string? (mínimo 2 caracteres)",
  "phone": "string? (opcional)",
  "locationLat": "number? (opcional)",
  "locationLng": "number? (opcional)",
  "locationAddress": "string? (opcional)"
}
```

**Schema**: `UpdateUserProfileRequestSchema`

## Servicios

### GET /api/services/postings/list

**Descripción**: Obtiene una lista paginada de publicaciones de servicios.

**Parámetros de Query**:
```json
{
  "page": "number (default: 1)",
  "limit": "number (default: 10, max: 100)",
  "status": "OPEN|IN_PROGRESS|COMPLETED|CANCELLED? (opcional)",
  "category": "string? (opcional)",
  "minBudget": "number? (opcional)",
  "maxBudget": "number? (opcional)",
  "search": "string? (opcional)",
  "locationLat": "number? (opcional)",
  "locationLng": "number? (opcional)",
  "radius": "number (default: 10)",
  "sortBy": "budget|recent|deadline",
  "sortDirection": "asc|desc (default: desc)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Service postings retrieved successfully",
  "data": {
    "postings": [
      {
        "id": "string",
        "clientId": "string",
        "title": "string",
        "description": "string",
        "category": "string",
        "budget": "number",
        "deadline": "string|null",
        "status": "OPEN|IN_PROGRESS|COMPLETED|CANCELLED",
        "location": "string|null",
        "locationLat": "number|null",
        "locationLng": "number|null",
        "createdAt": "string",
        "updatedAt": "string",
        "deletedAt": "string|null"
      }
    ],
    "total": "number",
    "page": "number",
    "limit": "number",
    "totalPages": "number"
  }
}
```

**Schema**: `GetServicePostingsQuerySchema`, `ServicePostingsListResponseSchema`

---

### POST /api/services/postings/create

**Descripción**: Crea una nueva publicación de servicio.

**Autenticación**: Bearer Token requerido

**Parámetros del Body**:
```json
{
  "title": "string (5-200 caracteres)",
  "description": "string (10-5000 caracteres)",
  "category": "string",
  "budget": "number",
  "deadline": "string? (ISO datetime)",
  "location": "string? (opcional)",
  "locationLat": "number? (opcional)",
  "locationLng": "number? (opcional)"
}
```

**Schema**: `CreateServicePostingRequestSchema`

---

### POST /api/services/offers/create

**Descripción**: Crea una oferta para una publicación de servicio.

**Autenticación**: Bearer Token requerido (solo profesionales)

**Parámetros del Body**:
```json
{
  "postingId": "string",
  "proposedPrice": "number",
  "description": "string (10-2000 caracteres)",
  "estimatedDuration": "number (mínimo 1)"
}
```

**Schema**: `CreateServiceOfferRequestSchema`

## Verificación

### POST /api/user/send-otp

**Descripción**: Envía un código OTP al número de teléfono.

**Parámetros del Body**:
```json
{
  "phoneNumber": "string (formato: 04120386216)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "message": "string",
    "expiresIn": "number"
  }
}
```

**Códigos de Error**:
- `400`: Número de teléfono inválido
- `429`: Demasiadas solicitudes

**Schema**: `OTPSendRequestSchema`, `OTPSendResponseSchema`

---

### POST /api/user/verify-otp

**Descripción**: Verifica un código OTP.

**Parámetros del Body**:
```json
{
  "phoneNumber": "string (formato: 04120386216)",
  "otpCode": "string (5 dígitos)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "message": "string",
    "verified": "boolean"
  }
}
```

**Schema**: `OTPVerifyRequestSchema`, `OTPVerifyResponseSchema`

---

### POST /api/user/verify-id

**Descripción**: Verifica la identidad usando cédula y escaneo facial.

**Autenticación**: Bearer Token requerido

**Parámetros del Body**:
```json
{
  "cedula": "string (7-8 dígitos)",
  "cedulaImage": "string (base64 image)",
  "faceScanData": "string (face scan data)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "ID verification completed",
  "data": {
    "message": "string",
    "verified": "boolean",
    "verificationId": "string?"
  }
}
```

**Schema**: `IDVerificationRequestSchema`, `IDVerificationResponseSchema`

---

### POST /api/user/verify-phone

**Descripción**: Inicia la verificación de teléfono.

**Parámetros del Body**:
```json
{
  "phoneNumber": "string (formato: 04120386216)"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "success": true,
  "message": "Phone verification initiated",
  "data": {
    "message": "string",
    "verified": "boolean"
  }
}
```

**Schema**: `PhoneVerificationRequestSchema`, `PhoneVerificationResponseSchema`

## Schemas

### Tipos Comunes

```typescript
// Role Schema
type Role = 'CLIENT' | 'PROFESSIONAL' | 'ADMIN'

// Common Response Format
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
}
```

### Schemas de Autenticación

```typescript
// User Schema
interface User {
  id: string;
  email: string | null;
  phone: string | null;
  name: string | null;
  role: Role;
  isVerified: boolean;
  isIDVerified: boolean;
  balance: number;
  isSuspended: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
  locationLat: number | null;
  locationLng: number | null;
  locationAddress: string | null;
}

// Auth Response
interface AuthResponse {
  user: User;
  token: string;
  refreshToken?: string;
}
```

### Schemas de Profesionales

```typescript
// Professional Profile
interface ProfessionalProfile {
  id: string;
  userId: string;
  bio: string | null;
  rating: number | null;
  totalReviews: number | null;
  yearsOfExperience: number | null;
  certifications: string | null;
  specialties: string[];
  responseTime: number | null;
  completionRate: number | null;
  hourlyRate: number | null;
  bankAccount: string | null;
  taxId: string | null;
  isVerified: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
}
```

### Schemas de Servicios

```typescript
// Service Posting
interface ServicePosting {
  id: string;
  clientId: string;
  title: string;
  description: string;
  category: string;
  budget: number;
  deadline: string | null;
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  location: string | null;
  locationLat: number | null;
  locationLng: number | null;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
}

// Service Offer
interface ServiceOffer {
  id: string;
  postingId: string;
  professionalId: string;
  proposedPrice: number;
  description: string;
  estimatedDuration: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}
```

### Schemas de Verificación

```typescript
// OTP Request
interface OTPSendRequest {
  phoneNumber: string; // Formato: 04120386216
}

// OTP Response
interface OTPSendResponse {
  message: string;
  expiresIn: number;
}

// ID Verification Request
interface IDVerificationRequest {
  cedula: string; // 7-8 dígitos
  cedulaImage: string; // Base64 image
  faceScanData: string; // Face scan data
}
```

## Notas Generales

### Autenticación
- La mayoría de los endpoints requieren autenticación Bearer Token
- Los tokens se obtienen mediante `/api/auth/login` o `/api/auth/register`
- Los refresh tokens se usan en `/api/auth/refresh` para obtener nuevos access tokens

### Paginación
- Los endpoints de lista soportan paginación con `page` y `limit`
- `page` inicia en 1
- `limit` máximo es 100
- Las respuestas incluyen metadata de paginación

### Validación
- Todos los requests se validan usando Zod schemas
- Las respuestas de error incluyen detalles de validación
- Los campos opcionales están marcados con `?`

### Códigos de Estado HTTP
- `200`: Éxito
- `201`: Recurso creado
- `400`: Datos inválidos
- `401`: No autenticado
- `403`: Prohibido
- `404`: Recurso no encontrado
- `409`: Conflicto (ej: email ya existe)
- `422`: Validación fallida
- `429`: Demasiadas solicitudes
- `500`: Error interno del servidor

### Formatos de Datos
- Fechas: ISO 8601 strings
- Imágenes: Base64 encoded strings con prefijo `data:image/`
- Teléfonos: Formato venezolano `04120386216`
- Cédulas: 7-8 dígitos
- OTP: 5 dígitos

Esta documentación se basa en los schemas y endpoints implementados en el proyecto UnTigrito.

