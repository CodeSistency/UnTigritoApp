package com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.GetProposalDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de UI para ProposalDetailScreen
 */
data class ProposalDetailUiState(
    val isLoading: Boolean = true,
    val proposal: Proposal? = null,
    val errorMessage: String? = null
)

/**
 * Eventos de ProposalDetailScreen
 */
sealed class ProposalDetailEvent {
    data class NavigateToChat(val conversationId: String) : ProposalDetailEvent()
    data class ShowError(val message: String) : ProposalDetailEvent()
}

/**
 * ViewModel para la pantalla de detalles de propuesta
 */
@HiltViewModel
class ProposalDetailViewModel @Inject constructor(
    private val getProposalDetailsUseCase: GetProposalDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalDetailUiState())
    val uiState: StateFlow<ProposalDetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ProposalDetailEvent>()
    val eventFlow: SharedFlow<ProposalDetailEvent> = _eventFlow.asSharedFlow()

    /**
     * Cargar detalles de propuesta
     */
    fun loadProposalDetails(proposalId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val result = getProposalDetailsUseCase(proposalId)
                
                result.onSuccess { proposal ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            proposal = proposal,
                            errorMessage = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al cargar la propuesta"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    /**
     * Abrir chat
     */
    fun openChat(conversationId: String) {
        viewModelScope.launch {
            _eventFlow.emit(ProposalDetailEvent.NavigateToChat(conversationId))
        }
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
