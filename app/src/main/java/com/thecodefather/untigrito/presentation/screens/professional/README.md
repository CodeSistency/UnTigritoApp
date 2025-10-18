# Modo Trabajador - UnTigritoApp

## Descripción General

Este módulo implementa la funcionalidad completa del Modo Trabajador para UnTigritoApp, permitiendo a los profesionales gestionar sus servicios, buscar trabajos, enviar propuestas y comunicarse con clientes.

## Estructura del Módulo

```
presentation/screens/professional/
├── jobs/                    # Subflujo de Trabajos
│   ├── JobsScreen.kt
│   ├── JobDetailScreen.kt
│   └── CreateProposalScreen.kt
├── proposals/              # Subflujo de Propuestas
│   ├── ProposalsScreen.kt
│   └── ProposalDetailScreen.kt
├── messages/               # Subflujo de Mensajes
│   ├── MessagesScreen.kt
│   └── ChatScreen.kt
├── services/               # Subflujo de Mis Servicios
│   ├── ServicesScreen.kt
│   └── CreateEditServiceScreen.kt
├── ProfessionalMainScreen.kt
└── README.md
```

## Funcionalidades Implementadas

### 1. Subflujo de Trabajos (P-TR-001 a P-TR-007)
- ✅ **P-TR-001**: Ver listado de trabajos disponibles
- ✅ **P-TR-002**: Buscar trabajos por palabras clave
- ✅ **P-TR-003**: Filtrar por Recientes, Recomendados, Favoritos
- ✅ **P-TR-004**: Ver detalles completos de un trabajo
- ✅ **P-TR-005**: Generar propuesta con monto y descripción
- ✅ **P-TR-006**: Especificar materiales y garantía
- ✅ **P-TR-007**: Incluir términos y condiciones

### 2. Subflujo de Propuestas (P-PR-001 a P-PR-003)
- ✅ **P-PR-001**: Ver listado de propuestas enviadas
- ✅ **P-PR-002**: Filtrar por Abiertas, En curso, Historial
- ✅ **P-PR-003**: Ver monto, cliente y estado

### 3. Subflujo de Mensajes (P-MS-001 a P-MS-003)
- ✅ **P-MS-001**: Ver listado de chats recientes
- ✅ **P-MS-002**: Acceso directo a chat de soporte
- ✅ **P-MS-003**: Indicador de mensajes no leídos

### 4. Subflujo de Mis Servicios (P-SR-001 a P-SR-006)
- ✅ **P-SR-001**: Ver listado de servicios publicados
- ✅ **P-SR-002**: Filtrar por Todos, Activos, Inactivos
- ✅ **P-SR-003**: Crear nuevo servicio
- ✅ **P-SR-004**: Editar información de servicio
- ✅ **P-SR-005**: Definir rango de precios
- ✅ **P-SR-006**: Añadir categoría y zona de servicio

## Arquitectura Técnica

### Patrón de Arquitectura
- **Clean Architecture** con separación de capas
- **MVVM** para la gestión de estado
- **Repository Pattern** para acceso a datos
- **Dependency Injection** con Hilt

### Componentes Principales

#### Modelos de Dominio
- `Job`: Entidad de trabajo con información completa
- `Proposal`: Entidad de propuesta con estados
- `Message`: Entidad de mensaje con tipos
- `Service`: Entidad de servicio con categorías
- `Conversation`: Entidad de conversación

#### Repositorios
- `JobsRepository`: Gestión de trabajos y búsquedas
- `ProposalsRepository`: Gestión de propuestas
- `MessagesRepository`: Gestión de mensajes y conversaciones
- `ServicesRepository`: Gestión de servicios del profesional

#### ViewModels
- `JobsViewModel`: Estado y lógica de trabajos
- `JobDetailViewModel`: Estado y lógica de detalle de trabajo
- `ProposalsViewModel`: Estado y lógica de propuestas
- `ProposalDetailViewModel`: Estado y lógica de detalle de propuesta
- `MessagesViewModel`: Estado y lógica de mensajes
- `ServicesViewModel`: Estado y lógica de servicios

### Datos Dummy

Todos los repositorios implementan datos dummy realistas que incluyen:

#### Trabajos (5 ejemplos)
- Reparación de grifo en cocina
- Instalación de aire acondicionado
- Pintura de apartamento
- Reparación de nevera
- Instalación de cerradura digital

#### Propuestas (5 ejemplos)
- Propuestas en diferentes estados (Pendiente, Aceptada, Rechazada, En Progreso, Completada)
- Información completa de precios, descripciones y términos

#### Conversaciones (4 ejemplos)
- Chats con clientes
- Chat de soporte
- Mensajes con diferentes estados de lectura

#### Servicios (5 ejemplos)
- Reparación de Plomería
- Instalación de Aires Acondicionados
- Pintura Residencial
- Reparación de Electrodomésticos
- Instalación de Cerraduras

## Navegación

### Estructura de Navegación
```
ProfessionalMainScreen
├── JobsScreen (Pestaña principal)
│   ├── JobDetailScreen
│   └── CreateProposalScreen
├── ProposalsScreen (Pestaña principal)
│   └── ProposalDetailScreen
├── MessagesScreen (Pestaña principal)
│   └── ChatScreen
└── ServicesScreen (Pestaña principal)
    ├── CreateEditServiceScreen
    └── ServiceDetailScreen
```

### Bottom Navigation
- **Trabajos**: Listado y búsqueda de trabajos
- **Propuestas**: Gestión de propuestas enviadas
- **Mensajes**: Comunicación con clientes y soporte
- **Mis Servicios**: Gestión de servicios publicados

## Componentes Reutilizables

### Componentes de UI
- `SearchBar`: Barra de búsqueda estandarizada
- `FilterTabs`: Pestañas de filtro reutilizables
- `JobCard`: Tarjeta de trabajo con información completa
- `ProposalCard`: Tarjeta de propuesta con estado
- `ConversationCard`: Tarjeta de conversación con contador
- `ChatBubble`: Burbuja de chat con estados
- `ServiceCard`: Tarjeta de servicio con acciones

### Características de los Componentes
- **Material Design 3**: Diseño moderno y consistente
- **Estados de carga**: Indicadores de progreso
- **Manejo de errores**: Mensajes de error claros
- **Accesibilidad**: Soporte para TalkBack
- **Responsive**: Adaptable a diferentes tamaños

## Integración

### Inyección de Dependencias
El módulo está configurado en `ProfessionalModule.kt` con:
- Repositorios con datos dummy
- ViewModels con Hilt
- Configuración de navegación

### Uso en la Aplicación
```kotlin
// En tu NavGraph principal
composable(ProfessionalNavigation.PROFESSIONAL_MAIN) {
    ProfessionalMainScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}
```

## Testing

### Estrategia de Testing
- **Unit Tests**: ViewModels y casos de uso
- **UI Tests**: Navegación y flujos principales
- **Integration Tests**: Repositorios y datos dummy

### Datos de Prueba
Los datos dummy incluyen escenarios realistas para testing:
- Trabajos con diferentes estados de urgencia
- Propuestas en todos los estados posibles
- Conversaciones con mensajes no leídos
- Servicios activos e inactivos

## Próximos Pasos

### Integración con Backend
1. Reemplazar implementaciones dummy con llamadas a API reales
2. Implementar autenticación y autorización
3. Agregar manejo de errores de red
4. Implementar cache local con Room

### Mejoras de UX
1. Agregar animaciones de transición
2. Implementar pull-to-refresh
3. Agregar notificaciones push
4. Mejorar accesibilidad

### Funcionalidades Adicionales
1. Geolocalización para trabajos cercanos
2. Sistema de calificaciones y reseñas
3. Integración con pagos
4. Chat en tiempo real

## Conclusión

El Modo Trabajador está completamente implementado con:
- ✅ **4 subflujos principales** con todas las funcionalidades
- ✅ **Navegación completa** entre pantallas
- ✅ **Datos dummy realistas** para testing
- ✅ **Arquitectura limpia** y mantenible
- ✅ **Componentes reutilizables** y consistentes
- ✅ **Integración con Hilt** para inyección de dependencias

La implementación sigue las mejores prácticas de Android y está lista para integración con el backend real.
