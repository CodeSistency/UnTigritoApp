package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseOffer
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateProposalViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateProposalUiState())
    val uiState: StateFlow<CreateProposalUiState> = _uiState.asStateFlow()

    init {
        Timber.d("CreateProposalViewModel initialized")
    }

    // Cargar trabajo para contexto
    fun loadJob(jobId: String) {
        viewModelScope.launch {
            Timber.d("loadJob() called with jobId: $jobId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ServicePosting")
                Timber.d("OPERATION: getById()")
                Timber.d("Fetching job with id: $jobId")
                val result = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", jobId)
                
                result.onSuccess { posting ->
                    if (posting != null) {
                        Timber.d("Job found: title=${posting.title}, budget=${posting.budget}, status=${posting.status}")
                        Timber.d("Job details: description=${posting.description}, categoryId=${posting.categoryId}")
                        Timber.d("Client info: clientId=${posting.clientId}")

                        val job = mapSupabaseToJob(posting)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            job = job,
                            errorMessage = null
                        )
                        Timber.d("Job loaded successfully for proposal creation")
                        Timber.d("==========================================")
                    } else {
                        Timber.w("Job not found with id: $jobId")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Trabajo no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load job from table 'ServicePosting'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadJob()")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    // Crear propuesta directamente en Supabase
    fun createProposal(
        jobId: String,
        proposedPrice: Double,
        description: String,
        estimatedDuration: Int,
        includesMaterials: Boolean,
        offersWarranty: Boolean,
        termsAndConditions: String?
    ) {
        viewModelScope.launch {
            Timber.d("createProposal() called")
            Timber.d("Parameters: jobId=$jobId, proposedPrice=$proposedPrice, estimatedDuration=$estimatedDuration")
            Timber.d("Additional: includesMaterials=$includesMaterials, offersWarranty=$offersWarranty")
            Timber.d("Description length: ${description.length} characters")

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                Timber.d("Current professional ID: $professionalId")
                
                if (professionalId == null) {
                    Timber.w("User not authenticated, cannot create proposal")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }

                val proposalId = UUID.randomUUID().toString()
                Timber.d("Generated proposal UUID: $proposalId")

                val proposal = SupabaseOffer(
                    id = proposalId,
                    postingId = jobId,
                    professionalId = professionalId,
                    price = proposedPrice,
                    proposedPrice = proposedPrice,
                    message = description,
                    status = "PENDING",
                    createdAt = "Sun Oct 19 2025 06:44:33 GMT+0000"
//                    createdAt = System.currentTimeMillis().toString()
                )

                Timber.d("Creating SupabaseOffer object:")
                Timber.d("  - id: ${proposal.id}")
                Timber.d("  - postingId: ${proposal.postingId}")
                Timber.d("  - professionalId: ${proposal.professionalId}")
                Timber.d("  - price: ${proposal.price}")
                Timber.d("  - proposedPrice: ${proposal.proposedPrice}")
                Timber.d("  - message: ${proposal.message}")
                Timber.d("  - status: ${proposal.status}")
                Timber.d("  - createdAt: ${proposal.createdAt}")

                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: Offer")
                Timber.d("OPERATION: insert()")
                Timber.d("Inserting new offer into database...")
                val result = supabaseDatabase.insert("Offer", proposal)
                
                result.onSuccess { insertedProposal ->
                    if (insertedProposal != null) {
                        Timber.d("Offer inserted successfully into table 'Offer'")
                        Timber.d("Inserted offer ID: ${insertedProposal.id}")
                        Timber.d("Inserted offer status: ${insertedProposal.status}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposalCreated = true,
                            errorMessage = null
                        )
                        Timber.d("Proposal creation completed successfully")
                        Timber.d("==========================================")
                    } else {
                        Timber.w("Insert operation succeeded but returned null")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al crear propuesta"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to insert offer into table 'Offer'")
                    Timber.d("Error details: ${exception.message}")
                    Timber.d("Error stack trace: ${exception.stackTraceToString()}")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear propuesta"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in createProposal()")
                Timber.d("Error details: ${e.message}")
                Timber.d("Error stack trace: ${e.stackTraceToString()}")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Mapper helper
    private fun mapSupabaseToJob(posting: SupabaseServicePosting): Job {
        Timber.d("Mapping SupabaseServicePosting to Job: id=${posting.id}")

        try {
            val job = Job(
                id = posting.id,
                title = posting.title,
                description = posting.description,
                category = posting.categoryId,
                budget = posting.budget ?: 0.0,
                clientId = posting.clientId,
                clientName = "",
                clientAvatar = null,
                status = when (posting.status) {
                    "OPEN" -> JobStatus.OPEN
                    "IN_PROGRESS" -> JobStatus.IN_PROGRESS
                    "COMPLETED" -> JobStatus.COMPLETED
                    "CANCELLED" -> JobStatus.CANCELLED
                    else -> {
                        Timber.w("Unknown job status: ${posting.status}, defaulting to OPEN")
                        JobStatus.OPEN
                    }
                },
                location = posting.address?.let { address ->
                    Location(
                        address = address,
                        city = "",
                        state = "",
                        coordinates = if (posting.lat != null && posting.lng != null) {
                            Coordinates(posting.lat, posting.lng)
                        } else null
                    )
                },
                deadline = posting.requiredTo?.let {
                    try {
                        Date(it)
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to parse deadline: $it")
                        null
                    }
                },
                createdAt = try {
                    Date(posting.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${posting.createdAt}")
                    Date()
                },
                isRecommended = false,
                isFavorite = false,
                urgency = UrgencyLevel.NORMAL,
                estimatedDuration = null,
                requiredSkills = emptyList(),
                attachments = emptyList()
            )

            Timber.d("Successfully mapped job: title=${job.title}, budget=${job.budget}, status=${job.status}")
            return job
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseServicePosting to Job")
            throw e
        }
    }
    
    fun clearError() {
        Timber.d("clearError() called")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class CreateProposalUiState(
    val job: Job? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val proposalCreated: Boolean = false
)

