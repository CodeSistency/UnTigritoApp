package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
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
import com.thecodefather.untigrito.domain.model.toClientUser
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
                // Load user data from Supabase User table
                loadUserFromDatabase()

                // Load services offered by professionals (NOT service_postings)
                supabaseDatabaseService.getAllOrdered<SupabaseService>(
                    "ProfessionalService",
                    "createdAt",
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
                    "ProfessionalProfile",
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

    /**
     * Load user data from Supabase User table using AuthStateManager
     */
    private fun loadUserFromDatabase() {
        viewModelScope.launch {
            try {
                // Get current user ID from AuthStateManager
                val currentUserId = authStateManager.getCurrentUserId()

                if (currentUserId == null) {
                    Timber.w("No user authenticated in AuthStateManager")
                    _error.value = "Usuario no autenticado"
                    return@launch
                }

                Timber.d("Loading user data from Supabase for userId: $currentUserId")

                // Fetch user from Supabase User table
                val result = supabaseDatabaseService.getById<SupabaseUser>("User", currentUserId)

                result.onSuccess { supabaseUser ->
                    if (supabaseUser != null) {
                        // Convert SupabaseUser to ClientUser
                        val clientUser = supabaseUser.toClientUser()
                        _user.value = clientUser

                        // Guardar clientUser en la base de datos local
                        Timber.d("💾 Saving clientUser to local database...")
                        try {
                            repository.saveUser(clientUser)
                            Timber.d("✅ ClientUser saved to local database successfully")
                        } catch (e: Exception) {
                            Timber.e(e, "❌ Error saving clientUser to local database")
                        }

                        Timber.d("✅ User loaded successfully: ${clientUser.name} (${clientUser.email})")
                        Timber.d("   Balance: ${clientUser.balance}")
                        Timber.d("   Role: ${clientUser.role}")
                        Timber.d("   Verified: ${clientUser.isVerified}")

                        _error.value = null
                    } else {
                        Timber.w("User not found in database with id: $currentUserId")
                        _error.value = "Usuario no encontrado en la base de datos"
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Error loading user from database")
                    _error.value = "Error al cargar datos del usuario: ${exception.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error loading user from database")
                _error.value = "Error inesperado: ${e.message}"
            }
        }
    }

    /**
     * Public method to manually refresh user data
     */
    fun refreshUserData() {
        loadUserFromDatabase()
    }
}
