package com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.ProposalFilter
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.GetProposalsUseCase
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.GetProposalDetailsUseCase
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.CreateProposalUseCase
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.CancelProposalUseCase
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
 * Estado de UI para listado de propuestas
 */
data class ProposalsListUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val errorMessage: String? = null,
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)

/**
 * Estado de UI para crear propuesta
 */
data class CreateProposalUiState(
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val jobId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val includesMaterials: Boolean = false,
    val offerWarranty: Boolean = false,
    val warrantyDescription: String = "",
    val terms: String = ""
)

/**
 * ViewModel para listado y gestión de propuestas
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
    
    fun loadProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val status = _uiState.value.selectedFilter.name.lowercase()
            
            val result = getProposalsUseCase(page = 1, perPage = 20, status = status)
            
            result.onSuccess { proposals ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    proposals = proposals,
                    currentPage = 1,
                    hasMorePages = proposals.size >= 20
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error loading proposals"
                )
            }
        }
    }
    
    fun filterProposals(filter: ProposalFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadProposals()
    }
    
    fun loadMoreProposals() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return
        
        viewModelScope.launch {
            val nextPage = _uiState.value.currentPage + 1
            val status = _uiState.value.selectedFilter.name.lowercase()
            
            val result = getProposalsUseCase(page = nextPage, perPage = 20, status = status)
            
            result.onSuccess { proposals ->
                _uiState.value = _uiState.value.copy(
                    proposals = _uiState.value.proposals + proposals,
                    currentPage = nextPage,
                    hasMorePages = proposals.size >= 20
                )
            }
        }
    }
    
    fun cancelProposal(proposalId: String) {
        viewModelScope.launch {
            val result = cancelProposalUseCase(proposalId)
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    proposals = _uiState.value.proposals.filter { it.id != proposalId }
                )
                _eventFlow.emit(ProposalsListEvent.ProposalCancelled)
            }.onFailure { error ->
                _eventFlow.emit(ProposalsListEvent.ShowError(error.message ?: "Error cancelling proposal"))
            }
        }
    }
    
    fun onProposalClick(proposalId: String) {
        viewModelScope.launch {
            _eventFlow.emit(ProposalsListEvent.NavigateToDetail(proposalId))
        }
    }
}

/**
 * ViewModel para crear propuesta
 */
@HiltViewModel
class CreateProposalViewModel @Inject constructor(
    private val createProposalUseCase: CreateProposalUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateProposalUiState())
    val uiState: StateFlow<CreateProposalUiState> = _uiState.asStateFlow()
    
    private val _eventFlow = MutableSharedFlow<CreateProposalEvent>()
    val eventFlow: SharedFlow<CreateProposalEvent> = _eventFlow.asSharedFlow()
    
    fun initWithJobId(jobId: String) {
        _uiState.value = _uiState.value.copy(jobId = jobId)
    }
    
    fun updateAmount(amount: Double) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }
    
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    fun toggleIncludesMaterials() {
        _uiState.value = _uiState.value.copy(
            includesMaterials = !_uiState.value.includesMaterials
        )
    }
    
    fun toggleOfferWarranty() {
        _uiState.value = _uiState.value.copy(
            offerWarranty = !_uiState.value.offerWarranty
        )
    }
    
    fun updateWarrantyDescription(description: String) {
        _uiState.value = _uiState.value.copy(warrantyDescription = description)
    }
    
    fun updateTerms(terms: String) {
        _uiState.value = _uiState.value.copy(terms = terms)
    }
    
    fun submitProposal() {
        val state = _uiState.value
        
        // Validar campos requeridos
        if (state.amount <= 0) {
            viewModelScope.launch {
                _eventFlow.emit(CreateProposalEvent.ShowError("El monto debe ser mayor a 0"))
            }
            return
        }
        
        if (state.description.isEmpty()) {
            viewModelScope.launch {
                _eventFlow.emit(CreateProposalEvent.ShowError("La descripción es requerida"))
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            
            val result = createProposalUseCase(
                jobId = state.jobId,
                amount = state.amount,
                description = state.description,
                includesMaterials = state.includesMaterials,
                offerWarranty = state.offerWarranty,
                warrantyDescription = if (state.offerWarranty) state.warrantyDescription else null,
                terms = state.terms.takeIf { it.isNotEmpty() }
            )
            
            result.onSuccess { proposal ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSuccess = true
                )
                _eventFlow.emit(CreateProposalEvent.ProposalCreated(proposal.id))
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = error.message ?: "Error creating proposal"
                )
                _eventFlow.emit(CreateProposalEvent.ShowError(error.message ?: "Unknown error"))
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

// Eventos
sealed class ProposalsListEvent {
    data class ShowError(val message: String) : ProposalsListEvent()
    data class NavigateToDetail(val proposalId: String) : ProposalsListEvent()
    object ProposalCancelled : ProposalsListEvent()
}

sealed class CreateProposalEvent {
    data class ShowError(val message: String) : CreateProposalEvent()
    data class ProposalCreated(val proposalId: String) : CreateProposalEvent()
}
