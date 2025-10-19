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
    
    // Cargar trabajo para contexto
    fun loadJob(jobId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = supabaseDatabase.getById<SupabaseServicePosting>("ServicePosting", jobId)
                
                result.onSuccess { posting ->
                    if (posting != null) {
                        val job = mapSupabaseToJob(posting)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            job = job,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Trabajo no encontrado"
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
                
                val proposal = SupabaseOffer(
                    id = UUID.randomUUID().toString(),
                    postingId = jobId,
                    professionalId = professionalId,
                    price = proposedPrice,
                    proposedPrice = proposedPrice,
                    message = description,
                    status = "PENDING",
                    createdAt = System.currentTimeMillis().toString()
                )
                
                val result = supabaseDatabase.insert("Offer", proposal)
                
                result.onSuccess { insertedProposal ->
                    if (insertedProposal != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            proposalCreated = true,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al crear propuesta"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear propuesta"
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
    private fun mapSupabaseToJob(posting: SupabaseServicePosting): Job {
        return Job(
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
                else -> JobStatus.OPEN
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
                    null
                }
            },
            createdAt = try {
                Date(posting.createdAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            isRecommended = false,
            isFavorite = false,
            urgency = UrgencyLevel.NORMAL,
            estimatedDuration = null,
            requiredSkills = emptyList(),
            attachments = emptyList()
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class CreateProposalUiState(
    val job: Job? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val proposalCreated: Boolean = false
)

