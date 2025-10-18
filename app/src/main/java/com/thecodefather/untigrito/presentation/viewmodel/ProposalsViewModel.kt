package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProposalsViewModel @Inject constructor(
    private val proposalsRepository: ProposalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalsUiState())
    val uiState: StateFlow<ProposalsUiState> = _uiState.asStateFlow()

    fun loadProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            proposalsRepository.getProposals(_uiState.value.selectedFilter)
                .collect { proposals ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposals = proposals,
                        errorMessage = null
                    )
                }
        }
    }

    fun updateFilter(filter: ProposalFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadProposals()
    }

    fun withdrawProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            proposalsRepository.withdrawProposal(proposalId)
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                            loadProposals() // Recargar la lista
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ProposalsUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val errorMessage: String? = null
)