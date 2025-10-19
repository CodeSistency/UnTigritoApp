package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseOffer
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.domain.model.ProposalStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProposalsViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalsUiState())
    val uiState: StateFlow<ProposalsUiState> = _uiState.asStateFlow()

    // Cargar propuestas directamente desde Supabase
    fun loadProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                
                if (professionalId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Consulta directa a Offer
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    val proposals = offers
                        .filter { it.professionalId == professionalId } // Filtrar por usuario actual
                        .map { offer -> mapSupabaseToProposal(offer) }
                        .let { allProposals ->
                            // Aplicar filtros
                            when (_uiState.value.selectedFilter) {
                                ProposalFilter.OPEN -> allProposals.filter { 
                                    it.status == ProposalStatus.PENDING 
                                }
                                ProposalFilter.IN_PROGRESS -> allProposals.filter { 
                                    it.status == ProposalStatus.IN_PROGRESS || 
                                    it.status == ProposalStatus.ACCEPTED 
                                }
                                ProposalFilter.COMPLETED -> allProposals.filter { 
                                    it.status == ProposalStatus.COMPLETED 
                                }
                                ProposalFilter.REJECTED -> allProposals.filter { 
                                    it.status == ProposalStatus.REJECTED 
                                }
                                ProposalFilter.HISTORY -> allProposals.filter { 
                                    it.status == ProposalStatus.COMPLETED || 
                                    it.status == ProposalStatus.REJECTED ||
                                    it.status == ProposalStatus.CANCELLED
                                }
                            }
                        }
                        .sortedByDescending { it.createdAt }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposals = proposals,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar propuestas"
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

    // Cargar propuestas con información completa del trabajo y cliente
    fun loadProposalsWithJobInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val professionalId = authStateManager.getCurrentUserId()
                
                if (professionalId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    val proposalsWithInfo = offers
                        .filter { it.professionalId == professionalId }
                        .map { offer ->
                            // Obtener información del trabajo asociado
                            val jobResult = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", offer.postingId)
                            val job = jobResult.getOrNull()
                            
                            // Obtener información del cliente
                            val clientResult = if (job != null) {
                                supabaseDatabase.getById<SupabaseUser>("User", job.clientId)
                            } else {
                                null
                            }
                            val client = clientResult?.getOrNull()
                            
                            mapSupabaseToProposalWithDetails(offer, job, client)
                        }
                        .let { allProposals ->
                            // Aplicar filtros
                            when (_uiState.value.selectedFilter) {
                                ProposalFilter.OPEN -> allProposals.filter { 
                                    it.status == ProposalStatus.PENDING 
                                }
                                ProposalFilter.IN_PROGRESS -> allProposals.filter { 
                                    it.status == ProposalStatus.IN_PROGRESS || 
                                    it.status == ProposalStatus.ACCEPTED 
                                }
                                ProposalFilter.COMPLETED -> allProposals.filter { 
                                    it.status == ProposalStatus.COMPLETED 
                                }
                                ProposalFilter.REJECTED -> allProposals.filter { 
                                    it.status == ProposalStatus.REJECTED 
                                }
                                ProposalFilter.HISTORY -> allProposals.filter { 
                                    it.status == ProposalStatus.COMPLETED || 
                                    it.status == ProposalStatus.REJECTED ||
                                    it.status == ProposalStatus.CANCELLED
                                }
                            }
                        }
                        .sortedByDescending { it.createdAt }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposals = proposalsWithInfo,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar propuestas"
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

    // Obtener propuesta con información completa
    fun loadProposalWithDetails(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = supabaseDatabase.getById<SupabaseOffer>("Offer", proposalId)
                
                result.onSuccess { offer ->
                    if (offer != null) {
                        // Obtener información del trabajo
                        val jobResult = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", offer.postingId)
                        val job = jobResult.getOrNull()
                        
                        // Obtener información del cliente
                        val clientResult = if (job != null) {
                            supabaseDatabase.getById<SupabaseUser>("User", job.clientId)
                        } else {
                            null
                        }
                        val client = clientResult?.getOrNull()
                        
                        val proposal = mapSupabaseToProposalWithDetails(offer, job, client)
                        
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

    // Obtener contador de propuestas por estado
    fun loadProposalCounts() {
        viewModelScope.launch {
            try {
                val professionalId = authStateManager.getCurrentUserId()
                
                if (professionalId == null) return@launch
                
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    val userOffers = offers.filter { it.professionalId == professionalId }
                    
                    val counts = mapOf(
                        "PENDING" to userOffers.count { it.status == "PENDING" },
                        "ACCEPTED" to userOffers.count { it.status == "ACCEPTED" },
                        "REJECTED" to userOffers.count { it.status == "REJECTED" },
                        "IN_PROGRESS" to userOffers.count { it.status == "IN_PROGRESS" },
                        "COMPLETED" to userOffers.count { it.status == "COMPLETED" }
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        proposalCounts = counts
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cargar contadores"
                )
            }
        }
    }
    
    // Obtener propuesta por ID
    fun loadProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
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

    fun updateFilter(filter: ProposalFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadProposals()
    }

    // Retirar propuesta
    fun withdrawProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = supabaseDatabase.delete("Offer", proposalId)
                
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposalWithdrawn = true,
                        errorMessage = null
                    )
                    // Recargar propuestas
                    loadProposals()
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
    
    // Mapper helper
    private fun mapSupabaseToProposal(offer: SupabaseOffer): Proposal {
        return Proposal(
            id = offer.id,
            jobId = offer.postingId,
            jobTitle = "", // Se obtiene por separado si es necesario
            clientId = "", // Se obtiene por separado
            clientName = "", // Se obtiene por separado
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

    // Mapper mejorado que incluye información del trabajo y cliente
    private fun mapSupabaseToProposalWithDetails(
        offer: SupabaseOffer,
        job: SupabaseServicePosting?,
        client: SupabaseUser?
    ): Proposal {
        return Proposal(
            id = offer.id,
            jobId = offer.postingId,
            jobTitle = job?.title ?: "Trabajo no disponible",
            clientId = job?.clientId ?: "",
            clientName = client?.name ?: "Cliente",
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
}

data class ProposalsUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val proposal: Proposal? = null,
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val errorMessage: String? = null,
    val proposalWithdrawn: Boolean = false,
    val proposalCounts: Map<String, Int> = emptyMap()
)