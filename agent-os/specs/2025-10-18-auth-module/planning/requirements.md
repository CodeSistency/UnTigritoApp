# Requisitos del Módulo de Autenticación - UnTigritoApp

## Contexto del Producto
- **Producto**: UnTigritoApp - Plataforma que conecta clientes con profesionales calificados
- **Arquitectura**: Clean Architecture + MVVM con Jetpack Compose
- **Tech Stack**: Kotlin, Hilt, Coroutines, Room, Navigation Compose
- **API Base**: https://api.untigrito.com (ejemplo)

## Flujo de Navegación

### Flujo Principal
```
Splash → Login
```

### Desde Login (3 opciones)
- **Opción A**: Login exitoso → Home
- **Opción B**: Registro → Verificación (opcional)
- **Opción C**: Recuperar Contraseña → Login

### Flujo de Registro
```
Registro → Autenticado automáticamente → Introducción a Verificación
```

### Flujo de Verificación (Opcional)
```
Introducción a Verificación → [Omitir] → Home
Introducción a Verificación → [Continuar] → Registrar Cédula
Registrar Cédula → Registrar Teléfono
Registrar Teléfono → Validar Teléfono → Home
```

## Pantallas Requeridas

### 1. Splash Screen
- **Propósito**: Pantalla de bienvenida/carga inicial
- **Contenido**: Logo UnTigrito® centrado
- **Navegación**: Automática al Login tras 2-3 segundos

### 2. Login Screen
- **Campos**: Email, Contraseña
- **Acciones**: 
  - Botón "Iniciar Sesión"
  - Enlace "¿Olvidaste tu contraseña?"
  - Botón "Iniciar sesión con Google"
  - Enlace "¿No tienes cuenta? Regístrate"

### 3. Register Screen
- **Campos**: Nombre completo, Email, Contraseña, Confirmar contraseña
- **Acciones**:
  - Botón "Registrarse"
  - Botón "Iniciar sesión con Google"
  - Enlace "¿Ya tienes cuenta? Iniciar Sesión"

### 4. Introducción a Verificación
- **Propósito**: Informar sobre beneficios de verificación
- **Contenido**: Ilustración, lista de requisitos (cédula, teléfono)
- **Acciones**: Botón "Continuar", Enlace "Omitir"

### 5. Verificación de Cédula
- **Campos**: Número de cédula, Upload de imagen de cédula
- **Validaciones**: Formato de imagen, preview antes de enviar
- **Acciones**: Botón "Verificar y continuar"

### 6. Registro de Teléfono
- **Campos**: Número de teléfono (formato venezolano)
- **Acciones**: Botón "Continuar"

### 7. Validación de Teléfono
- **Contenido**: 6 campos para código OTP
- **Acciones**: Botón "Verificar Código", Enlace "Reenviar código"

### 8. Recuperación de Contraseña
- **Campos**: Email o teléfono
- **Acciones**: Botón "Enviar código"
- **Navegación**: Pantalla de confirmación → Login

## Requisitos Técnicos

### Autenticación y Sesiones
- **Refresh automático de tokens**: ✅ Implementar
- **Manejo de expiración**: Redirección automática al Login
- **Logout automático**: En caso de token inválido
- **Persistencia**: Estado de verificación en dispositivo local

### Validaciones
- **Email**: Formato válido, tiempo real
- **Contraseña**: Mínimo 8 caracteres, tiempo real
- **Teléfono**: Formato venezolano (04120386216), tiempo real
- **Cédula**: 7-8 dígitos, tiempo real

### UX/UI
- **Indicadores de carga**: Durante llamadas a API
- **Mensajes de error**: Estandarizados por tipo de fallo
- **Preview de imágenes**: Antes de enviar cédula
- **Validación de formato**: Para imágenes de cédula

### Google Sign-In
- **Configuración**: Desde cero
- **Información adicional**: No requerida por el momento

### Recuperación de Contraseña
- **Métodos**: Email y SMS
- **Pantalla de confirmación**: Después de enviar código
- **Navegación**: Confirmación → Login

## Reglas de Negocio

### Verificación de Identidad
- **Obligatoriedad**: No obligatoria para uso básico
- **Profesionales**: Deben verificarse para ser profesionales
- **Estado**: Persistir en dispositivo local
- **Escaneo facial**: No implementar (solo upload de imagen)

### Flujos de Usuario
- **Registro exitoso**: Autenticación automática
- **Omitir verificación**: Acceso directo al Home
- **Verificación completa**: Acceso a funcionalidades de profesional

## Endpoints de API

### Autenticación
- `POST /api/auth/login` - Login con email/contraseña
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/google` - Login con Google
- `POST /api/auth/forgot-password` - Recuperar contraseña
- `POST /api/auth/reset-password` - Resetear contraseña
- `POST /api/auth/logout` - Cerrar sesión
- `POST /api/auth/refresh` - Refresh token

### Verificación
- `POST /api/user/send-otp` - Enviar código OTP
- `POST /api/user/verify-otp` - Verificar código OTP
- `POST /api/user/verify-id` - Verificar identidad con cédula
- `POST /api/user/verify-phone` - Verificar teléfono

## Estilos y Diseño

### Referencias Visuales
- **Colores**: 
  - Primario: #E67822 (naranja)
  - Secundario: #2196F3 (azul)
  - Texto: Negro/Gris
- **Componentes**: Basados en LoginScreen.kt y RegisterScreen.kt existentes
- **Material Design 3**: Seguir guías de Material Design

### Componentes Reutilizables
- **OutlinedTextField**: Para campos de entrada
- **Button**: Botones principales (naranja)
- **OutlinedButton**: Botones secundarios
- **TextButton**: Enlaces de navegación
- **IconButton**: Botones con iconos

## Consideraciones de Seguridad
- **Almacenamiento seguro**: EncryptedSharedPreferences para tokens
- **Validación**: Tanto en cliente como servidor
- **TLS**: Comunicación segura con API
- **PII**: Minimizar recolección de datos personales

## Testing
- **Unit Tests**: Para lógica de negocio
- **UI Tests**: Para flujos de navegación
- **Integration Tests**: Para llamadas a API
- **Mocking**: Para servicios externos (Google Sign-In)

## Configuración de Entornos
- **Desarrollo**: https://api-dev.untigrito.com
- **Producción**: https://api.untigrito.com
- **Feature Flags**: Para funcionalidades experimentales
