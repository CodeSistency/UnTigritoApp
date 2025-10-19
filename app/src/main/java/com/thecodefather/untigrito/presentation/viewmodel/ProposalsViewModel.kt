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
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProposalsViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalsUiState())
    val uiState: StateFlow<ProposalsUiState> = _uiState.asStateFlow()

    init {
        Timber.d("ProposalsViewModel initialized")
    }

    // Cargar propuestas directamente desde Supabase
    fun loadProposals() {
        viewModelScope.launch {
            Timber.d("loadProposals() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUser()?.id
                Timber.d("Current professional ID: $professionalId")
                Timber.d("Current professional : ${authStateManager.getCurrentUser()}")

                if (professionalId == null) {
                    Timber.w("User not authenticated")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Consulta directa a Offer
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: getAll()")
                Timber.d("Fetching all offers from Supabase...")
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    Timber.d("Successfully fetched ${offers.size} offers from table 'Offer'")
                    Timber.d("Raw offers data: $offers")

                    val userOffers = offers.filter { it.professionalId == professionalId }
                    Timber.d("Filtered to ${userOffers.size} offers for professional: $professionalId")

                    val proposals = userOffers.map { offer ->
                        Timber.d("Mapping offer: id=${offer.id}, postingId=${offer.postingId}, status=${offer.status}, price=${offer.price}")
                        mapSupabaseToProposal(offer)
                    }
                    Timber.d("Mapped ${proposals.size} proposals successfully")

                    val filteredProposals = when (_uiState.value.selectedFilter) {
                        ProposalFilter.OPEN -> {
                            Timber.d("Applying OPEN filter (PENDING status)")
                            proposals.filter { it.status == ProposalStatus.PENDING }
                        }

                        ProposalFilter.IN_PROGRESS -> {
                            Timber.d("Applying IN_PROGRESS filter (IN_PROGRESS or ACCEPTED status)")
                            proposals.filter {
                                it.status == ProposalStatus.IN_PROGRESS ||
                                        it.status == ProposalStatus.ACCEPTED
                            }
                        }
                        ProposalFilter.COMPLETED -> {
                            Timber.d("Applying COMPLETED filter")
                            proposals.filter { it.status == ProposalStatus.COMPLETED }
                        }

                        ProposalFilter.REJECTED -> {
                            Timber.d("Applying REJECTED filter")
                            proposals.filter { it.status == ProposalStatus.REJECTED }
                        }

                        ProposalFilter.HISTORY -> {
                            Timber.d("Applying HISTORY filter (COMPLETED, REJECTED, CANCELLED)")
                            proposals.filter {
                                it.status == ProposalStatus.COMPLETED ||
                                        it.status == ProposalStatus.REJECTED ||
                                        it.status == ProposalStatus.CANCELLED
                            }
                        }
                    }
                    Timber.d("After filter: ${filteredProposals.size} proposals")

                    val sortedProposals = filteredProposals.sortedByDescending { it.createdAt }
                    Timber.d("Final proposals count: ${sortedProposals.size}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposals = sortedProposals,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully")
                    Timber.d("==========================================")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load proposals from table 'Offer'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar propuestas"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProposals()")
                Timber.d("==========================================")
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
            Timber.d("loadProposalsWithJobInfo() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val professionalId = authStateManager.getCurrentUserId()
                Timber.d("Current professional ID: $professionalId")
                
                if (professionalId == null) {
                    Timber.w("User not authenticated")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }

                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: getAll()")
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    Timber.d("Successfully fetched ${offers.size} offers from table 'Offer'")

                    val userOffers = offers.filter { it.professionalId == professionalId }
                    Timber.d("Filtered to ${userOffers.size} offers for professional: $professionalId")

                    val proposalsWithInfo = userOffers.mapIndexed { index, offer ->
                        Timber.d("Processing offer ${index + 1}/${userOffers.size}: id=${offer.id}, postingId=${offer.postingId}")

                        // Obtener información del trabajo asociado
                        Timber.d("========== SUPABASE CONNECTION ==========")
                        Timber.d("TABLE: ServicePosting")
                        Timber.d("OPERATION: getById()")
                        Timber.d("Fetching job with postingId: ${offer.postingId}")
                        val jobResult = supabaseDatabase.getById<SupabaseServicePosting>(
                            "ServicePosting",
                            offer.postingId
                        )
                        val job = jobResult.getOrNull()

                        if (job != null) {
                            Timber.d("Job found: title=${job.title}, clientId=${job.clientId}")
                            
                            // Obtener información del cliente
                            Timber.d("========== SUPABASE CONNECTION ==========")
                            Timber.d("TABLE: User")
                            Timber.d("OPERATION: getById()")
                            Timber.d("Fetching client with userId: ${job.clientId}")
                            val clientResult =
                                supabaseDatabase.getById<SupabaseUser>("User", job.clientId)
                            val client = clientResult.getOrNull()

                            if (client != null) {
                                Timber.d("Client found: name=${client.name}, id=${client.id}")
                            } else {
                                Timber.w("Client not found for userId: ${job.clientId}")
                            }
                            
                            mapSupabaseToProposalWithDetails(offer, job, client)
                        } else {
                            Timber.w("Job not found for postingId: ${offer.postingId}")
                            mapSupabaseToProposalWithDetails(offer, null, null)
                        }
                    }
                    Timber.d("Mapped ${proposalsWithInfo.size} proposals with job info")

                    val filteredProposals = when (_uiState.value.selectedFilter) {
                        ProposalFilter.OPEN -> {
                            Timber.d("Applying OPEN filter")
                            proposalsWithInfo.filter { it.status == ProposalStatus.PENDING }
                        }

                        ProposalFilter.IN_PROGRESS -> {
                            Timber.d("Applying IN_PROGRESS filter")
                            proposalsWithInfo.filter {
                                it.status == ProposalStatus.IN_PROGRESS ||
                                        it.status == ProposalStatus.ACCEPTED
                            }
                        }
                        ProposalFilter.COMPLETED -> {
                            Timber.d("Applying COMPLETED filter")
                            proposalsWithInfo.filter { it.status == ProposalStatus.COMPLETED }
                        }

                        ProposalFilter.REJECTED -> {
                            Timber.d("Applying REJECTED filter")
                            proposalsWithInfo.filter { it.status == ProposalStatus.REJECTED }
                        }

                        ProposalFilter.HISTORY -> {
                            Timber.d("Applying HISTORY filter")
                            proposalsWithInfo.filter {
                                it.status == ProposalStatus.COMPLETED ||
                                        it.status == ProposalStatus.REJECTED ||
                                        it.status == ProposalStatus.CANCELLED
                            }
                        }
                    }
                    Timber.d("After filter: ${filteredProposals.size} proposals")

                    val sortedProposals = filteredProposals.sortedByDescending { it.createdAt }
                    Timber.d("Final proposals count: ${sortedProposals.size}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposals = sortedProposals,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully with job info")
                    Timber.d("==========================================")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load proposals with job info from table 'Offer'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar propuestas"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProposalsWithJobInfo()")
                Timber.d("==========================================")
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
            Timber.d("loadProposalWithDetails() called with proposalId: $proposalId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: getById()")
                Timber.d("Fetching offer with id: $proposalId")
                val result = supabaseDatabase.getById<SupabaseOffer>("Offer", proposalId)
                
                result.onSuccess { offer ->
                    if (offer != null) {
                        Timber.d("Offer found: id=${offer.id}, postingId=${offer.postingId}, status=${offer.status}")

                        // Obtener información del trabajo
                        Timber.d("========== SUPABASE CONNECTION ==========")
                        Timber.d("TABLE: ServicePosting")
                        Timber.d("OPERATION: getById()")
                        Timber.d("Fetching job with postingId: ${offer.postingId}")
                        val jobResult = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", offer.postingId)
                        val job = jobResult.getOrNull()

                        if (job != null) {
                            Timber.d("Job found: title=${job.title}, clientId=${job.clientId}")

                            // Obtener información del cliente
                            Timber.d("========== SUPABASE CONNECTION ==========")
                            Timber.d("TABLE: User")
                            Timber.d("OPERATION: getById()")
                            Timber.d("Fetching client with userId: ${job.clientId}")
                            val clientResult =
                                supabaseDatabase.getById<SupabaseUser>("User", job.clientId)
                            val client = clientResult.getOrNull()

                            if (client != null) {
                                Timber.d("Client found: name=${client.name}, id=${client.id}")
                            } else {
                                Timber.w("Client not found for userId: ${job.clientId}")
                            }

                            val proposal = mapSupabaseToProposalWithDetails(offer, job, client)

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                proposal = proposal,
                                errorMessage = null
                            )
                            Timber.d("Proposal details loaded successfully")
                        } else {
                            Timber.w("Job not found for postingId: ${offer.postingId}")
                            val proposal = mapSupabaseToProposalWithDetails(offer, null, null)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                proposal = proposal,
                                errorMessage = null
                            )
                        }
                        Timber.d("==========================================")
                    } else {
                        Timber.w("Offer not found with id: $proposalId")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Propuesta no encontrada"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load proposal details from table 'Offer'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProposalWithDetails()")
                Timber.d("==========================================")
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
            Timber.d("loadProposalCounts() called")
            try {
                val professionalId = authStateManager.getCurrentUserId()
                Timber.d("Current professional ID: $professionalId")

                if (professionalId == null) {
                    Timber.w("User not authenticated, cannot load proposal counts")
                    return@launch
                }

                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: getAll()")
                Timber.d("Fetching all offers to count by status...")
                val result = supabaseDatabase.getAll<SupabaseOffer>("Offer")
                
                result.onSuccess { offers ->
                    Timber.d("Successfully fetched ${offers.size} offers from table 'Offer'")

                    val userOffers = offers.filter { it.professionalId == professionalId }
                    Timber.d("Filtered to ${userOffers.size} offers for professional: $professionalId")
                    
                    val counts = mapOf(
                        "PENDING" to userOffers.count { it.status == "PENDING" },
                        "ACCEPTED" to userOffers.count { it.status == "ACCEPTED" },
                        "REJECTED" to userOffers.count { it.status == "REJECTED" },
                        "IN_PROGRESS" to userOffers.count { it.status == "IN_PROGRESS" },
                        "COMPLETED" to userOffers.count { it.status == "COMPLETED" }
                    )

                    Timber.d("Proposal counts by status: $counts")

                    _uiState.value = _uiState.value.copy(
                        proposalCounts = counts
                    )
                    Timber.d("Proposal counts updated successfully")
                    Timber.d("==========================================")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load proposal counts from table 'Offer'")
                    Timber.d("==========================================")
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProposalCounts()")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cargar contadores"
                )
            }
        }
    }
    
    // Obtener propuesta por ID
    fun loadProposal(proposalId: String) {
        viewModelScope.launch {
            Timber.d("loadProposal() called with proposalId: $proposalId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: getById()")
                Timber.d("Fetching offer with id: $proposalId")
                val result = supabaseDatabase.getById<SupabaseOffer>("Offer", proposalId)
                
                result.onSuccess { offer ->
                    if (offer != null) {
                        Timber.d("Offer found: id=${offer.id}, status=${offer.status}, price=${offer.price}")
                        val proposal = mapSupabaseToProposal(offer)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposal = proposal,
                            errorMessage = null
                        )
                        Timber.d("Proposal loaded successfully")
                        Timber.d("==========================================")
                    } else {
                        Timber.w("Offer not found with id: $proposalId")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Propuesta no encontrada"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load proposal from table 'Offer'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProposal()")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun updateFilter(filter: ProposalFilter) {
        Timber.d("updateFilter() called with filter: $filter")
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadProposals()
    }

    // Retirar propuesta
    fun withdrawProposal(proposalId: String) {
        viewModelScope.launch {
            Timber.d("withdrawProposal() called with proposalId: $proposalId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: delete()")
                Timber.d("Deleting offer with id: $proposalId")
                val result = supabaseDatabase.delete("Offer", proposalId)
                
                result.onSuccess {
                    Timber.d("Offer deleted successfully from table 'Offer'")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        proposalWithdrawn = true,
                        errorMessage = null
                    )
                    Timber.d("==========================================")
                    // Recargar propuestas
                    loadProposals()
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to delete offer from table 'Offer'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al retirar propuesta"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in withdrawProposal()")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Mapper helper
    private fun mapSupabaseToProposal(offer: SupabaseOffer): Proposal {
        Timber.d("Mapping SupabaseOffer to Proposal: id=${offer.id}")

        try {
            val proposal = Proposal(
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
                    else -> {
                        Timber.w("Unknown proposal status: ${offer.status}, defaulting to PENDING")
                        ProposalStatus.PENDING
                    }
                },
                createdAt = try {
                    Date(offer.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${offer.createdAt}")
                    Date()
                },
                updatedAt = try {
                    Date(offer.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse updatedAt: ${offer.createdAt}")
                    Date()
                },
                responseMessage = null,
                responseDate = null
            )

            Timber.d("Successfully mapped proposal: jobTitle=${proposal.jobTitle}, status=${proposal.status}")
            return proposal
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseOffer to Proposal")
            throw e
        }
    }

    // Mapper mejorado que incluye información del trabajo y cliente
    private fun mapSupabaseToProposalWithDetails(
        offer: SupabaseOffer,
        job: SupabaseServicePosting?,
        client: SupabaseUser?
    ): Proposal {
        Timber.d("Mapping SupabaseOffer with details: id=${offer.id}, jobTitle=${job?.title}, clientName=${client?.name}")

        try {
            val proposal = Proposal(
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
                    else -> {
                        Timber.w("Unknown proposal status: ${offer.status}, defaulting to PENDING")
                        ProposalStatus.PENDING
                    }
                },
                createdAt = try {
                    Date(offer.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${offer.createdAt}")
                    Date()
                },
                updatedAt = try {
                    Date(offer.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse updatedAt: ${offer.createdAt}")
                    Date()
                },
                responseMessage = null,
                responseDate = null
            )

            Timber.d("Successfully mapped proposal with details: jobTitle=${proposal.jobTitle}, clientName=${proposal.clientName}, status=${proposal.status}")
            return proposal
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseOffer with details to Proposal")
            throw e
        }
    }

    fun clearError() {
        Timber.d("clearError() called")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ProposalsUiState(
    val searchQuery : String = "",
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val proposal: Proposal? = null,
    val selectedFilter: ProposalFilter = ProposalFilter.OPEN,
    val errorMessage: String? = null,
    val proposalWithdrawn: Boolean = false,
    val proposalCounts: Map<String, Int> = emptyMap()
)