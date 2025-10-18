# 📱 Instrucciones para Android Developer

**Especificación**: Módulo de Profesional  
**Subagente**: android-developer  
**Total de Tareas**: 34 en 13 grupos  
**Duración Estimada**: 4 semanas  

---

## 🎯 Tu Responsabilidad

Como **android-developer**, eres responsable de implementar:
- Todas las pantallas del módulo profesional
- Componentes UI reutilizables
- ViewModels y State Management
- Navegación entre pantallas
- Integración con APIs
- Optimizaciones de UX

---

## 📋 Orden de Implementación

### SEMANA 1 - CRÍTICA ⭐
**Grupo 1.1: Configuración del Proyecto** (22 horas)
- TASK-001: Estructura de paquetes
- TASK-002: Navegación
- TASK-003: Inyección de dependencias

**Por qué primero**: Base requerida para todas las pantallas

**Entregables**:
```
com/thecodefather/untigrito/presentation/screens/professional/
com/thecodefather/untigrito/domain/model/professional/
com/thecodefather/untigrito/data/repository/professional/
com/thecodefather/untigrito/di/ProfessionalModule.kt
ProfessionalRoutes.kt
ProfessionalNavigationGraph.kt
ProfessionalBottomNavigation.kt
```

---

### SEMANA 2 - SUBFLUJO TRABAJOS
**Grupo 2.1: Pantalla de Listado de Trabajos** (24 horas)
- TASK-009, TASK-010, TASK-011, TASK-012

**Pantalla**: `ProfessionalJobsScreen.kt`
**Componentes**:
- `JobCard.kt` - Tarjeta individual de trabajo
- `JobFilters.kt` - Componente de filtros
- `SearchBar.kt` - Búsqueda

**ViewModel**: `ProfessionalJobsViewModel.kt`
**Features**:
- Listado paginado de trabajos
- Búsqueda por texto
- Filtros por categoría, ubicación, presupuesto
- Lazy loading
- Marcar favoritos

---

**Grupo 2.2: Pantalla de Detalle de Trabajo** (10 horas)
- TASK-013, TASK-014

**Pantalla**: `JobDetailScreen.kt`
**Features**:
- Mostrar detalles completos del trabajo
- Información del cliente
- Ubicación con mapa
- Botón "Generar Propuesta"

---

**Grupo 2.3: Pantalla de Crear Propuesta** (18 horas)
- TASK-015, TASK-016, TASK-017

**Pantalla**: `CreateProposalScreen.kt`
**Componentes**:
- Campo de monto
- Descripción
- Toggles: Materiales, Garantía
- Términos y condiciones

**Validación**: Todos los campos requeridos

---

### SEMANA 3 - SUBFLUJO PROPUESTAS
**Grupo 3.1: Pantalla de Listado de Propuestas** (18 horas)
- TASK-018, TASK-019, TASK-020

**Pantalla**: `ProposalsScreen.kt`
**Tabs**: Abiertas, En Curso, Historial
**Componentes**: `ProposalCard.kt`

---

**Grupo 3.2: Pantalla de Detalle de Propuesta** (10 horas)
- TASK-021, TASK-022

**Pantalla**: `ProposalDetailScreen.kt`
**Features**:
- Mostrar información completa de la propuesta
- Acciones según el estado
- Historial de cambios

---

### SEMANA 4 - SUBFLUJOS MENSAJES Y SERVICIOS
**Grupo 4.1: Pantalla de Inbox** (16 horas)
- TASK-024, TASK-025, TASK-026

**Pantalla**: `MessagesScreen.kt`
**Componentes**: `ConversationCard.kt`
**Features**:
- Chat de soporte prominente
- Lista de conversaciones
- Indicadores de no leídos
- Búsqueda de contactos

---

**Grupo 4.2: Pantalla de Chat** (18 horas)
- TASK-027, TASK-028, TASK-029

**Pantalla**: `ChatScreen.kt`
**Componentes**: `ChatBubble.kt`
**Features**:
- Lista de mensajes con burbujas
- Campo de texto para escribir
- Botón de envío
- Indicador "escribiendo..."

---

**Grupo 5.1: Pantalla de Listado de Servicios** (16 horas)
- TASK-032, TASK-033, TASK-034

**Pantalla**: `MyServicesScreen.kt`
**Componentes**: `ServiceCard.kt`
**Tabs**: Todos, Activos, Inactivos
**FAB**: Crear nuevo servicio

---

**Grupo 5.2: Pantalla de Crear/Editar Servicio** (22 horas)
- TASK-035, TASK-036, TASK-037

**Pantalla**: `ServiceFormScreen.kt`
**Formulario**:
- Información básica (título, descripción)
- Rango de precios (mín/máx)
- Categoría y especialidades
- Zona de servicio (con mapa)
- Galería de imágenes/videos
- Guardar/Cancelar

**Componentes**:
- `PriceRangeSelector.kt`
- `CategorySelector.kt`
- `ServiceZoneSelector.kt`
- `MediaGallery.kt`

---

**Grupo 5.3: Gestión de Perfil Profesional** (16 horas)
- TASK-038, TASK-039, TASK-040

**Pantalla**: `ProfessionalProfileScreen.kt`
**Features**:
- Mostrar información del profesional
- Editar perfil
- Estadísticas (solicitudes, calificación)
- Mostrar reseñas

---

### SEMANA 6 - OPTIMIZACIONES
**Grupo 6.4: Optimizaciones y Bug Fixes** (24 horas)
- TASK-049, TASK-050, TASK-051

**Tareas**:
- Optimizar performance (lazy loading, caching)
- Mejorar UX (animaciones, feedback)
- Corregir bugs encontrados
- Testing final

---

## 🛠️ Arquitectura a Seguir

### Estado de UI (UiState Pattern)
```kotlin
data class ProfessionalJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: JobFilter = JobFilter.RECENT
)
```

### ViewModels
```kotlin
@HiltViewModel
class ProfessionalJobsViewModel @Inject constructor(
    private val getJobsUseCase: GetJobsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfessionalJobsUiState())
    val uiState: StateFlow<ProfessionalJobsUiState> = _uiState.asStateFlow()
    
    fun loadJobs() { /* ... */ }
    fun searchJobs(query: String) { /* ... */ }
    fun filterJobs(filter: JobFilter) { /* ... */ }
}
```

### Componentes Composables
```kotlin
@Composable
fun JobCard(
    job: Job,
    onJobClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    // Implementar según diseño
}
```

---

## 📚 Archivos Base Proporcionados

**Leer primero**:
1. `spec.md` - Especificación completa
2. `technical-spec.md` - Detalles técnicos
3. `app/docs/back.md` - API documentation

**Componentes existentes** (reutilizar):
- `AppButton` - Botones estandarizados
- `LoadingIndicator` - Indicadores de carga
- `ErrorSnackbar` - Mensajes de error
- `GoogleSignInButton` - Autenticación

---

## ✅ Checklist de Implementación

### Configuración
- [ ] Estructura de paquetes creada
- [ ] Navegación configurada
- [ ] DI funcionando
- [ ] Bottom Navigation implementada

### Pantallas
- [ ] ProfessionalJobsScreen
- [ ] JobDetailScreen
- [ ] CreateProposalScreen
- [ ] ProposalsScreen
- [ ] ProposalDetailScreen
- [ ] MessagesScreen
- [ ] ChatScreen
- [ ] MyServicesScreen
- [ ] ServiceFormScreen
- [ ] ProfessionalProfileScreen

### Componentes
- [ ] JobCard
- [ ] ProposalCard
- [ ] ServiceCard
- [ ] ConversationCard
- [ ] ChatBubble
- [ ] Todos los selectores de formulario

### ViewModels
- [ ] ProfessionalJobsViewModel
- [ ] JobDetailViewModel
- [ ] CreateProposalViewModel
- [ ] ProposalsViewModel
- [ ] ProposalDetailViewModel
- [ ] MessagesViewModel
- [ ] ChatViewModel
- [ ] MyServicesViewModel
- [ ] ServiceFormViewModel
- [ ] ProfessionalProfileViewModel

### Testing
- [ ] Todos los ViewModels testeados
- [ ] Componentes testeados
- [ ] Navegación testeada

---

## 📝 Reporte de Implementación

**Después de completar cada grupo**, crear archivo:
```
implementation/[NN]-[nombre].md
```

**Incluir**:
- Tareas completadas (checklist)
- Archivos generados
- Desafíos encontrados
- Soluciones aplicadas
- Notas adicionales

---

## 🚨 Dependencias Críticas

✅ **Espera a que database-engineer complete**: Modelos de datos  
✅ **Espera a que api-engineer complete**: Servicios de API  
⚠️ **Coordina con testing-engineer**: Para testing paralelo

---

## 📞 Sincronización

- **Con api-engineer**: Interfaces de API y mappers
- **Con testing-engineer**: Tests cuando haya código
- **Con database-engineer**: Modelos de datos

---

**¡Buena suerte! 🚀**
