package com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.ProposalFilter
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.GetProposalsUseCase
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.CancelProposalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de UI para ProposalsListScreen
 */
data class ProposalsListUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val errorMessage: String? = null,
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 0
)

/**
 * Eventos de ProposalsListScreen
 */
sealed class ProposalsListEvent {
    data class NavigateToDetail(val proposalId: String) : ProposalsListEvent()
    data class ShowError(val message: String) : ProposalsListEvent()
    data class ProposalCancelled(val proposalId: String) : ProposalsListEvent()
}

/**
 * ViewModel para la pantalla de listado de propuestas
 */
@HiltViewModel
class ProposalsListViewModel @Inject constructor(
    private val getProposalsUseCase: GetProposalsUseCase,
    private val cancelProposalUseCase: CancelProposalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalsListUiState())
    val uiState: StateFlow<ProposalsListUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ProposalsListEvent>()
    val eventFlow: SharedFlow<ProposalsListEvent> = _eventFlow.asSharedFlow()

    init {
        loadProposals()
    }

    /**
     * Cargar propuestas
     */
    fun loadProposals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val currentState = _uiState.value
                val result = getProposalsUseCase(
                    page = currentState.currentPage,
                    status = currentState.selectedFilter.name
                )
                
                result.onSuccess { proposals ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            proposals = proposals,
                            errorMessage = null,
                            hasMorePages = proposals.size >= 20
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error desconocido"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar propuestas"
                    )
                }
            }
        }
    }

    /**
     * Filtrar propuestas por estado
     */
    fun filterProposals(filter: ProposalFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                currentPage = 0,
                proposals = emptyList()
            )
        }
        loadProposals()
    }

    /**
     * Cargar más propuestas (paginación)
     */
    fun loadMoreProposals() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val currentState = _uiState.value
                val result = getProposalsUseCase(
                    page = currentState.currentPage + 1,
                    status = currentState.selectedFilter.name
                )
                
                result.onSuccess { newProposals ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            proposals = currentState.proposals + newProposals,
                            currentPage = currentState.currentPage + 1,
                            hasMorePages = newProposals.size >= 20
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error desconocido"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar más propuestas"
                    )
                }
            }
        }
    }

    /**
     * Cancelar una propuesta
     */
    fun cancelProposal(proposalId: String) {
        viewModelScope.launch {
            try {
                val result = cancelProposalUseCase(proposalId)
                
                result.onSuccess {
                    // Actualizar lista removiendo la propuesta cancelada
                    _uiState.update { state ->
                        state.copy(
                            proposals = state.proposals.filter { it.id != proposalId }
                        )
                    }
                    _eventFlow.emit(ProposalsListEvent.ProposalCancelled(proposalId))
                }.onFailure { exception ->
                    _eventFlow.emit(
                        ProposalsListEvent.ShowError(
                            exception.message ?: "Error al cancelar propuesta"
                        )
                    )
                }
            } catch (e: Exception) {
                _eventFlow.emit(
                    ProposalsListEvent.ShowError(e.message ?: "Error desconocido")
                )
            }
        }
    }

    /**
     * Navegar a detalles de propuesta
     */
    fun onProposalClick(proposalId: String) {
        viewModelScope.launch {
            _eventFlow.emit(ProposalsListEvent.NavigateToDetail(proposalId))
        }
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
