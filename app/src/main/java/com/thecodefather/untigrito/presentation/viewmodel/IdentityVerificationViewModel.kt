package com.thecodefather.untigrito.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseStorageService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel para el flujo de verificación de identidad
 * Maneja la actualización del rol de usuario a PROFESSIONAL en Supabase
 */
@HiltViewModel
class IdentityVerificationViewModel @Inject constructor(
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val supabaseStorageService: SupabaseStorageService,
    private val postgrest: Postgrest,
    private val supabaseAuth: Auth,
    private val authStateManager: AuthStateManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentityVerificationUiState())
    val uiState: StateFlow<IdentityVerificationUiState> = _uiState.asStateFlow()

    /**
     * Verifica la identidad y actualiza el rol del usuario a PROFESSIONAL
     * @param cedulaImageUri URI de la imagen de la cédula
     */
    fun verifyIdentityAndUpgrade(cedulaImageUri: Uri) {
        if (cedulaImageUri == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Debes seleccionar una imagen de tu cédula"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        Timber.d("🔄 UI_STATE - Loading iniciado: ${_uiState.value.isLoading}")

        viewModelScope.launch {
            try {
                // Obtener el ID del usuario actual desde AuthStateManager
                Timber.d("🔐 AUTH - Verificando usuario autenticado...")
                val currentUserId = authStateManager.getCurrentUserId()
                Timber.d("🔐 AUTH - Current user ID: $currentUserId")
                
                if (currentUserId == null) {
                    Timber.w("⚠️ AUTH - Usuario no autenticado")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado. Por favor, inicia sesión nuevamente."
                    )
                    Timber.d("🔄 UI_STATE - Loading detenido (no auth): ${_uiState.value.isLoading}")
                    return@launch
                }
                
                // Timeout de 30 segundos para evitar cuelgues
                withTimeout(30000) {
                Timber.d("🔐 IDENTITY_VERIFICATION_START - Verificando identidad...")
                Timber.d("🔐 IDENTITY_VERIFICATION - User ID: $currentUserId")

                // 1. Actualizar el usuario a PROFESSIONAL (sin subir imagen)
                Timber.d("📤 DATABASE - Actualizando usuario a PROFESSIONAL...")
                
                // Crear un objeto SupabaseUser con los campos actualizados
                val updatedUser = SupabaseUser(
                    id = currentUserId,
                    role = "PROFESSIONAL",
                    isIDVerified = true
                )
                
                // Usar el método genérico update del SupabaseDatabaseService
                val result = supabaseDatabaseService.update("User", currentUserId, updatedUser)
                
                result.onSuccess { updatedUserResult ->
                    if (updatedUserResult != null) {
                        Timber.d("✅ IDENTITY_VERIFICATION_SUCCESS - Usuario actualizado a PROFESSIONAL")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            verificationSuccess = true,
                            errorMessage = null
                        )
                    } else {
                        Timber.w("⚠️ IDENTITY_VERIFICATION_FAILED - No se pudo actualizar el usuario")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "No se pudo actualizar tu perfil. Inténtalo de nuevo."
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ IDENTITY_VERIFICATION_ERROR - Error en actualización")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar tu perfil: ${exception.message}"
                    )
                }
                } // Cerrar withTimeout

            } catch (exception: Exception) {
                Timber.e(exception, "❌ IDENTITY_VERIFICATION_ERROR")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error al verificar tu identidad"
                )
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Resetea el estado de verificación exitosa
     */
    fun resetVerificationSuccess() {
        _uiState.value = _uiState.value.copy(verificationSuccess = false)
    }
}

/**
 * Estado de la UI para la verificación de identidad
 */
data class IdentityVerificationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val verificationSuccess: Boolean = false
)
