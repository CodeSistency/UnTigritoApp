package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.data.datasource.remote.SupabaseOffer
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.toClientRequest
import timber.log.Timber

/**
 * ViewModel for RequestsScreen
 * Manages client requests (service_postings + offers) filtered by status
 */
@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl,
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _pendingRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    private val _activeRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val activeRequests = _activeRequests.asStateFlow()

    private val _completedRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val completedRequests = _completedRequests.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                if (currentUserId == null) {
                    _error.value = "Usuario no autenticado"
                    _loading.value = false
                    return@launch
                }
                
                // Load service_postings created by client
                supabaseDatabaseService.findBy<SupabaseServicePosting>(
                    "service_postings",
                    "clientId",
                    currentUserId
                ).onSuccess { postings ->
                    Timber.d("Found ${postings.size} service postings for client")
                    
                    // For each posting, load related offers
                    val allRequests = mutableListOf<ClientRequest>()
                    postings.forEach { posting ->
                        supabaseDatabaseService.findBy<SupabaseOffer>(
                            "offers",
                            "postingId",
                            posting.id
                        ).onSuccess { offers ->
                            Timber.d("Found ${offers.size} offers for posting ${posting.id}")
                            allRequests.addAll(offers.map { it.toClientRequest() })
                        }
                    }
                    
                    // Filter by status
                    _pendingRequests.value = allRequests.filter { it.status == ClientRequest.STATUS_PENDING }
                    _activeRequests.value = allRequests.filter { it.status == ClientRequest.STATUS_ACCEPTED }
                    _completedRequests.value = allRequests.filter { 
                        it.status == ClientRequest.STATUS_REJECTED || it.status == ClientRequest.STATUS_CANCELLED
                    }
                    
                    Timber.d("Loaded requests - Pending: ${_pendingRequests.value.size}, Active: ${_activeRequests.value.size}, Completed: ${_completedRequests.value.size}")
                }.onFailure { exception ->
                    Timber.e(exception, "Error loading service postings")
                    _error.value = "Error loading requests: ${exception.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception loading requests")
                _error.value = "Error loading requests: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateRequestStatus(requestId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateClientRequestStatus(requestId, newStatus)
                loadRequests()
            } catch (e: Exception) {
                Timber.e(e, "Error updating request status")
                _error.value = "Error updating request: ${e.message}"
            }
        }
    }
}
