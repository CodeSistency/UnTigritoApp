package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfessionalViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val postgrest: Postgrest,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _professionalProfile = MutableStateFlow<SupabaseProfessionalProfile?>(null)
    val professionalProfile: StateFlow<SupabaseProfessionalProfile?> = _professionalProfile.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    init {
        loadCurrentUser()
    }

    /**
     * Carga el usuario actual desde AuthStateManager
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authStateManager.getCurrentUser()
                _currentUser.value = user
                
                if (user != null) {
                    loadProfessionalProfile(user.id)
                    loadUserBalance(user.id)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading current user")
                _errorMessage.value = e.message ?: "Error al cargar usuario"
            }
        }
    }

    /**
     * Carga el perfil profesional desde Supabase
     */
    fun loadProfessionalProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔍 PROFESSIONAL VIEWMODEL - Loading professional profile for user: $userId")
                
                val result = postgrest.from("ProfessionalProfile")
                    .select {
                        filter {
                            eq("userId", userId)
                        }
                    }
                    .decodeSingleOrNull<SupabaseProfessionalProfile>()
                
                if (result != null) {
                    _professionalProfile.value = result
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile loaded successfully")
                } else {
                    Timber.w("⚠️ PROFESSIONAL VIEWMODEL - No profile found for user: $userId")
                    _errorMessage.value = "No se encontró perfil profesional"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error loading profile")
                _errorMessage.value = e.message ?: "Error al cargar perfil profesional"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga el saldo del usuario desde la tabla User
     */
    fun loadUserBalance(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("💰 PROFESSIONAL VIEWMODEL - Loading balance for user: $userId")
                
                val result = postgrest.from("User")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<SupabaseUser>()
                
                if (result != null) {
                    _balance.value = result.balance
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Balance loaded: ${result.balance}")
                } else {
                    Timber.w("⚠️ PROFESSIONAL VIEWMODEL - User not found for balance")
                    _balance.value = 0.0
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error loading balance")
                _balance.value = 0.0
            }
        }
    }

    /**
     * Actualiza el perfil profesional
     */
    fun updateProfile(profile: SupabaseProfessionalProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("💾 PROFESSIONAL VIEWMODEL - Updating profile: ${profile.id}")
                
                val result = supabaseDatabase.update(
                    "ProfessionalProfile",
                    profile.id,
                    profile
                )
                
                result.onSuccess { updatedProfile ->
                    _professionalProfile.value = updatedProfile
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile updated successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                    _errorMessage.value = exception.message ?: "Error al actualizar perfil"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                _errorMessage.value = e.message ?: "Error al actualizar perfil"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Realiza logout del usuario
     */
    fun logout() {
        viewModelScope.launch {
            try {
                Timber.d("🚪 PROFESSIONAL VIEWMODEL - Logging out user")
                
                authStateManager.clearAuthState()
                _currentUser.value = null
                _professionalProfile.value = null
                _balance.value = 0.0
                _logoutSuccess.value = true
                
                Timber.d("✅ PROFESSIONAL VIEWMODEL - Logout successful")
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error during logout")
                _errorMessage.value = e.message ?: "Error al cerrar sesión"
            }
        }
    }

    /**
     * Limpia los errores
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Resetea el estado de logout
     */
    fun resetLogoutSuccess() {
        _logoutSuccess.value = false
    }
}
