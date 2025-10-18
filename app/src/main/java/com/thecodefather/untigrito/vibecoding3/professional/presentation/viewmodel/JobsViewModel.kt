package com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Job
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.JobFilter
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de UI para la pantalla de trabajos
 */
data class ProfessionalJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: JobFilter = JobFilter.RECENT,
    val favorites: Set<String> = emptySet(),
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1,
    val selectedCategory: String? = null,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val searchRadius: Double? = null
)

/**
 * ViewModel para la pantalla de listado de trabajos
 */
@HiltViewModel
class JobsViewModel @Inject constructor(
    private val getJobsUseCase: GetJobsUseCase,
    private val searchJobsUseCase: SearchJobsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfessionalJobsUiState())
    val uiState: StateFlow<ProfessionalJobsUiState> = _uiState.asStateFlow()
    
    private val _eventFlow = MutableSharedFlow<JobsEvent>()
    val eventFlow: SharedFlow<JobsEvent> = _eventFlow.asSharedFlow()
    
    init {
        observeFavorites()
        loadJobs()
    }
    
    /**
     * Carga la lista inicial de trabajos
     */
    fun loadJobs(
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Double? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val currentState = _uiState.value
            val result = getJobsUseCase(
                page = 1,
                perPage = 20,
                latitude = latitude ?: currentState.userLatitude,
                longitude = longitude ?: currentState.userLongitude,
                radius = radius ?: currentState.searchRadius,
                category = currentState.selectedCategory,
                sortBy = currentState.selectedFilter.name.lowercase()
            )
            
            result.onSuccess { jobs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        jobs = jobs,
                        currentPage = 1,
                        hasMorePages = jobs.size >= 20,
                        userLatitude = latitude ?: currentState.userLatitude,
                        userLongitude = longitude ?: currentState.userLongitude,
                        searchRadius = radius ?: currentState.searchRadius
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error loading jobs"
                    )
                }
                _eventFlow.emit(JobsEvent.ShowError(error.message ?: "Unknown error"))
            }
        }
    }
    
    /**
     * Carga más páginas de trabajos (paginación)
     */
    fun loadMoreJobs() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return
        
        viewModelScope.launch {
            val currentState = _uiState.value
            val nextPage = currentState.currentPage + 1
            
            _uiState.update { it.copy(isLoading = true) }
            
            val result = getJobsUseCase(
                page = nextPage,
                perPage = 20,
                latitude = currentState.userLatitude,
                longitude = currentState.userLongitude,
                radius = currentState.searchRadius,
                category = currentState.selectedCategory,
                sortBy = currentState.selectedFilter.name.lowercase()
            )
            
            result.onSuccess { newJobs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        jobs = it.jobs + newJobs,
                        currentPage = nextPage,
                        hasMorePages = newJobs.size >= 20
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _eventFlow.emit(JobsEvent.ShowError(error.message ?: "Error loading more jobs"))
            }
        }
    }
    
    /**
     * Busca trabajos por palabras clave
     */
    fun searchJobs(query: String) {
        if (query.isEmpty()) {
            _uiState.update { it.copy(searchQuery = "") }
            loadJobs()
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, searchQuery = query, errorMessage = null) }
            
            val result = searchJobsUseCase(
                query = query,
                page = 1,
                perPage = 20
            )
            
            result.onSuccess { jobs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        jobs = jobs,
                        currentPage = 1,
                        hasMorePages = jobs.size >= 20
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error searching jobs"
                    )
                }
                _eventFlow.emit(JobsEvent.ShowError(error.message ?: "Search error"))
            }
        }
    }
    
    /**
     * Filtra trabajos por tipo
     */
    fun filterJobs(filter: JobFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        loadJobs()
    }
    
    /**
     * Filtra trabajos por categoría
     */
    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category, currentPage = 1) }
        loadJobs()
    }
    
    /**
     * Marca/desmarca un trabajo como favorito
     */
    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            val isFavorite = !_uiState.value.favorites.contains(jobId)
            
            val result = toggleFavoriteUseCase(jobId, isFavorite)
            
            result.onSuccess {
                _eventFlow.emit(JobsEvent.FavoriteToggled(jobId, isFavorite))
            }.onFailure { error ->
                _eventFlow.emit(JobsEvent.ShowError(error.message ?: "Error toggling favorite"))
            }
        }
    }
    
    /**
     * Observa los cambios en los favoritos
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            toggleFavoriteUseCase.getFavoritesFlow()
                .collect { favorites ->
                    _uiState.update { it.copy(favorites = favorites.toSet()) }
                }
        }
    }
    
    /**
     * Navega a los detalles de un trabajo
     */
    fun onJobClick(jobId: String) {
        viewModelScope.launch {
            _eventFlow.emit(JobsEvent.NavigateToJobDetail(jobId))
        }
    }
    
    /**
     * Limpia los errores
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * Eventos del ViewModel de trabajos
 */
sealed class JobsEvent {
    data class ShowError(val message: String) : JobsEvent()
    data class NavigateToJobDetail(val jobId: String) : JobsEvent()
    data class FavoriteToggled(val jobId: String, val isFavorite: Boolean) : JobsEvent()
}
