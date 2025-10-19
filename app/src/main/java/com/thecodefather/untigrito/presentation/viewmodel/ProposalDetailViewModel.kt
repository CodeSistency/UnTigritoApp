package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseOffer
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProposalDetailViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalDetailUiState())
    val uiState: StateFlow<ProposalDetailUiState> = _uiState.asStateFlow()

    fun loadProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.getById<SupabaseOffer>("Offer", proposalId)
                
                result.onSuccess { offer ->
                    if (offer != null) {
                        val proposal = mapSupabaseToProposal(offer)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposal = proposal,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Propuesta no encontrada"
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

    fun withdrawProposal() {
        _uiState.value.proposal?.let { proposal ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                try {
                    val result = supabaseDatabase.delete("Offer", proposal.id)
                    
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showWithdrawSuccess = true
                        )
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al retirar propuesta"
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
    }
    
    // Mapper helper
    private fun mapSupabaseToProposal(offer: SupabaseOffer): Proposal {
        return Proposal(
            id = offer.id,
            jobId = offer.postingId,
            jobTitle = "",
            clientId = "",
            clientName = "",
            clientAvatar = null,
            proposedPrice = offer.proposedPrice ?: offer.price,
            description = offer.message ?: "",
            estimatedDuration = 0,
            includesMaterials = false,
            offersWarranty = false,
            termsAndConditions = null,
            status = when (offer.status) {
                "PENDING" -> ProposalStatus.PENDING
                "ACCEPTED" -> ProposalStatus.ACCEPTED
                "REJECTED" -> ProposalStatus.REJECTED
                "IN_PROGRESS" -> ProposalStatus.IN_PROGRESS
                "COMPLETED" -> ProposalStatus.COMPLETED
                "CANCELLED" -> ProposalStatus.CANCELLED
                else -> ProposalStatus.PENDING
            },
            createdAt = try {
                Date(offer.createdAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            updatedAt = try {
                Date(offer.createdAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            responseMessage = null,
            responseDate = null
        )
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