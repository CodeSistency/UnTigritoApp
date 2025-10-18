package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProposalDetailViewModel @Inject constructor(
    private val proposalsRepository: ProposalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalDetailUiState())
    val uiState: StateFlow<ProposalDetailUiState> = _uiState.asStateFlow()

    fun loadProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            proposalsRepository.getProposalById(proposalId)
                .collect { proposal ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposal = proposal,
                        errorMessage = if (proposal == null) "Propuesta no encontrada" else null
                    )
                }
        }
    }

    fun withdrawProposal() {
        _uiState.value.proposal?.let { proposal ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                proposalsRepository.withdrawProposal(proposal.id)
                    .collect { result ->
                        result.fold(
                            onSuccess = {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    showWithdrawSuccess = true
                                )
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = exception.message ?: "Error al retirar propuesta"
                                )
                            }
                        )
                    }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearWithdrawSuccess() {
        _uiState.value = _uiState.value.copy(showWithdrawSuccess = false)
    }
}

data class ProposalDetailUiState(
    val isLoading: Boolean = false,
    val proposal: Proposal? = null,
    val errorMessage: String? = null,
    val showWithdrawSuccess: Boolean = false
)