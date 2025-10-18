package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.repository.JobsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobsRepository: JobsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    fun loadJob(jobId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            jobsRepository.getJobById(jobId)
                .collect { job ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        job = job,
                        errorMessage = if (job == null) "Trabajo no encontrado" else null
                    )
                }
        }
    }

    fun toggleFavorite() {
        _uiState.value.job?.let { job ->
            viewModelScope.launch {
                jobsRepository.toggleFavorite(job.id)
                loadJob(job.id) // Recargar para actualizar el estado
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class JobDetailUiState(
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val job: Job? = null,
    val errorMessage: String? = null
)