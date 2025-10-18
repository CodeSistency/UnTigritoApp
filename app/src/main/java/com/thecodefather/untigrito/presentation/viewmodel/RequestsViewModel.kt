package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.repository.ClientRepository

/**
 * ViewModel for RequestsScreen
 * Manages client requests filtered by status
 */
@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl
) : ViewModel() {

    private val _pendingRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    private val _activeRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val activeRequests = _activeRequests.asStateFlow()

    private val _completedRequests = MutableStateFlow<List<ClientRequest>>(emptyList())
    val completedRequests = _completedRequests.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _currentUserId = MutableStateFlow("current_user_id")

    init {
        loadRequests()
    }

    private fun loadRequests() {
        _loading.value = true
        viewModelScope.launch {
            try {
                repository.getClientRequestsByClient(_currentUserId.value).collect { requests ->
                    _pendingRequests.value = requests.filter { it.status == ClientRequest.STATUS_PENDING }
                    _activeRequests.value = requests.filter { it.status == ClientRequest.STATUS_ACCEPTED }
                    _completedRequests.value = requests.filter { 
                        it.status == ClientRequest.STATUS_REJECTED || it.status == ClientRequest.STATUS_CANCELLED
                    }
                }
            } catch (e: Exception) {
                // Error handling
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
                // Error handling
            }
        }
    }
}
