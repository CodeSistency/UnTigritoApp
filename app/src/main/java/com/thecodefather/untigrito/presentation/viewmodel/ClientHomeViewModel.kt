package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.repository.ClientRepository

/**
 * ViewModel for ClientHomeScreen
 * Manages home screen state: user balance, services, loading states
 */
@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl
) : ViewModel() {

    private val _user = MutableStateFlow<ClientUser?>(null)
    val user = _user.asStateFlow()

    private val _services = MutableStateFlow<List<ServicePosting>>(emptyList())
    val services = _services.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadInitialData()
    }

    /**
     * Load user and services data on initialization
     */
    private fun loadInitialData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                // Load user data
                repository.getUserById("current_user_id").collect { user ->
                    _user.value = user
                }

                // Load available services
                repository.getServicePostingsByStatus(ServicePosting.STATUS_OPEN).collect { services ->
                    _services.value = services.take(10) // Show top 10
                }

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error loading data: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Refresh all data
     */
    fun refresh() {
        loadInitialData()
    }

    /**
     * Navigate to service detail
     */
    fun selectService(serviceId: String) {
        // Navigation handled in Composable
    }

    /**
     * Navigate to create request
     */
    fun navigateToCreateRequest() {
        // Navigation handled in Composable
    }
}
