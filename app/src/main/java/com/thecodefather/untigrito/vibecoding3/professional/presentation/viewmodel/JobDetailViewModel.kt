package com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Job
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.GetJobDetailsUseCase
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de UI para la pantalla de detalles del trabajo
 */
data class JobDetailUiState(
    val isLoading: Boolean = true,
    val job: Job? = null,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val isSubmittingProposal: Boolean = false
)

/**
 * ViewModel para la pantalla de detalles del trabajo
 */
@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val getJobDetailsUseCase: GetJobDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()
    
    private val _eventFlow = MutableSharedFlow<JobDetailEvent>()
    val eventFlow: SharedFlow<JobDetailEvent> = _eventFlow.asSharedFlow()
    
    /**
     * Carga los detalles del trabajo
     */
    fun loadJobDetails(jobId: String) {
        viewModelScope.launch {
            _uiState.value = JobDetailUiState(isLoading = true)
            
            val result = getJobDetailsUseCase(jobId)
            
            result.onSuccess { job ->
                _uiState.value = JobDetailUiState(
                    isLoading = false,
                    job = job,
                    isFavorite = false // TODO: Obtener del estado compartido de favoritos
                )
            }.onFailure { error ->
                _uiState.value = JobDetailUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Error loading job details"
                )
                _eventFlow.emit(JobDetailEvent.ShowError(error.message ?: "Unknown error"))
            }
        }
    }
    
    /**
     * Marca/desmarca como favorito
     */
    fun toggleFavorite() {
        val job = _uiState.value.job ?: return
        
        viewModelScope.launch {
            val newFavoriteState = !_uiState.value.isFavorite
            
            val result = toggleFavoriteUseCase(job.id, newFavoriteState)
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)
                _eventFlow.emit(JobDetailEvent.FavoriteToggled(newFavoriteState))
            }.onFailure { error ->
                _eventFlow.emit(JobDetailEvent.ShowError(error.message ?: "Error toggling favorite"))
            }
        }
    }
    
    /**
     * Navega a crear propuesta
     */
    fun createProposal() {
        val job = _uiState.value.job ?: return
        viewModelScope.launch {
            _eventFlow.emit(JobDetailEvent.NavigateToCreateProposal(job.id))
        }
    }
    
    /**
     * Limpia los errores
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Eventos del ViewModel de detalles del trabajo
 */
sealed class JobDetailEvent {
    data class ShowError(val message: String) : JobDetailEvent()
    data class NavigateToCreateProposal(val jobId: String) : JobDetailEvent()
    data class FavoriteToggled(val isFavorite: Boolean) : JobDetailEvent()
}
