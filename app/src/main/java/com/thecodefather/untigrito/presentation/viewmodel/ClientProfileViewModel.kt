package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.toClientUser
import com.thecodefather.untigrito.domain.model.toSupabaseUser
import io.github.jan.supabase.gotrue.Auth
import timber.log.Timber

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl,
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val supabaseAuth: Auth
) : ViewModel() {

    private val _user = MutableStateFlow<ClientUser?>(null)
    val user = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess = _logoutSuccess.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val currentUserId = supabaseAuth.currentUserOrNull()?.id
                if (currentUserId != null) {
                    supabaseDatabaseService.getById<SupabaseUser>("users", currentUserId)
                        .onSuccess { supabaseUser ->
                            _user.value = supabaseUser?.toClientUser()
                            Timber.d("User profile loaded successfully")
                        }
                        .onFailure { exception ->
                            Timber.e(exception, "Error loading user profile from Supabase")
                            _error.value = "Error loading profile: ${exception.message}"
                        }
                } else {
                    _error.value = "Usuario no autenticado"
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception loading profile")
                _error.value = "Error loading profile: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                supabaseAuth.signOut()
                _logoutSuccess.value = true
                _user.value = null
                Timber.d("User logged out successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error logging out")
                _error.value = "Error logging out: ${e.message}"
            }
        }
    }

    fun updateProfile(user: ClientUser) {
        viewModelScope.launch {
            try {
                _loading.value = true
                supabaseDatabaseService.update("users", user.id, user.toSupabaseUser())
                    .onSuccess {
                        _user.value = user
                        Timber.d("Profile updated successfully")
                    }
                    .onFailure { exception ->
                        Timber.e(exception, "Error updating profile")
                        _error.value = "Error updating profile: ${exception.message}"
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception updating profile")
                _error.value = "Error updating profile: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun requestVerification() {
        viewModelScope.launch {
            try {
                _user.value?.let { currentUser ->
                    val updatedUser = currentUser.copy(isVerified = true)
                    updateProfile(updatedUser)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error requesting verification")
                _error.value = "Error requesting verification: ${e.message}"
            }
        }
    }
}
