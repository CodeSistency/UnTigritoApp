package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.ProfessionalService
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.toProfessionalService
import com.thecodefather.untigrito.domain.model.toProfessional
import timber.log.Timber

/**
 * ViewModel for ClientHomeScreen
 * Manages home screen state: user balance, services offered by professionals, top professionals
 */
@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl,
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _user = MutableStateFlow<ClientUser?>(null)
    val user = _user.asStateFlow()

    private val _services = MutableStateFlow<List<ProfessionalService>>(emptyList())
    val services = _services.asStateFlow()

    private val _topProfessionals = MutableStateFlow<List<Professional>>(emptyList())
    val topProfessionals = _topProfessionals.asStateFlow()

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
                val currentUserId = authStateManager.getCurrentUserId()
                if (currentUserId != null) {
                    repository.getUserById(currentUserId).collect { user ->
                        _user.value = user
                    }
                } else {
                    Timber.w("No user authenticated")
                    _error.value = "Usuario no autenticado"
                }

                // Load services offered by professionals (NOT service_postings)
                supabaseDatabaseService.getAllOrdered<SupabaseService>(
                    "professional_services",
                    "created_at",
                    false
                ).onSuccess { services ->
                    _services.value = services
                        .filter { it.isActive }
                        .take(10)
                        .map { it.toProfessionalService() }
                    Timber.d("Loaded ${_services.value.size} professional services")
                }.onFailure { exception ->
                    Timber.e(exception, "Error loading professional services")
                    _error.value = "Error loading services: ${exception.message}"
                }

                // Load top professionals
                supabaseDatabaseService.getAllOrdered<SupabaseProfessionalProfile>(
                    "professional_profiles",
                    "rating",
                    false
                ).onSuccess { profiles ->
                    _topProfessionals.value = profiles
                        .filter { it.rating != null && it.rating!! > 4.0 }
                        .take(5)
                        .map { it.toProfessional() }
                    Timber.d("Loaded ${_topProfessionals.value.size} top professionals")
                }.onFailure { exception ->
                    Timber.e(exception, "Error loading professionals")
                }

                _error.value = null
            } catch (e: Exception) {
                Timber.e(e, "Error loading home data")
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
