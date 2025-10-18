# Especificación Técnica Detallada: Módulo de Profesional

## Arquitectura y Estructura

### Navegación Principal
- **Integración**: Sección separada dentro de la app principal UnTigrito
- **Acceso**: A través de Bottom Navigation Bar con 4 secciones principales
- **Autenticación**: Mismo sistema de login, pero requiere verificación de identidad para acceder al módulo profesional

### Estructura de Navegación
```kotlin
object ProfessionalRoutes {
    // Navegación principal
    const val PROFESSIONAL_HOME = "professional/home"
    
    // Subflujo Trabajos
    const val JOBS_LIST = "professional/jobs"
    const val JOB_DETAIL = "professional/jobs/{jobId}"
    const val CREATE_PROPOSAL = "professional/proposals/create/{jobId}"
    
    // Subflujo Propuestas
    const val PROPOSALS_LIST = "professional/proposals"
    const val PROPOSAL_DETAIL = "professional/proposals/{proposalId}"
    
    // Subflujo Mensajes
    const val MESSAGES_LIST = "professional/messages"
    const val CHAT = "professional/chat/{conversationId}"
    const val SUPPORT_CHAT = "professional/chat/support"
    
    // Subflujo Mis Servicios
    const val MY_SERVICES = "professional/services"
    const val CREATE_SERVICE = "professional/services/create"
    const val EDIT_SERVICE = "professional/services/edit/{serviceId}"
    const val SERVICE_DETAIL = "professional/services/{serviceId}"
}
```

## Funcionalidades por Subflujo

### 1. Subflujo: Trabajos (Jobs)
**Pantallas**: Listado → Detalle → Generar Propuesta

#### Pantalla: Listado de Trabajos
```kotlin
@Composable
fun ProfessionalJobsScreen(
    viewModel: ProfessionalJobsViewModel,
    onJobClick: (String) -> Unit,
    onNavigateToProposal: (String) -> Unit
) {
    // Header con búsqueda
    // Tabs: Recientes, Recomendados, Favoritos
    // Filtros: Categoría, Ubicación, Presupuesto
    // Lista de trabajos con lazy loading
    // Bottom Navigation
}
```

**Componentes UI**:
- `JobCard` - Tarjeta de trabajo con imagen, título, descripción, precio, ubicación
- `JobFilters` - Filtros por categoría, ubicación, presupuesto
- `SearchBar` - Búsqueda por palabras clave
- `CategoryTabs` - Tabs para Recientes, Recomendados, Favoritos

#### Pantalla: Detalle de Trabajo
```kotlin
@Composable
fun JobDetailScreen(
    jobId: String,
    viewModel: JobDetailViewModel,
    onGenerateProposal: (String) -> Unit
) {
    // Imagen del trabajo
    // Título y descripción completa
    // Requisitos del cliente
    // Ubicación con mapa
    // Información del cliente
    // Botón "Generar Propuesta"
}
```

#### Pantalla: Generar Propuesta
```kotlin
@Composable
fun CreateProposalScreen(
    jobId: String,
    viewModel: CreateProposalViewModel,
    onProposalSent: () -> Unit
) {
    // Campo: Monto del presupuesto
    // Campo: Descripción de la propuesta
    // Toggles: Incluye materiales, Ofrece garantía
    // Campo: Términos y condiciones
    // Botón: Enviar propuesta
}
```

### 2. Subflujo: Propuestas (Proposals)
**Pantallas**: Listado → Detalle de Propuesta

#### Pantalla: Listado de Propuestas
```kotlin
@Composable
fun ProposalsScreen(
    viewModel: ProposalsViewModel,
    onProposalClick: (String) -> Unit
) {
    // Tabs: Abiertas, En Curso, Historial
    // Lista de propuestas con estado
    // Filtros por fecha y estado
}
```

**Estados de Propuesta**:
- `ABIERTA` - Enviada, esperando respuesta
- `EN_CURSO` - Aceptada por el cliente
- `COMPLETADA` - Trabajo finalizado
- `RECHAZADA` - Rechazada por el cliente
- `CANCELADA` - Cancelada por el profesional

### 3. Subflujo: Mensajes (Messages)
**Pantallas**: Inbox → Chat Individual

#### Pantalla: Inbox de Mensajes
```kotlin
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel,
    onChatClick: (String) -> Unit,
    onSupportClick: () -> Unit
) {
    // Chat de Soporte (siempre visible)
    // Lista de chats recientes
    // Indicadores de mensajes no leídos
    // Búsqueda de contactos
}
```

#### Pantalla: Chat Individual
```kotlin
@Composable
fun ChatScreen(
    conversationId: String,
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    // Lista de mensajes con burbujas
    // Campo de texto para escribir
    // Botón de envío
    // Indicador de "escribiendo..."
}
```

### 4. Subflujo: Mis Servicios (My Services)
**Pantallas**: Listado → Crear/Editar Servicio

#### Pantalla: Listado de Servicios
```kotlin
@Composable
fun MyServicesScreen(
    viewModel: MyServicesViewModel,
    onCreateService: () -> Unit,
    onEditService: (String) -> Unit
) {
    // Tabs: Todos, Activos, Inactivos
    // Lista de servicios con estado
    // FAB para crear nuevo servicio
    // Acciones: Editar, Activar/Desactivar
}
```

#### Pantalla: Crear/Editar Servicio
```kotlin
@Composable
fun ServiceFormScreen(
    serviceId: String? = null, // null para crear, ID para editar
    viewModel: ServiceFormViewModel,
    onServiceSaved: () -> Unit
) {
    // Información básica: Título, Descripción
    // Rango de precios: Mínimo, Máximo
    // Categoría y especialidades
    // Zona de servicio con mapa
    // Galería de imágenes/videos
    // Toggle: Activo/Inactivo
}
```

## Integración con Backend

### Endpoints Específicos del Módulo Profesional

#### Trabajos
```kotlin
interface ProfessionalJobsApiService {
    @GET("services/postings/list")
    suspend fun getJobs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("minBudget") minBudget: Double? = null,
        @Query("maxBudget") maxBudget: Double? = null,
        @Query("locationLat") locationLat: Double? = null,
        @Query("locationLng") locationLng: Double? = null,
        @Query("radius") radius: Int = 10,
        @Query("sortBy") sortBy: String = "recent"
    ): ApiResponse<JobsListResponse>
    
    @GET("services/postings/{id}")
    suspend fun getJobDetail(@Path("id") jobId: String): ApiResponse<JobDetailResponse>
}
```

#### Propuestas
```kotlin
interface ProfessionalProposalsApiService {
    @GET("professionals/{id}/offers")
    suspend fun getProposals(
        @Path("id") professionalId: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<ProposalsListResponse>
    
    @POST("services/offers/create")
    suspend fun createProposal(@Body request: CreateProposalRequest): ApiResponse<ProposalResponse>
    
    @PUT("services/offers/{id}")
    suspend fun updateProposal(
        @Path("id") proposalId: String,
        @Body request: UpdateProposalRequest
    ): ApiResponse<ProposalResponse>
}
```

#### Mensajes
```kotlin
interface ProfessionalMessagesApiService {
    @GET("messages/conversations")
    suspend fun getConversations(): ApiResponse<ConversationsResponse>
    
    @GET("messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiResponse<MessagesResponse>
    
    @POST("messages/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<MessageResponse>
}
```

#### Servicios
```kotlin
interface ProfessionalServicesApiService {
    @GET("professionals/profile")
    suspend fun getProfessionalProfile(): ApiResponse<ProfessionalProfileResponse>
    
    @PUT("professionals/profile/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<ProfileResponse>
    
    @POST("services/create")
    suspend fun createService(@Body request: CreateServiceRequest): ApiResponse<ServiceResponse>
    
    @PUT("services/{id}/update")
    suspend fun updateService(
        @Path("id") serviceId: String,
        @Body request: UpdateServiceRequest
    ): ApiResponse<ServiceResponse>
}
```

## Modelos de Datos

### Entidades del Dominio
```kotlin
// Trabajo
data class Job(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val clientId: String,
    val clientName: String,
    val clientRating: Double?,
    val status: JobStatus,
    val location: Location,
    val deadline: String?,
    val createdAt: String,
    val requirements: List<String>,
    val images: List<String>
)

// Propuesta
data class Proposal(
    val id: String,
    val jobId: String,
    val professionalId: String,
    val proposedPrice: Double,
    val description: String,
    val estimatedDuration: Int,
    val includesMaterials: Boolean,
    val offersWarranty: Boolean,
    val termsAndConditions: String?,
    val status: ProposalStatus,
    val createdAt: String,
    val updatedAt: String
)

// Servicio
data class Service(
    val id: String,
    val professionalId: String,
    val title: String,
    val description: String,
    val category: String,
    val specialties: List<String>,
    val minPrice: Double,
    val maxPrice: Double,
    val serviceZone: ServiceZone,
    val images: List<String>,
    val videos: List<String>,
    val isActive: Boolean,
    val rating: Double?,
    val totalRequests: Int,
    val createdAt: String,
    val updatedAt: String
)

// Mensaje
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val type: MessageType,
    val attachments: List<Attachment>?,
    val timestamp: String,
    val isRead: Boolean
)

// Conversación
data class Conversation(
    val id: String,
    val participants: List<String>,
    val lastMessage: Message?,
    val unreadCount: Int,
    val isSupport: Boolean,
    val updatedAt: String
)
```

### Estados y Enums
```kotlin
enum class JobStatus {
    OPEN, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class ProposalStatus {
    PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED
}

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT
}

enum class ServiceStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val state: String
)

data class ServiceZone(
    val center: Location,
    val radius: Int, // en kilómetros
    val cities: List<String>
)
```

## Componentes UI Específicos

### JobCard Component
```kotlin
@Composable
fun JobCard(
    job: Job,
    onJobClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onJobClick(job.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = { onFavoriteClick(job.id) }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Favorito")
                    }
                    Text(
                        text = "$${job.budget}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Imagen del trabajo
            AsyncImage(
                model = job.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            // Información del cliente
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = job.clientName,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (job.clientRating != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                        Text(
                            text = "${job.clientRating}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Ubicación y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = job.location.address,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = job.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(job.requirements) { requirement ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = requirement,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
```

### ProposalCard Component
```kotlin
@Composable
fun ProposalCard(
    proposal: Proposal,
    job: Job,
    onProposalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProposalClick(proposal.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = when (proposal.status) {
                        ProposalStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer
                        ProposalStatus.ACCEPTED -> Color(0xFF4CAF50)
                        ProposalStatus.REJECTED -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = proposal.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Imagen del trabajo
            AsyncImage(
                model = job.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            // Información de la propuesta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monto: $${proposal.proposedPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Duración: ${proposal.estimatedDuration}h",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Descripción de la propuesta
            Text(
                text = proposal.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Tags de características
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (proposal.includesMaterials) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Material",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (proposal.offersWarranty) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Garantía",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
```

## Notificaciones Push

### Tipos de Notificaciones
```kotlin
enum class NotificationType {
    PROPOSAL_ACCEPTED,      // Propuesta aceptada
    PROPOSAL_REJECTED,       // Propuesta rechazada
    NEW_MESSAGE,            // Nuevo mensaje
    NEW_JOB_AVAILABLE,      // Nuevo trabajo disponible
    JOB_STATUS_CHANGED,     // Estado de trabajo cambiado
    SERVICE_REQUEST,        // Solicitud de servicio
    PAYMENT_RECEIVED,       // Pago recibido
    RATING_RECEIVED         // Nueva calificación
}

data class ProfessionalNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val data: Map<String, String>,
    val timestamp: String,
    val isRead: Boolean
)
```

### Configuración de Notificaciones
```kotlin
data class NotificationPreferences(
    val proposalAccepted: Boolean = true,
    val proposalRejected: Boolean = true,
    val newMessages: Boolean = true,
    val newJobs: Boolean = true,
    val jobStatusChanged: Boolean = true,
    val serviceRequests: Boolean = true,
    val payments: Boolean = true,
    val ratings: Boolean = true
)
```

## Geolocalización

### Filtros por Ubicación
```kotlin
data class LocationFilter(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Int = 10, // kilómetros
    val city: String? = null,
    val state: String? = null
)

@Composable
fun LocationFilterComponent(
    locationFilter: LocationFilter,
    onLocationChanged: (LocationFilter) -> Unit
) {
    Column {
        // Selector de radio de búsqueda
        Text("Radio de búsqueda: ${locationFilter.radius} km")
        Slider(
            value = locationFilter.radius.toFloat(),
            onValueChange = { radius ->
                onLocationChanged(locationFilter.copy(radius = radius.toInt()))
            },
            valueRange = 1f..50f
        )
        
        // Botón para usar ubicación actual
        Button(
            onClick = { /* Obtener ubicación actual */ }
        ) {
            Text("Usar mi ubicación")
        }
    }
}
```

## Multimedia

### Gestión de Imágenes y Videos
```kotlin
@Composable
fun MediaGallery(
    mediaItems: List<MediaItem>,
    onAddMedia: () -> Unit,
    onRemoveMedia: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(mediaItems) { item ->
            MediaItemCard(
                item = item,
                onRemove = { onRemoveMedia(item.id) }
            )
        }
        item {
            AddMediaButton(onClick = onAddMedia)
        }
    }
}

data class MediaItem(
    val id: String,
    val type: MediaType,
    val uri: String,
    val thumbnail: String? = null,
    val caption: String? = null
)

enum class MediaType {
    IMAGE, VIDEO, AUDIO, DOCUMENT
}
```

## Sistema de Calificaciones

### Modelos de Calificación
```kotlin
data class Rating(
    val id: String,
    val professionalId: String,
    val clientId: String,
    val jobId: String,
    val score: Int, // 1-5
    val comment: String?,
    val categories: Map<String, Int>, // Calificaciones por categoría
    val createdAt: String
)

data class RatingCategories(
    val punctuality: Int = 0,      // Puntualidad
    val quality: Int = 0,          // Calidad del trabajo
    val communication: Int = 0,   // Comunicación
    val professionalism: Int = 0,  // Profesionalismo
    val value: Int = 0             // Relación calidad-precio
)

@Composable
fun RatingInput(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        repeat(5) { index ->
            IconButton(
                onClick = { onRatingChanged(index + 1) }
            ) {
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Estrella ${index + 1}",
                    tint = if (index < rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

## Pagos

### Integración con Sistema de Pagos
```kotlin
interface PaymentService {
    suspend fun processPayment(amount: Double, currency: String): Result<PaymentResult>
    suspend fun getPaymentMethods(): List<PaymentMethod>
    suspend fun getPaymentHistory(): List<PaymentTransaction>
}

data class PaymentResult(
    val transactionId: String,
    val status: PaymentStatus,
    val amount: Double,
    val currency: String,
    val timestamp: String
)

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED
}

data class PaymentMethod(
    val id: String,
    val type: PaymentMethodType,
    val lastFourDigits: String,
    val isDefault: Boolean
)

enum class PaymentMethodType {
    CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, DIGITAL_WALLET
}
```

## Testing

### Testing de ViewModels
```kotlin
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
```

### Testing de UI
```kotlin
@Test
fun professionalJobsScreen_displaysJobsCorrectly() {
    composeTestRule.setContent {
        ProfessionalJobsScreen(
            viewModel = mockViewModel,
            onJobClick = {},
            onNavigateToProposal = {}
        )
    }
    
    composeTestRule.onNodeWithText("Trabajo de ejemplo").assertIsDisplayed()
}
```

## Consideraciones de Performance

### Lazy Loading
```kotlin
@Composable
fun JobsList(
    jobs: List<Job>,
    onJobClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jobs) { job ->
            JobCard(
                job = job,
                onJobClick = onJobClick
            )
        }
        
        item {
            if (jobs.isNotEmpty()) {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
        }
    }
}
```

### Cache Local
```kotlin
@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val clientId: String,
    val status: String,
    val locationLat: Double,
    val locationLng: Double,
    val address: String,
    val createdAt: String,
    val cachedAt: String
)

@Dao
interface JobsDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    suspend fun getAllJobs(): List<JobEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)
    
    @Query("DELETE FROM jobs WHERE cachedAt < :expiryDate")
    suspend fun deleteExpiredJobs(expiryDate: String)
}
```

Esta especificación técnica detallada proporciona una base sólida para la implementación del módulo de profesional, cubriendo todos los aspectos técnicos, de UI/UX y de integración necesarios.
