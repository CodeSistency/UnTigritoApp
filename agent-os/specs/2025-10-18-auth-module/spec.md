# Specification: Módulo de Autenticación UnTigritoApp

## Goal
Implementar un módulo de autenticación completo para UnTigritoApp que permita a usuarios registrarse, iniciar sesión, recuperar contraseñas y verificar su identidad de forma opcional, siguiendo Clean Architecture y Material Design 3.

## User Stories
- Como usuario, quiero registrarme con email y contraseña para acceder a la plataforma
- Como usuario, quiero iniciar sesión con mis credenciales para acceder a mi cuenta
- Como usuario, quiero iniciar sesión con Google para un acceso más rápido
- Como usuario, quiero recuperar mi contraseña si la olvido
- Como usuario, quiero verificar mi identidad opcionalmente para acceder a funcionalidades de profesional
- Como profesional, debo verificar mi identidad para poder ofrecer servicios

## Core Requirements

### Functional Requirements
- **Splash Screen**: Pantalla de bienvenida con logo y navegación automática
- **Login**: Autenticación con email/contraseña y Google Sign-In
- **Registro**: Creación de cuenta con validaciones en tiempo real
- **Recuperación de Contraseña**: Envío de códigos por email y SMS
- **Verificación de Identidad**: Upload de cédula y verificación de teléfono (opcional)
- **Gestión de Sesiones**: Refresh automático de tokens y logout automático
- **Navegación**: Flujo completo desde Splash hasta Home con todas las ramas

### Non-Functional Requirements
- **Performance**: Respuesta de UI en <100ms, llamadas API <2s
- **Seguridad**: Almacenamiento seguro de tokens, validación cliente-servidor
- **UX**: Indicadores de carga, mensajes de error estandarizados, validaciones en tiempo real
- **Accesibilidad**: Soporte para TalkBack, contraste adecuado, tamaños de texto accesibles

## Visual Design
- **Referencias**: Basado en LoginScreen.kt y RegisterScreen.kt existentes
- **Colores**: Primario #E67822 (naranja), Secundario #2196F3 (azul)
- **Componentes**: Material Design 3 con OutlinedTextField, Button, TextButton
- **Layout**: Column con padding 24dp, elementos centrados horizontalmente
- **Navegación**: TopAppBar con botón de retroceso donde corresponda

## Reusable Components

### Existing Code to Leverage
- **AppButton**: Componente reutilizable para botones principales
- **AuthNavGraph**: Estructura de navegación existente con rutas definidas
- **AuthState**: Modelo de estados de autenticación (Loading, Authenticated, Error)
- **User**: Modelo de usuario con campos de verificación
- **AuthRepository**: Interfaz y implementación de repositorio de autenticación
- **LoginScreen/RegisterScreen**: Patrones de UI y estilos establecidos
- **OutlinedTextField**: Componentes de entrada con validación de contraseña
- **Material Design 3**: Scaffold, Button, TextButton, IconButton

### New Components Required
- **SplashScreen**: Pantalla de bienvenida con logo y delay automático
- **ForgotPasswordScreen**: Formulario de recuperación de contraseña
- **VerificationIntroductionScreen**: Introducción a verificación con ilustración
- **CedulaVerificationScreen**: Upload de cédula con preview de imagen
- **PhoneRegistrationScreen**: Registro de número de teléfono
- **PhoneValidationScreen**: Validación de código OTP con 6 campos
- **GoogleSignInButton**: Botón personalizado para autenticación con Google
- **LoadingIndicator**: Indicador de carga reutilizable
- **ErrorSnackbar**: Componente para mostrar errores estandarizados

## Technical Approach

### Database
- **User Entity**: Extender modelo existente con campos de verificación
- **Auth Tokens**: Almacenamiento seguro con EncryptedSharedPreferences
- **Verification State**: Persistencia local del estado de verificación

### API
- **Endpoints**: 7 endpoints de autenticación + 4 de verificación
- **Data Flow**: Repository → UseCase → ViewModel → UI
- **Error Handling**: Result<T> sealed classes para manejo de errores
- **Token Management**: Refresh automático y logout automático

### Frontend
- **Architecture**: Clean Architecture + MVVM con Jetpack Compose
- **Navigation**: NavGraph con rutas definidas en AuthRoutes
- **State Management**: StateFlow/SharedFlow en ViewModels
- **Dependency Injection**: Hilt con módulos específicos
- **UI Components**: 8 pantallas con navegación fluida

### Testing
- **Unit Tests**: ViewModels, UseCases, Repositories
- **UI Tests**: Flujos de navegación y interacciones
- **Integration Tests**: Llamadas a API y persistencia
- **Mocking**: Google Sign-In y servicios externos

## Out of Scope
- **Escaneo facial**: Solo upload de imagen de cédula
- **Verificación biométrica**: No implementar en esta fase
- **Autenticación de dos factores**: Solo OTP por SMS/email
- **Integración con redes sociales**: Solo Google Sign-In
- **Gestión de roles avanzada**: Solo CLIENT/PROFESSIONAL básico

## Success Criteria
- **Flujo completo**: Usuario puede registrarse, verificar identidad y acceder a Home
- **Navegación fluida**: Transiciones suaves entre todas las pantallas
- **Validaciones**: Errores claros y validaciones en tiempo real
- **Seguridad**: Tokens seguros y manejo adecuado de sesiones
- **UX**: Indicadores de carga y mensajes de error consistentes
- **Testing**: Cobertura >80% en lógica de negocio y flujos críticos
