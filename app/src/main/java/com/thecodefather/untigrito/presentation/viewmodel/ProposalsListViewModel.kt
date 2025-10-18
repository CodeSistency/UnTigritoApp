package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProposalsListViewModel @Inject constructor(
    private val proposalsRepository: ProposalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalsListUiState())
    val uiState: StateFlow<ProposalsListUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ProposalsListEvent>()
    val eventFlow: SharedFlow<ProposalsListEvent> = _eventFlow.asSharedFlow()

    init {
        loadProposals()
    }

    fun loadProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                proposalsRepository.getProposals(_uiState.value.selectedFilter)
                    .collect { proposals ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposals = proposals,
                            errorMessage = null,
                            hasMorePages = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar propuestas"
                )
                _eventFlow.emit(ProposalsListEvent.ShowError(e.message ?: "Error desconocido"))
            }
        }
    }

    fun filterProposals(filter: ProposalFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadProposals()
    }

    fun loadMoreProposals() {
        // Simulación de paginación
        // En una aplicación real, aquí se cargaría la siguiente página
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                proposalsRepository.getProposals(_uiState.value.selectedFilter)
                    .collect { proposals ->
                        val currentProposals = _uiState.value.proposals
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposals = currentProposals + proposals,
                            hasMorePages = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar más propuestas"
                )
            }
        }
    }

    fun onProposalClick(proposalId: String) {
        viewModelScope.launch {
            _eventFlow.emit(ProposalsListEvent.NavigateToDetail(proposalId))
        }
    }

    fun cancelProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = proposalsRepository.cancelProposal(proposalId)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _eventFlow.emit(ProposalsListEvent.ProposalCancelled(proposalId))
                        loadProposals() // Recargar la lista
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al cancelar propuesta"
                        )
                        _eventFlow.emit(ProposalsListEvent.ShowError(exception.message ?: "Error desconocido"))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cancelar propuesta"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ProposalsListUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val errorMessage: String? = null,
    val hasMorePages: Boolean = false
)
