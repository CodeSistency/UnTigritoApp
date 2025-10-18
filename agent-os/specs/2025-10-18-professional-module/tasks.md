# Lista de Tareas: Módulo de Profesional

## Fase 1: Configuración y Estructura Base (Semana 1)

### 1.1 Configuración del Proyecto
- [ ] **TASK-001**: Crear estructura de paquetes para el módulo profesional
  - Crear `com.thecodefather.untigrito.presentation.screens.professional`
  - Crear `com.thecodefather.untigrito.domain.model.professional`
  - Crear `com.thecodefather.untigrito.data.repository.professional`
  - Crear `com.thecodefather.untigrito.di.professional`

- [ ] **TASK-002**: Configurar navegación para el módulo profesional
  - Definir rutas en `ProfessionalRoutes`
  - Crear `ProfessionalNavigationGraph`
  - Integrar con navegación principal de la app
  - Configurar Bottom Navigation Bar

- [ ] **TASK-003**: Configurar inyección de dependencias
  - Crear `ProfessionalModule` para Hilt
  - Configurar repositorios y casos de uso
  - Configurar servicios de API
  - Configurar base de datos local

### 1.2 Modelos de Datos Base
- [ ] **TASK-004**: Crear entidades del dominio
  - `Job` - Entidad de trabajo
  - `Proposal` - Entidad de propuesta
  - `Service` - Entidad de servicio
  - `Message` - Entidad de mensaje
  - `Conversation` - Entidad de conversación

- [ ] **TASK-005**: Crear enums y estados
  - `JobStatus` - Estados de trabajo
  - `ProposalStatus` - Estados de propuesta
  - `MessageType` - Tipos de mensaje
  - `NotificationType` - Tipos de notificación

- [ ] **TASK-006**: Crear modelos de respuesta de API
  - `JobResponse` - Respuesta de trabajo
  - `ProposalResponse` - Respuesta de propuesta
  - `ServiceResponse` - Respuesta de servicio
  - `MessageResponse` - Respuesta de mensaje

### 1.3 Servicios de API
- [ ] **TASK-007**: Crear interfaces de servicios API
  - `ProfessionalJobsApiService`
  - `ProfessionalProposalsApiService`
  - `ProfessionalMessagesApiService`
  - `ProfessionalServicesApiService`

- [ ] **TASK-008**: Implementar mappers de datos
  - `JobMapper` - Mapeo entre JobResponse y Job
  - `ProposalMapper` - Mapeo entre ProposalResponse y Proposal
  - `ServiceMapper` - Mapeo entre ServiceResponse y Service
  - `MessageMapper` - Mapeo entre MessageResponse y Message

## Fase 2: Subflujo Trabajos (Semana 2)

### 2.1 Pantalla de Listado de Trabajos
- [ ] **TASK-009**: Crear `ProfessionalJobsScreen`
  - Implementar layout principal con Scaffold
  - Agregar TopAppBar con búsqueda
  - Implementar tabs (Recientes, Recomendados, Favoritos)
  - Agregar filtros por categoría y ubicación

- [ ] **TASK-010**: Crear `JobCard` component
  - Diseñar tarjeta con imagen, título, descripción
  - Agregar información del cliente y calificación
  - Mostrar ubicación y fecha
  - Agregar botón de favorito y precio
  - Implementar tags de requisitos

- [ ] **TASK-011**: Crear `ProfessionalJobsViewModel`
  - Implementar estado de UI (`ProfessionalJobsUiState`)
  - Agregar funciones de búsqueda y filtrado
  - Implementar paginación con lazy loading
  - Manejar estados de carga y error

- [ ] **TASK-012**: Implementar casos de uso
  - `GetJobsUseCase` - Obtener lista de trabajos
  - `SearchJobsUseCase` - Buscar trabajos por texto
  - `FilterJobsUseCase` - Filtrar trabajos por criterios
  - `ToggleFavoriteUseCase` - Marcar/desmarcar favorito

### 2.2 Pantalla de Detalle de Trabajo
- [ ] **TASK-013**: Crear `JobDetailScreen`
  - Mostrar imagen del trabajo
  - Mostrar título y descripción completa
  - Mostrar requisitos del cliente
  - Mostrar ubicación con mapa
  - Mostrar información del cliente

- [ ] **TASK-014**: Crear `JobDetailViewModel`
  - Implementar estado de UI (`JobDetailUiState`)
  - Cargar detalles del trabajo
  - Manejar estados de carga y error
  - Implementar navegación a crear propuesta

### 2.3 Pantalla de Crear Propuesta
- [ ] **TASK-015**: Crear `CreateProposalScreen`
  - Campo de monto del presupuesto
  - Campo de descripción de la propuesta
  - Toggles para materiales y garantía
  - Campo de términos y condiciones
  - Botón de enviar propuesta

- [ ] **TASK-016**: Crear `CreateProposalViewModel`
  - Implementar estado de UI (`CreateProposalUiState`)
  - Validar formulario de propuesta
  - Enviar propuesta al servidor
  - Manejar estados de carga y error

- [ ] **TASK-017**: Implementar `CreateProposalUseCase`
  - Validar datos de la propuesta
  - Enviar propuesta a la API
  - Manejar respuestas y errores

## Fase 3: Subflujo Propuestas (Semana 3)

### 3.1 Pantalla de Listado de Propuestas
- [ ] **TASK-018**: Crear `ProposalsScreen`
  - Implementar tabs (Abiertas, En Curso, Historial)
  - Mostrar lista de propuestas
  - Agregar filtros por fecha y estado
  - Implementar búsqueda de propuestas

- [ ] **TASK-019**: Crear `ProposalCard` component
  - Mostrar información del trabajo
  - Mostrar monto y estado de la propuesta
  - Mostrar tags de características (Material, Garantía)
  - Agregar acciones (Editar, Cancelar)

- [ ] **TASK-020**: Crear `ProposalsViewModel`
  - Implementar estado de UI (`ProposalsUiState`)
  - Cargar propuestas por estado
  - Implementar filtros y búsqueda
  - Manejar estados de carga y error

### 3.2 Pantalla de Detalle de Propuesta
- [ ] **TASK-021**: Crear `ProposalDetailScreen`
  - Mostrar detalles completos de la propuesta
  - Mostrar información del trabajo
  - Mostrar historial de cambios
  - Agregar acciones según el estado

- [ ] **TASK-022**: Crear `ProposalDetailViewModel`
  - Implementar estado de UI (`ProposalDetailUiState`)
  - Cargar detalles de la propuesta
  - Implementar acciones (Editar, Cancelar)
  - Manejar estados de carga y error

### 3.3 Gestión de Estados de Propuesta
- [ ] **TASK-023**: Implementar casos de uso de propuestas
  - `GetProposalsUseCase` - Obtener lista de propuestas
  - `UpdateProposalUseCase` - Actualizar propuesta
  - `CancelProposalUseCase` - Cancelar propuesta
  - `GetProposalDetailUseCase` - Obtener detalle de propuesta

## Fase 4: Subflujo Mensajes (Semana 4)

### 4.1 Pantalla de Inbox
- [ ] **TASK-024**: Crear `MessagesScreen`
  - Mostrar chat de soporte
  - Mostrar lista de chats recientes
  - Agregar indicadores de mensajes no leídos
  - Implementar búsqueda de contactos

- [ ] **TASK-025**: Crear `ConversationCard` component
  - Mostrar información del contacto
  - Mostrar último mensaje
  - Mostrar timestamp
  - Mostrar indicador de mensajes no leídos

- [ ] **TASK-026**: Crear `MessagesViewModel`
  - Implementar estado de UI (`MessagesUiState`)
  - Cargar conversaciones
  - Implementar búsqueda de contactos
  - Manejar estados de carga y error

### 4.2 Pantalla de Chat
- [ ] **TASK-027**: Crear `ChatScreen`
  - Mostrar lista de mensajes con burbujas
  - Implementar campo de texto para escribir
  - Agregar botón de envío
  - Mostrar indicador de "escribiendo..."

- [ ] **TASK-028**: Crear `ChatBubble` component
  - Diseñar burbujas de mensaje
  - Diferenciar mensajes enviados/recibidos
  - Mostrar timestamp
  - Soporte para diferentes tipos de mensaje

- [ ] **TASK-029**: Crear `ChatViewModel`
  - Implementar estado de UI (`ChatUiState`)
  - Cargar mensajes de la conversación
  - Enviar mensajes
  - Implementar WebSocket para tiempo real

### 4.3 Sistema de Notificaciones
- [ ] **TASK-030**: Implementar notificaciones push
  - Configurar Firebase Cloud Messaging
  - Crear `NotificationService`
  - Implementar tipos de notificación
  - Configurar preferencias de notificación

- [ ] **TASK-031**: Crear casos de uso de mensajes
  - `GetConversationsUseCase` - Obtener conversaciones
  - `GetMessagesUseCase` - Obtener mensajes
  - `SendMessageUseCase` - Enviar mensaje
  - `MarkAsReadUseCase` - Marcar como leído

## Fase 5: Subflujo Mis Servicios (Semana 5)

### 5.1 Pantalla de Listado de Servicios
- [ ] **TASK-032**: Crear `MyServicesScreen`
  - Implementar tabs (Todos, Activos, Inactivos)
  - Mostrar lista de servicios
  - Agregar FAB para crear servicio
  - Implementar acciones (Editar, Activar/Desactivar)

- [ ] **TASK-033**: Crear `ServiceCard` component
  - Mostrar información del servicio
  - Mostrar estado (Activo/Inactivo)
  - Mostrar estadísticas (Solicitudes, Calificación)
  - Agregar acciones (Editar, Toggle estado)

- [ ] **TASK-034**: Crear `MyServicesViewModel`
  - Implementar estado de UI (`MyServicesUiState`)
  - Cargar servicios del profesional
  - Implementar filtros por estado
  - Manejar estados de carga y error

### 5.2 Pantalla de Crear/Editar Servicio
- [ ] **TASK-035**: Crear `ServiceFormScreen`
  - Formulario de información básica
  - Selector de rango de precios
  - Selector de categoría y especialidades
  - Configuración de zona de servicio
  - Galería de imágenes/videos

- [ ] **TASK-036**: Crear `ServiceFormViewModel`
  - Implementar estado de UI (`ServiceFormUiState`)
  - Validar formulario de servicio
  - Guardar/cancelar cambios
  - Manejar estados de carga y error

- [ ] **TASK-037**: Crear componentes de formulario
  - `PriceRangeSelector` - Selector de rango de precios
  - `CategorySelector` - Selector de categoría
  - `ServiceZoneSelector` - Selector de zona de servicio
  - `MediaGallery` - Galería de multimedia

### 5.3 Gestión de Perfil Profesional
- [ ] **TASK-038**: Crear `ProfessionalProfileScreen`
  - Mostrar información del profesional
  - Mostrar estadísticas
  - Permitir edición de perfil
  - Mostrar calificaciones y reseñas

- [ ] **TASK-039**: Crear `ProfessionalProfileViewModel`
  - Implementar estado de UI (`ProfessionalProfileUiState`)
  - Cargar perfil del profesional
  - Actualizar información del perfil
  - Manejar estados de carga y error

- [ ] **TASK-040**: Implementar casos de uso de servicios
  - `GetServicesUseCase` - Obtener servicios
  - `CreateServiceUseCase` - Crear servicio
  - `UpdateServiceUseCase` - Actualizar servicio
  - `ToggleServiceStatusUseCase` - Activar/desactivar servicio

## Fase 6: Testing y Refinamiento (Semana 6)

### 6.1 Testing Unitario
- [ ] **TASK-041**: Testing de ViewModels
  - `ProfessionalJobsViewModelTest`
  - `ProposalsViewModelTest`
  - `MessagesViewModelTest`
  - `MyServicesViewModelTest`

- [ ] **TASK-042**: Testing de Casos de Uso
  - `GetJobsUseCaseTest`
  - `CreateProposalUseCaseTest`
  - `SendMessageUseCaseTest`
  - `CreateServiceUseCaseTest`

- [ ] **TASK-043**: Testing de Repositorios
  - `ProfessionalJobsRepositoryTest`
  - `ProfessionalProposalsRepositoryTest`
  - `ProfessionalMessagesRepositoryTest`
  - `ProfessionalServicesRepositoryTest`

### 6.2 Testing de UI
- [ ] **TASK-044**: Testing de Pantallas
  - `ProfessionalJobsScreenTest`
  - `ProposalsScreenTest`
  - `MessagesScreenTest`
  - `MyServicesScreenTest`

- [ ] **TASK-045**: Testing de Componentes
  - `JobCardTest`
  - `ProposalCardTest`
  - `ChatBubbleTest`
  - `ServiceCardTest`

- [ ] **TASK-046**: Testing de Navegación
  - `ProfessionalNavigationTest`
  - `BottomNavigationTest`
  - `DeepLinkingTest`

### 6.3 Testing de Integración
- [ ] **TASK-047**: Testing de API
  - `ProfessionalJobsApiTest`
  - `ProfessionalProposalsApiTest`
  - `ProfessionalMessagesApiTest`
  - `ProfessionalServicesApiTest`

- [ ] **TASK-048**: Testing de Base de Datos
  - `ProfessionalDatabaseTest`
  - `CacheSyncTest`
  - `DataMigrationTest`

### 6.4 Optimizaciones y Bug Fixes
- [ ] **TASK-049**: Optimizaciones de Performance
  - Implementar lazy loading en listas
  - Optimizar imágenes con Coil
  - Implementar cache inteligente
  - Optimizar consultas de base de datos

- [ ] **TASK-050**: Mejoras de UX
  - Agregar animaciones suaves
  - Implementar pull-to-refresh
  - Mejorar feedback visual
  - Optimizar tiempos de carga

- [ ] **TASK-051**: Bug Fixes y Testing Final
  - Corregir bugs encontrados
  - Testing de regresión
  - Testing de performance
  - Testing de accesibilidad

## Criterios de Aceptación por Fase

### Fase 1 - Configuración Base
- [ ] Estructura de paquetes creada
- [ ] Navegación configurada
- [ ] Inyección de dependencias funcionando
- [ ] Modelos de datos implementados

### Fase 2 - Subflujo Trabajos
- [ ] Listado de trabajos funcionando
- [ ] Búsqueda y filtros implementados
- [ ] Detalle de trabajo mostrando información completa
- [ ] Creación de propuestas funcionando

### Fase 3 - Subflujo Propuestas
- [ ] Listado de propuestas por estado
- [ ] Filtros y búsqueda de propuestas
- [ ] Detalle de propuesta con acciones
- [ ] Gestión de estados funcionando

### Fase 4 - Subflujo Mensajes
- [ ] Inbox con conversaciones
- [ ] Chat individual funcionando
- [ ] Notificaciones push configuradas
- [ ] Tiempo real implementado

### Fase 5 - Subflujo Mis Servicios
- [ ] Listado de servicios con filtros
- [ ] Crear/editar servicio funcionando
- [ ] Gestión de perfil profesional
- [ ] Multimedia implementado

### Fase 6 - Testing y Refinamiento
- [ ] Cobertura de testing > 80%
- [ ] Performance optimizada
- [ ] Bugs corregidos
- [ ] UX mejorada

## Estimación de Tiempo

| Fase | Tareas | Tiempo Estimado |
|------|--------|-----------------|
| Fase 1 | 6 tareas | 5 días |
| Fase 2 | 9 tareas | 5 días |
| Fase 3 | 6 tareas | 5 días |
| Fase 4 | 8 tareas | 5 días |
| Fase 5 | 9 tareas | 5 días |
| Fase 6 | 11 tareas | 5 días |
| **Total** | **49 tareas** | **30 días** |

## Dependencias

### Dependencias Externas
- Backend API funcionando
- Firebase Cloud Messaging configurado
- Servicios de geolocalización
- Servicios de pago integrados

### Dependencias Internas
- Sistema de autenticación funcionando
- Navegación principal configurada
- Componentes base reutilizables
- Base de datos local configurada

## Riesgos y Mitigaciones

### Riesgos Técnicos
1. **Complejidad de navegación**: Mitigación con arquitectura modular
2. **Performance con listas grandes**: Mitigación con paginación y lazy loading
3. **Sincronización de datos**: Mitigación con cache local y refresh strategies

### Riesgos de Negocio
1. **Cambios en API**: Mitigación con versionado y adaptadores
2. **Requisitos cambiantes**: Mitigación con arquitectura flexible
3. **Integración compleja**: Mitigación con testing exhaustivo

## Notas de Implementación

### Estándares de Código
- Seguir convenciones de Kotlin
- Usar Material Design 3
- Implementar testing exhaustivo
- Documentar código complejo

### Consideraciones de Performance
- Implementar lazy loading
- Usar cache local eficientemente
- Optimizar imágenes
- Minimizar re-renders

### Consideraciones de UX
- Feedback visual claro
- Estados de carga apropiados
- Manejo de errores user-friendly
- Navegación intuitiva
