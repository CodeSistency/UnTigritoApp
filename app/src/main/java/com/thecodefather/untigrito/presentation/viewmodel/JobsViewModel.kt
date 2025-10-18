package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.model.JobFilter
import com.thecodefather.untigrito.domain.repository.JobsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobsRepository: JobsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            jobsRepository.getJobs(_uiState.value.selectedFilter, _uiState.value.searchQuery)
                .collect { jobs ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        errorMessage = null
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
            jobsRepository.toggleFavorite(jobId)
            loadJobs() // Recargar para actualizar el estado
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


data class JobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: JobFilter = JobFilter.RECENT,
    val errorMessage: String? = null,
    val selectedCategory: String? = null,
    val hasMorePages: Boolean = false
)