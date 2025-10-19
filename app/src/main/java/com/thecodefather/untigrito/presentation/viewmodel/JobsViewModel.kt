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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    // Cargar trabajos directamente desde Supabase
    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Consulta directa a ServicePosting
                val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                
                result.onSuccess { postings ->
                    val jobs = postings
                        .filter { it.status == "OPEN" } // Solo trabajos abiertos
                        .map { posting -> mapSupabaseToJob(posting) }
                        .let { allJobs ->
                            // Aplicar filtros
                            when (_uiState.value.selectedFilter) {
                                JobFilter.RECENT -> allJobs.sortedByDescending { it.createdAt }
                                JobFilter.RECOMMENDED -> allJobs.filter { it.isRecommended }
                                JobFilter.FAVORITES -> allJobs.filter { it.isFavorite }
                            }
                        }
                        .let { filteredJobs ->
                            // Aplicar búsqueda si existe
                            if (_uiState.value.searchQuery.isNotEmpty()) {
                                filteredJobs.filter { job ->
                                    job.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    job.description.contains(_uiState.value.searchQuery, ignoreCase = true)
                                }
                            } else {
                                filteredJobs
                            }
                        }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos"
                    )
                }
            } catch (e: Exception) {
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
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Consulta directa a ServicePosting
                val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                
                result.onSuccess { postings ->
                    val jobsWithClientInfo = postings
                        .filter { it.status == "OPEN" }
                        .map { posting ->
                            // Obtener información del cliente para cada posting
                            val clientResult = supabaseDatabase.getById<SupabaseUser>("User", posting.clientId)
                            val client = clientResult.getOrNull()
                            
                            mapSupabaseToJobWithClient(posting, client)
                        }
                        .let { allJobs ->
                            // Aplicar filtros
                            when (_uiState.value.selectedFilter) {
                                JobFilter.RECENT -> allJobs.sortedByDescending { it.createdAt }
                                JobFilter.RECOMMENDED -> allJobs.filter { it.isRecommended }
                                JobFilter.FAVORITES -> allJobs.filter { it.isFavorite }
                            }
                        }
                        .let { filteredJobs ->
                            // Aplicar búsqueda si existe
                            if (_uiState.value.searchQuery.isNotEmpty()) {
                                filteredJobs.filter { job ->
                                    job.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    job.description.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    job.clientName.contains(_uiState.value.searchQuery, ignoreCase = true)
                                }
                            } else {
                                filteredJobs
                            }
                        }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobsWithClientInfo,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos"
                    )
                }
            } catch (e: Exception) {
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
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                
                if (currentUserId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Obtener especialidades del profesional actual
                val professionalResult = supabaseDatabase.getById<SupabaseUser>("User", currentUserId)
                
                professionalResult.onSuccess { professional ->
                    if (professional != null) {
                        // Filtrar trabajos por categorías que coincidan con las especialidades del profesional
                        val result = supabaseDatabase.getAll<SupabaseServicePosting>("ServicePosting")
                        
                        result.onSuccess { postings ->
                            val jobs = postings
                                .filter { it.status == "OPEN" }
                                .map { posting -> mapSupabaseToJob(posting) }
                                .filter { job ->
                                    // Filtrar por especialidad (esto requeriría una tabla de especialidades)
                                    // Por ahora, mostrar todos los trabajos abiertos
                                    true
                                }
                                .sortedByDescending { it.createdAt }
                            
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                jobs = jobs,
                                errorMessage = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
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
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.findByLocation<SupabaseServicePosting>(
                    "ServicePosting",
                    lat,
                    lng,
                    radiusKm
                )
                
                result.onSuccess { postings ->
                    val jobs = postings
                        .filter { it.status == "OPEN" }
                        .map { posting -> mapSupabaseToJob(posting) }
                        .sortedByDescending { it.createdAt }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar trabajos por ubicación"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Búsqueda directa en Supabase
    fun searchJobs(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadJobs()
    }
    
    // Obtener trabajo por ID
    fun loadJob(jobId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", jobId)
                
                result.onSuccess { posting ->
                    if (posting != null) {
                        val job = mapSupabaseToJob(posting)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            job = job,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Trabajo no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadJobs()
    }

    fun updateFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadJobs()
    }

    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            try {
                // Actualizar estado local
                val updatedJobs = _uiState.value.jobs.map { job ->
                    if (job.id == jobId) {
                        job.copy(isFavorite = !job.isFavorite)
                    } else {
                        job
                    }
                }
                _uiState.value = _uiState.value.copy(jobs = updatedJobs)
                
                // Actualizar trabajo actual si existe
                if (_uiState.value.job?.id == jobId) {
                    _uiState.value = _uiState.value.copy(
                        job = _uiState.value.job?.copy(isFavorite = !(_uiState.value.job?.isFavorite ?: false))
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al actualizar favorito"
                )
            }
        }
    }
    
    // Mapper helper para convertir SupabaseServicePosting a Job
    private fun mapSupabaseToJob(posting: SupabaseServicePosting): Job {
        return Job(
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
                else -> JobStatus.OPEN
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
                    null
                }
            },
            createdAt = try {
                posting.createdAt?.let { 
                    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).parse(it) ?: Date()
                } ?: Date()
            } catch (e: Exception) {
                Date()
            },
            isRecommended = false,
            isFavorite = false,
            urgency = UrgencyLevel.NORMAL,
            estimatedDuration = null,
            requiredSkills = emptyList(),
            attachments = emptyList()
        )
    }

    // Mapper mejorado que incluye información del cliente
    private fun mapSupabaseToJobWithClient(posting: SupabaseServicePosting, client: SupabaseUser?): Job {
        return Job(
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
                else -> JobStatus.OPEN
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
                    null
                }
            },
            createdAt = try {
                posting.createdAt?.let { 
                    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).parse(it) ?: Date()
                } ?: Date()
            } catch (e: Exception) {
                Date()
            },
            isRecommended = false,
            isFavorite = false,
            urgency = UrgencyLevel.NORMAL,
            estimatedDuration = null,
            requiredSkills = emptyList(),
            attachments = emptyList()
        )
    }

    fun clearError() {
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