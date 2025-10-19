package com.thecodefather.untigrito.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseStorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

        viewModelScope.launch {
            try {
                Timber.d("🔐 IDENTITY_VERIFICATION_START - Verificando identidad...")
                
                // Obtener el ID del usuario actual desde Supabase Auth
                val currentUserId = supabaseAuth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                Timber.d("🔐 IDENTITY_VERIFICATION - User ID: $currentUserId")

                // 1. Subir imagen a Supabase Storage
                Timber.d("📤 STORAGE - Subiendo imagen de cédula...")
                val uploadResult = supabaseStorageService.uploadIdentityDocument(
                    currentUserId, 
                    cedulaImageUri, 
                    context
                )

                uploadResult.onSuccess { imageUrl ->
                    Timber.d("✅ STORAGE - Imagen subida exitosamente: $imageUrl")
                    
                    // 2. Actualizar el usuario en Supabase con la URL de la imagen
                    val updatedFields = mapOf(
                        "role" to "PROFESSIONAL",
                        "isIDVerified" to true,
                        "emailVerificationToken" to imageUrl  // Usar campo temporal para URL de cédula
                    )

                    val result = postgrest.from("User")
                        .update(updatedFields) {
                            filter {
                                eq("id", currentUserId)
                            }
                        }
                        .decodeSingleOrNull<Map<String, Any>>()

                    if (result != null) {
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
                    Timber.e(exception, "❌ STORAGE - Error subiendo imagen")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al subir la imagen: ${exception.message}"
                    )
                }

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
