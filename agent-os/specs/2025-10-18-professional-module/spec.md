# Especificación Técnica: Módulo de Profesional

## Resumen Ejecutivo

Desarrollo completo del módulo de profesional para UnTigrito, una aplicación Android que permite a los profesionales gestionar sus servicios, buscar trabajos, enviar propuestas y comunicarse con clientes. El módulo se implementará siguiendo la arquitectura MVVM con Jetpack Compose y Hilt para inyección de dependencias.

## Contexto del Proyecto

### Arquitectura Actual
- **Patrón**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose
- **Navegación**: Navigation Compose
- **Inyección de Dependencias**: Hilt
- **Networking**: Retrofit + Kotlinx Serialization
- **Base de Datos Local**: Room
- **Imágenes**: Coil

### Estructura de Paquetes
```
com.thecodefather.untigrito/
├── presentation/
│   ├── screens/
│   ├── components/
│   └── navigation/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/
│   ├── repository/
│   ├── local/
│   └── remote/
└── di/
```

## Funcionalidades Principales

### 1. Subflujo: Trabajos (Jobs)
**Pantallas**: Listado de Trabajos, Detalle de Trabajo, Formulario de Propuesta

**User Stories**:
- P-TR-001: Ver listado de trabajos disponibles
- P-TR-002: Buscar trabajos por palabras clave
- P-TR-003: Filtrar por Recientes, Recomendados, Favoritos
- P-TR-004: Ver detalles completos de un trabajo
- P-TR-005: Generar propuesta con monto y descripción
- P-TR-006: Especificar materiales y garantía
- P-TR-007: Incluir términos y condiciones

**Endpoints Backend**:
- `GET /api/services/postings/list` - Listar trabajos
- `GET /api/services/postings/[id]` - Detalle de trabajo
- `POST /api/services/offers/create` - Crear propuesta

### 2. Subflujo: Propuestas (Proposals)
**Pantallas**: Listado de Propuestas, Detalle de Propuesta

**User Stories**:
- P-PR-001: Ver listado de propuestas enviadas
- P-PR-002: Filtrar por Abiertas, En curso, Historial
- P-PR-003: Ver monto, cliente y estado

**Endpoints Backend**:
- `GET /api/professionals/[id]/offers` - Listar propuestas del profesional

### 3. Subflujo: Mensajes (Messages)
**Pantallas**: Inbox, Chat con Cliente, Chat de Soporte

**User Stories**:
- P-MS-001: Ver listado de chats recientes
- P-MS-002: Acceso directo a chat de soporte
- P-MS-003: Indicador de mensajes no leídos

**Endpoints Backend**:
- `GET /api/messages/conversations` - Listar conversaciones
- `GET /api/messages/[conversationId]` - Obtener mensajes
- `POST /api/messages/send` - Enviar mensaje

### 4. Subflujo: Mis Servicios (My Services)
**Pantallas**: Listado de Servicios, Crear/Editar Servicio

**User Stories**:
- P-SR-001: Ver listado de servicios publicados
- P-SR-002: Filtrar por Todos, Activos, Inactivos
- P-SR-003: Crear nuevo servicio
- P-SR-004: Editar información de servicio
- P-SR-005: Definir rango de precios
- P-SR-006: Añadir categoría y zona de servicio

**Endpoints Backend**:
- `GET /api/professionals/profile` - Obtener perfil
- `PUT /api/professionals/profile/update` - Actualizar perfil
- `POST /api/services/create` - Crear servicio
- `PUT /api/services/[id]/update` - Actualizar servicio

## Arquitectura Técnica

### Capa de Presentación
```kotlin
// Estructura de ViewModels
class ProfessionalJobsViewModel @Inject constructor(
    private val getJobsUseCase: GetJobsUseCase,
    private val createProposalUseCase: CreateProposalUseCase
) : ViewModel()

// Estados de UI
data class ProfessionalJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: JobFilter = JobFilter.RECENT
)
```

### Capa de Dominio
```kotlin
// Entidades
data class Job(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val clientId: String,
    val status: JobStatus,
    val location: Location?,
    val deadline: String?
)

// Casos de Uso
class GetJobsUseCase @Inject constructor(
    private val jobsRepository: JobsRepository
) {
    suspend operator fun invoke(filters: JobFilters): Result<List<Job>>
}
```

### Capa de Datos
```kotlin
// Repository Implementation
class JobsRepositoryImpl @Inject constructor(
    private val jobsApiService: JobsApiService,
    private val jobsDao: JobsDao
) : JobsRepository {
    
    override suspend fun getJobs(filters: JobFilters): Result<List<Job>> {
        return try {
            val response = jobsApiService.getJobs(filters.toQueryParams())
            val jobs = response.data.jobs.map { it.toDomain() }
            jobsDao.insertJobs(jobs.map { it.toEntity() })
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## Navegación

### Estructura de Navegación
```kotlin
object ProfessionalRoutes {
    const val JOBS_LIST = "professional/jobs"
    const val JOB_DETAIL = "professional/jobs/{jobId}"
    const val CREATE_PROPOSAL = "professional/proposals/create/{jobId}"
    const val PROPOSALS_LIST = "professional/proposals"
    const val PROPOSAL_DETAIL = "professional/proposals/{proposalId}"
    const val MESSAGES_LIST = "professional/messages"
    const val CHAT = "professional/chat/{conversationId}"
    const val MY_SERVICES = "professional/services"
    const val CREATE_SERVICE = "professional/services/create"
    const val EDIT_SERVICE = "professional/services/edit/{serviceId}"
}
```

### Bottom Navigation Bar
```kotlin
@Composable
fun ProfessionalBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Work, contentDescription = "Trabajos") },
            label = { Text("Trabajos") },
            selected = currentRoute.startsWith("professional/jobs"),
            onClick = { onNavigate(ProfessionalRoutes.JOBS_LIST) }
        )
        // ... otros items
    }
}
```

## Componentes UI Reutilizables

### Componentes Existentes a Reutilizar
- `AppButton` - Botones estandarizados
- `LoadingIndicator` - Indicadores de carga
- `ErrorSnackbar` - Mensajes de error
- `GoogleSignInButton` - Botón de autenticación

### Nuevos Componentes Necesarios
```kotlin
// JobCard - Tarjeta de trabajo
@Composable
fun JobCard(
    job: Job,
    onJobClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
)

// ProposalCard - Tarjeta de propuesta
@Composable
fun ProposalCard(
    proposal: Proposal,
    onProposalClick: (String) -> Unit
)

// ServiceCard - Tarjeta de servicio
@Composable
fun ServiceCard(
    service: Service,
    onEditClick: (String) -> Unit,
    onToggleStatus: (String) -> Unit
)

// ChatBubble - Burbuja de chat
@Composable
fun ChatBubble(
    message: Message,
    isFromCurrentUser: Boolean
)
```

## Integración con Backend

### Servicios de API
```kotlin
interface ProfessionalApiService {
    @GET("services/postings/list")
    suspend fun getJobs(@QueryMap filters: Map<String, String>): ApiResponse<JobsListResponse>
    
    @GET("services/postings/{id}")
    suspend fun getJobDetail(@Path("id") jobId: String): ApiResponse<JobDetailResponse>
    
    @POST("services/offers/create")
    suspend fun createProposal(@Body request: CreateProposalRequest): ApiResponse<ProposalResponse>
    
    @GET("professionals/profile")
    suspend fun getProfessionalProfile(): ApiResponse<ProfessionalProfileResponse>
    
    @PUT("professionals/profile/update")
    suspend fun updateProfessionalProfile(@Body request: UpdateProfileRequest): ApiResponse<ProfileResponse>
}
```

### Modelos de Datos
```kotlin
@Serializable
data class JobResponse(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val clientId: String,
    val status: String,
    val location: LocationResponse?,
    val deadline: String?,
    val createdAt: String
)

@Serializable
data class CreateProposalRequest(
    val postingId: String,
    val proposedPrice: Double,
    val description: String,
    val estimatedDuration: Int,
    val includesMaterials: Boolean = false,
    val offersWarranty: Boolean = false,
    val termsAndConditions: String? = null
)
```

## Estados y Flujos de Datos

### Estados de Carga
```kotlin
sealed class ProfessionalUiState {
    object Loading : ProfessionalUiState()
    data class Success<T>(val data: T) : ProfessionalUiState()
    data class Error(val message: String, val exception: Throwable? = null) : ProfessionalUiState()
}
```

### Gestión de Estados
```kotlin
class ProfessionalJobsViewModel @Inject constructor(
    private val getJobsUseCase: GetJobsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfessionalJobsUiState())
    val uiState: StateFlow<ProfessionalJobsUiState> = _uiState.asStateFlow()
    
    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getJobsUseCase(_uiState.value.filters)
                .onSuccess { jobs ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }
}
```

## Consideraciones de UX/UI

### Principios de Diseño
1. **Consistencia**: Reutilizar componentes existentes
2. **Accesibilidad**: Cumplir con Material Design 3
3. **Performance**: Lazy loading y paginación
4. **Offline**: Cache local con Room
5. **Feedback**: Estados de carga y error claros

### Patrones de Navegación
- **Bottom Navigation**: Navegación principal entre secciones
- **Back Navigation**: Navegación hacia atrás consistente
- **Deep Linking**: Soporte para enlaces profundos
- **State Restoration**: Preservar estado en rotaciones

## Testing

### Estrategia de Testing
```kotlin
// Unit Tests
class ProfessionalJobsViewModelTest {
    @Test
    fun `loadJobs should emit loading then success state`() = runTest {
        // Given
        val mockUseCase = mockk<GetJobsUseCase>()
        coEvery { mockUseCase(any()) } returns Result.success(listOf())
        
        // When
        val viewModel = ProfessionalJobsViewModel(mockUseCase)
        viewModel.loadJobs()
        
        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }
}

// UI Tests
@Test
fun professionalJobsScreen_displaysJobsCorrectly() {
    composeTestRule.setContent {
        ProfessionalJobsScreen(
            viewModel = mockViewModel,
            onJobClick = {}
        )
    }
    
    composeTestRule.onNodeWithText("Trabajo de ejemplo").assertIsDisplayed()
}
```

## Cronograma de Implementación

### Fase 1: Estructura Base (Semana 1)
- Configuración de navegación
- ViewModels base
- Servicios de API
- Modelos de datos

### Fase 2: Subflujo Trabajos (Semana 2)
- Listado de trabajos
- Búsqueda y filtros
- Detalle de trabajo
- Formulario de propuesta

### Fase 3: Subflujo Propuestas (Semana 3)
- Listado de propuestas
- Detalle de propuesta
- Estados de propuesta

### Fase 4: Subflujo Mensajes (Semana 4)
- Listado de conversaciones
- Chat con cliente
- Chat de soporte

### Fase 5: Subflujo Mis Servicios (Semana 5)
- Listado de servicios
- Crear/editar servicio
- Gestión de estado

### Fase 6: Testing y Refinamiento (Semana 6)
- Tests unitarios
- Tests de UI
- Optimizaciones
- Bug fixes

## Criterios de Aceptación

### Funcionalidad
- [ ] Usuario puede ver listado de trabajos disponibles
- [ ] Usuario puede buscar y filtrar trabajos
- [ ] Usuario puede crear propuestas
- [ ] Usuario puede gestionar sus propuestas
- [ ] Usuario puede comunicarse con clientes
- [ ] Usuario puede gestionar sus servicios

### Performance
- [ ] Tiempo de carga < 2 segundos
- [ ] Soporte offline básico
- [ ] Gestión eficiente de memoria

### UX/UI
- [ ] Navegación intuitiva
- [ ] Feedback visual claro
- [ ] Accesibilidad básica
- [ ] Responsive design

## Riesgos y Mitigaciones

### Riesgos Técnicos
1. **Complejidad de navegación**: Mitigación con arquitectura modular
2. **Performance con listas grandes**: Mitigación con paginación y lazy loading
3. **Sincronización de datos**: Mitigación con cache local y refresh strategies

### Riesgos de Negocio
1. **Cambios en API**: Mitigación con versionado y adaptadores
2. **Requisitos cambiantes**: Mitigación con arquitectura flexible
3. **Integración compleja**: Mitigación con testing exhaustivo
