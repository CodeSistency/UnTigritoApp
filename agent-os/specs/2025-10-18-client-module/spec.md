# Specification: Módulo de Cliente

## Goal
Implementar un módulo completo de cliente que permita a los usuarios solicitar servicios profesionales, gestionar solicitudes, explorar profesionales y administrar su cuenta, siguiendo la arquitectura Clean Architecture + MVVM establecida.

## User Stories
- Como cliente, quiero ver mi saldo disponible y opciones de recarga/retiro para gestionar mis fondos
- Como cliente, quiero explorar categorías de servicios para encontrar lo que necesito rápidamente
- Como cliente, quiero ver profesionales mejor calificados para tomar decisiones informadas
- Como cliente, quiero publicar solicitudes de servicio con detalles específicos para obtener ofertas
- Como cliente, quiero buscar servicios y profesionales por categoría y ubicación
- Como cliente, quiero ver detalles de servicios y negociar precios antes de contratar
- Como cliente, quiero gestionar mis solicitudes (abiertas, en curso, historial) para hacer seguimiento
- Como cliente, quiero administrar mi perfil y verificar mi cuenta para mayor seguridad
- Como cliente, quiero cambiar entre rol de cliente y profesional según mis necesidades

## Core Requirements

### Functional Requirements
- **Pantalla Home**: Saldo, categorías, profesionales destacados, botón de solicitud, servicios cercanos
- **Navegación**: BottomNavigationBar con 4 pestañas (Inicio, Servicios, Solicitudes, Perfil)
- **Búsqueda y Filtros**: Buscador de servicios/profesionales con filtros por categoría
- **Crear Solicitud**: Formulario completo con título, descripción, categoría, ubicación, urgencia, fecha/hora, adjuntos, rango de precios
- **Detalle de Servicio**: Información completa, negociación de precios, ofertas recibidas
- **Gestión de Solicitudes**: Estados (Abiertas, En Curso, Historial) con acciones específicas
- **Perfil de Usuario**: Datos personales, verificación, cambio de rol, historial de servicios
- **Flujo de Pagos**: Información bancaria, métodos de recarga/retiro, confirmación manual

### Non-Functional Requirements
- **Performance**: Carga rápida de listas con paginación y cache local
- **Usabilidad**: Navegación intuitiva con Material Design 3
- **Seguridad**: Datos sensibles encriptados, autenticación requerida
- **Offline**: Cache local para funcionalidad básica sin conexión

## Visual Design
- **Estilo Base**: Seguir patrones visuales del HomeScreen.kt existente
- **Colores**: Primario #E67822 (naranja), fondo #F0F0F0, cards blancas
- **Componentes**: Material Design 3 con bordes redondeados (12dp)
- **Layout**: Column con padding 24dp, elementos centrados horizontalmente
- **Navegación**: BottomNavigationBar con iconos y colores consistentes

## Reusable Components

### Existing Code to Leverage
- **HomeScreen.kt**: Patrones de layout, colores, BottomNavigationBar, cards con bordes redondeados
- **AppNavigation.kt**: Estructura de navegación con NavHost y composables
- **AuthRepository**: Patrón de repositorio para autenticación y datos de usuario
- **UserApiService**: Servicios de API con Retrofit para operaciones de usuario
- **LoadingIndicator**: Componente de carga reutilizable
- **ErrorSnackbar**: Componente de errores estandarizado
- **Material Design 3**: Scaffold, Button, TextButton, NavigationBar, Cards

### New Components Required
- **ClientHomeScreen**: Pantalla principal con saldo, categorías y servicios
- **ServicesScreen**: Búsqueda y listado de servicios/profesionales
- **CreateRequestScreen**: Formulario de creación de solicitudes
- **ServiceDetailScreen**: Detalle de servicio con negociación
- **RequestsScreen**: Gestión de solicitudes por estado
- **ClientProfileScreen**: Perfil del cliente con verificación
- **PaymentScreen**: Flujo de pagos y recarga
- **CategoryCarousel**: Carrusel de categorías reutilizable
- **ProfessionalCard**: Tarjeta de profesional con rating
- **ServiceCard**: Tarjeta de servicio con detalles
- **RequestCard**: Tarjeta de solicitud con estado
- **BalanceCard**: Tarjeta de saldo con acciones

## Technical Approach

### Database
- **User Entity**: Extender modelo existente con campos de cliente
- **ServicePosting Entity**: Publicaciones de servicios con categorías
- **Professional Entity**: Perfiles de profesionales con especialidades
- **Request Entity**: Solicitudes con estados y ofertas
- **Transaction Entity**: Historial de pagos y transacciones

### API
- **Endpoints**: 15+ endpoints de la API documentada (auth, profesionales, servicios, usuarios)
- **Data Flow**: Repository → UseCase → ViewModel → UI
- **Error Handling**: Result<T> sealed classes para manejo de errores
- **Authentication**: JWT tokens con refresh automático

### Frontend
- **Architecture**: Clean Architecture + MVVM con módulo feature-client
- **Navigation**: Jetpack Navigation Compose con BottomNavigationBar
- **State Management**: ViewModel + StateFlow/Flow para estado reactivo
- **Networking**: Ktor Client o Retrofit para comunicación con API
- **Images**: Coil para carga y cache de imágenes
- **Persistence**: Room para cache local + EncryptedSharedPreferences para datos sensibles

### Testing
- **Unit Tests**: ViewModels, UseCases, Repositories
- **UI Tests**: Navegación y flujos principales
- **Integration Tests**: API calls y persistencia local

## Out of Scope
- **Integración real de pagos**: Solo información bancaria y confirmación manual
- **Funcionalidades de profesional**: Módulo separado para profesionales
- **Notificaciones push**: Para fases posteriores
- **Chat en tiempo real**: Comunicación básica inicial

## Success Criteria
- **Navegación fluida**: Transiciones suaves entre 4 pestañas principales
- **Carga rápida**: Listas cargan en <2 segundos con cache local
- **UX consistente**: Diseño coherente con HomeScreen.kt existente
- **Funcionalidad completa**: Todas las pantallas del flujo de cliente implementadas
- **Integración API**: Comunicación exitosa con todos los endpoints necesarios
