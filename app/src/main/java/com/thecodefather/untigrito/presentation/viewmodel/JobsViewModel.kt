package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    init {
        Timber.d("JobsViewModel initialized")
    }

    // Cargar trabajos directamente desde Supabase
    fun loadJobs() {
        viewModelScope.launch {
            Timber.d("loadJobs() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                Timber.d("Fetching all ServicePosting from database...")
                // Consulta directa a ServicePosting
                val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                
                result.onSuccess { postings ->
                    Timber.d("Successfully fetched ${postings.size} service postings")
                    Timber.d("Raw postings: $postings")

                    val openPostings = postings.filter { it.status == "OPEN" }
                    Timber.d("Filtered to ${openPostings.size} OPEN postings")

                    val jobs = openPostings.map { posting ->
                        Timber.d("Mapping posting: id=${posting.id}, title=${posting.title}, status=${posting.status}")
                        mapSupabaseToJob(posting)
                    }
                    Timber.d("Mapped ${jobs.size} jobs successfully")

                    val filteredJobs = when (_uiState.value.selectedFilter) {
                        JobFilter.RECENT -> {
                            Timber.d("Applying RECENT filter")
                            jobs.sortedByDescending { it.createdAt }
                        }
                        JobFilter.RECOMMENDED -> {
                            Timber.d("Applying RECOMMENDED filter")
                            jobs.filter { it.isRecommended }
                        }
                        JobFilter.FAVORITES -> {
                            Timber.d("Applying FAVORITES filter")
                            jobs.filter { it.isFavorite }
                        }
                    }
                    Timber.d("After filter: ${filteredJobs.size} jobs")

                    val searchedJobs = if (_uiState.value.searchQuery.isNotEmpty()) {
                        Timber.d("Applying search query: ${_uiState.value.searchQuery}")
                        filteredJobs.filter { job ->
                            job.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    job.description.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    )
                        }
                    } else {
                        filteredJobs
                    }
                    Timber.d("Final jobs count: ${searchedJobs.size}")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = searchedJobs,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load jobs from database")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJobs()")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Cargar trabajos con información completa del cliente
    fun loadJobsWithClientInfo() {
        viewModelScope.launch {
            Timber.d("loadJobsWithClientInfo() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                Timber.d("Fetching all ServicePosting with client info...")
                // Consulta directa a ServicePosting
                val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                
                result.onSuccess { postings ->
                    Timber.d("Successfully fetched ${postings.size} service postings")

                    val openPostings = postings.filter { it.status == "OPEN" }
                    Timber.d("Filtered to ${openPostings.size} OPEN postings")

                    val jobsWithClientInfo = openPostings.map { posting ->
                        Timber.d("Fetching client info for posting: ${posting.id}, clientId: ${posting.clientId}")
                        // Obtener información del cliente para cada posting
                        val clientResult =
                            supabaseDatabase.getById<SupabaseUser>("User", posting.clientId)
                        val client = clientResult.getOrNull()

                        if (client != null) {
                            Timber.d("Client found: ${client.name} (${client.id})")
                        } else {
                            Timber.w("Client not found for posting ${posting.id}")
                        }

                        mapSupabaseToJobWithClient(posting, client)
                    }
                    Timber.d("Mapped ${jobsWithClientInfo.size} jobs with client info")

                    val filteredJobs = when (_uiState.value.selectedFilter) {
                        JobFilter.RECENT -> {
                            Timber.d("Applying RECENT filter")
                            jobsWithClientInfo.sortedByDescending { it.createdAt }
                        }
                        JobFilter.RECOMMENDED -> {
                            Timber.d("Applying RECOMMENDED filter")
                            jobsWithClientInfo.filter { it.isRecommended }
                        }
                        JobFilter.FAVORITES -> {
                            Timber.d("Applying FAVORITES filter")
                            jobsWithClientInfo.filter { it.isFavorite }
                        }
                    }
                    Timber.d("After filter: ${filteredJobs.size} jobs")

                    val searchedJobs = if (_uiState.value.searchQuery.isNotEmpty()) {
                        Timber.d("Applying search query: ${_uiState.value.searchQuery}")
                        filteredJobs.filter { job ->
                            job.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    job.description.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    ) ||
                                    job.clientName.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    )
                        }
                    } else {
                        filteredJobs
                    }
                    Timber.d("Final jobs count: ${searchedJobs.size}")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = searchedJobs,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully with client info")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load jobs with client info")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJobsWithClientInfo()")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Filtrar trabajos por especialidad del profesional autenticado
    fun loadJobsBySpecialty() {
        viewModelScope.launch {
            Timber.d("loadJobsBySpecialty() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                Timber.d("Current user ID: $currentUserId")
                
                if (currentUserId == null) {
                    Timber.w("User not authenticated")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Obtener especialidades del profesional actual
                Timber.d("Fetching professional profile for user: $currentUserId")
                val professionalResult = supabaseDatabase.getById<SupabaseUser>("User", currentUserId)
                
                professionalResult.onSuccess { professional ->
                    if (professional != null) {
                        Timber.d("Professional found: ${professional.name}, role: ${professional.role}")

                        // Filtrar trabajos por categorías que coincidan con las especialidades del profesional
                        val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                        
                        result.onSuccess { postings ->
                            Timber.d("Fetched ${postings.size} postings for specialty filtering")

                            val jobs = postings
                                .filter { it.status == "OPEN" }
                                .map { posting -> mapSupabaseToJob(posting) }
                                .filter { job ->
                                    // Filtrar por especialidad (esto requeriría una tabla de especialidades)
                                    // Por ahora, mostrar todos los trabajos abiertos
                                    Timber.d("Job: ${job.id}, category: ${job.category}")
                                    true
                                }
                                .sortedByDescending { it.createdAt }

                            Timber.d("Filtered ${jobs.size} jobs by specialty")

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                jobs = jobs,
                                errorMessage = null
                            )
                        }.onFailure { exception ->
                            Timber.e(exception, "Failed to fetch postings for specialty filtering")
                        }
                    } else {
                        Timber.w("Professional profile not found for user: $currentUserId")
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to fetch professional profile")
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJobsBySpecialty()")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Filtrar trabajos por ubicación geográfica
    fun loadJobsByLocation(lat: Double, lng: Double, radiusKm: Double = 10.0) {
        viewModelScope.launch {
            Timber.d("loadJobsByLocation() called with lat=$lat, lng=$lng, radius=$radiusKm km")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.findByLocation<SupabaseServicePosting>(
                    "ServicePosting",
                    lat,
                    lng,
                    radiusKm
                )
                
                result.onSuccess { postings ->
                    Timber.d("Found ${postings.size} postings near location")

                    val jobs = postings
                        .filter { it.status == "OPEN" }
                        .map { posting -> mapSupabaseToJob(posting) }
                        .sortedByDescending { it.createdAt }

                    Timber.d("Filtered to ${jobs.size} OPEN jobs")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load jobs by location")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos por ubicación"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJobsByLocation()")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Búsqueda directa en Supabase
    fun searchJobs(query: String) {
        Timber.d("searchJobs() called with query: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadJobs()
    }
    
    // Obtener trabajo por ID
    fun loadJob(jobId: String) {
        viewModelScope.launch {
            Timber.d("loadJob() called with jobId: $jobId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", jobId)
                
                result.onSuccess { posting ->
                    if (posting != null) {
                        Timber.d("Job found: ${posting.title}")
                        val job = mapSupabaseToJob(posting)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            job = job,
                            errorMessage = null
                        )
                    } else {
                        Timber.w("Job not found with id: $jobId")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Trabajo no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load job with id: $jobId")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJob()")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        Timber.d("updateSearchQuery() called with query: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadJobs()
    }

    fun updateFilter(filter: JobFilter) {
        Timber.d("updateFilter() called with filter: $filter")
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadJobs()
    }

    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            Timber.d("toggleFavorite() called for jobId: $jobId")
            try {
                // Actualizar estado local
                val updatedJobs = _uiState.value.jobs.map { job ->
                    if (job.id == jobId) {
                        val newFavoriteState = !job.isFavorite
                        Timber.d("Toggling favorite for job ${job.title}: $newFavoriteState")
                        job.copy(isFavorite = newFavoriteState)
                    } else {
                        job
                    }
                }
                _uiState.value = _uiState.value.copy(jobs = updatedJobs)
                
                // Actualizar trabajo actual si existe
                if (_uiState.value.job?.id == jobId) {
                    val newFavoriteState = !(_uiState.value.job?.isFavorite ?: false)
                    Timber.d("Toggling favorite for current job: $newFavoriteState")
                    _uiState.value = _uiState.value.copy(
                        job = _uiState.value.job?.copy(isFavorite = newFavoriteState)
                    )
                }

                Timber.d("Favorite toggled successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al actualizar favorito"
                )
            }
        }
    }
    
    // Mapper helper para convertir SupabaseServicePosting a Job
    private fun mapSupabaseToJob(posting: SupabaseServicePosting): Job {
        Timber.d("Mapping SupabaseServicePosting to Job: id=${posting.id}")

        try {
            val job = Job(
                id = posting.id,
                title = posting.title,
                description = posting.description,
                category = posting.categoryId,
                budget = posting.budget ?: 0.0,
                clientId = posting.clientId,
                clientName = "", // Se obtiene por separado si es necesario
                clientAvatar = null,
                status = when (posting.status) {
                    "OPEN" -> JobStatus.OPEN
                    "IN_PROGRESS" -> JobStatus.IN_PROGRESS
                    "COMPLETED" -> JobStatus.COMPLETED
                    "CANCELLED" -> JobStatus.CANCELLED
                    else -> {
                        Timber.w("Unknown job status: ${posting.status}, defaulting to OPEN")
                        JobStatus.OPEN
                    }
                },
                location = posting.address?.let { address ->
                    Location(
                        address = address,
                        city = "",
                        state = "",
                        coordinates = if (posting.lat != null && posting.lng != null) {
                            Coordinates(posting.lat, posting.lng)
                        } else null
                    )
                },
                deadline = posting.requiredTo?.let {
                    try {
                        Date(it)
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to parse deadline: $it")
                        null
                    }
                },
                createdAt = try {
                    posting.createdAt?.let {
                        java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            java.util.Locale.getDefault()
                        ).parse(it) ?: Date()
                    } ?: Date()
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${posting.createdAt}")
                    Date()
                },
                isRecommended = false,
                isFavorite = false,
                urgency = UrgencyLevel.NORMAL,
                estimatedDuration = null,
                requiredSkills = emptyList(),
                attachments = emptyList()
            )

            Timber.d("Successfully mapped job: ${job.title}")
            return job
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseServicePosting to Job")
            throw e
        }
    }

    // Mapper mejorado que incluye información del cliente
    private fun mapSupabaseToJobWithClient(posting: SupabaseServicePosting, client: SupabaseUser?): Job {
        Timber.d("Mapping SupabaseServicePosting with client info: id=${posting.id}")

        try {
            val job = Job(
                id = posting.id,
                title = posting.title,
                description = posting.description,
                category = posting.categoryId,
                budget = posting.budget ?: 0.0,
                clientId = posting.clientId,
                clientName = client?.name ?: "Cliente",
                clientAvatar = null, // Se puede obtener de la tabla de usuarios si hay avatar
                status = when (posting.status) {
                    "OPEN" -> JobStatus.OPEN
                    "IN_PROGRESS" -> JobStatus.IN_PROGRESS
                    "COMPLETED" -> JobStatus.COMPLETED
                    "CANCELLED" -> JobStatus.CANCELLED
                    else -> {
                        Timber.w("Unknown job status: ${posting.status}, defaulting to OPEN")
                        JobStatus.OPEN
                    }
                },
                location = posting.address?.let { address ->
                    Location(
                        address = address,
                        city = "",
                        state = "",
                        coordinates = if (posting.lat != null && posting.lng != null) {
                            Coordinates(posting.lat, posting.lng)
                        } else null
                    )
                },
                deadline = posting.requiredTo?.let {
                    try {
                        Date(it)
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to parse deadline: $it")
                        null
                    }
                },
                createdAt = try {
                    posting.createdAt?.let {
                        java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            java.util.Locale.getDefault()
                        ).parse(it) ?: Date()
                    } ?: Date()
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${posting.createdAt}")
                    Date()
                },
                isRecommended = false,
                isFavorite = false,
                urgency = UrgencyLevel.NORMAL,
                estimatedDuration = null,
                requiredSkills = emptyList(),
                attachments = emptyList()
            )

            Timber.d("Successfully mapped job with client: ${job.title}, client: ${job.clientName}")
            return job
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseServicePosting with client to Job")
            throw e
        }
    }

    fun clearError() {
        Timber.d("clearError() called")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class JobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val job: Job? = null,
    val searchQuery: String = "",
    val selectedFilter: JobFilter = JobFilter.RECENT,
    val errorMessage: String? = null,
    val selectedCategory: String? = null,
    val hasMorePages: Boolean = false
)