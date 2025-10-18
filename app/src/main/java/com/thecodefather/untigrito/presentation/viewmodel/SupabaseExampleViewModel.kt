package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.datasource.remote.SupabaseAuthService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel de ejemplo que demuestra cómo usar Supabase en la aplicación
 * 
 * Este ViewModel muestra patrones comunes de uso:
 * - Autenticación con email/password
 * - Consultas a la base de datos
 * - Manejo de estados con StateFlow
 * - Manejo de errores
 */
@HiltViewModel
class SupabaseExampleViewModel @Inject constructor(
    private val authService: SupabaseAuthService,
    private val databaseService: SupabaseDatabaseService
) : ViewModel() {

    // ========== Estados de UI ==========
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _services = MutableStateFlow<List<SupabaseService>>(emptyList())
    val services: StateFlow<List<SupabaseService>> = _services.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ========== Autenticación ==========

    /**
     * Registra un nuevo usuario con email y contraseña
     */
    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authService.signUpWithEmail(email, password)
                .onSuccess { user ->
                    Timber.d("Usuario registrado: ${user?.email}")
                    _authState.value = AuthState.Authenticated(user)
                    
                    // Actualizar perfil con el nombre
                    user?.let {
                        updateUserProfile(mapOf("name" to name))
                    }
                }
                .onFailure { error ->
                    Timber.e(error, "Error al registrar usuario")
                    _errorMessage.value = "Error al registrar: ${error.message}"
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authService.signInWithEmail(email, password)
                .onSuccess { user ->
                    Timber.d("Usuario autenticado: ${user?.email}")
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { error ->
                    Timber.e(error, "Error al iniciar sesión")
                    _errorMessage.value = "Error al iniciar sesión: ${error.message}"
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            
            authService.signOut()
                .onSuccess {
                    Timber.d("Sesión cerrada")
                    _authState.value = AuthState.Unauthenticated
                    _services.value = emptyList()
                }
                .onFailure { error ->
                    Timber.e(error, "Error al cerrar sesión")
                    _errorMessage.value = "Error al cerrar sesión: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authService.getCurrentUser()
            _authState.value = if (user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Envía un email de recuperación de contraseña
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authService.resetPasswordForEmail(email)
                .onSuccess {
                    Timber.d("Email de recuperación enviado")
                    _errorMessage.value = "Email de recuperación enviado. Revisa tu correo."
                }
                .onFailure { error ->
                    Timber.e(error, "Error al enviar email de recuperación")
                    _errorMessage.value = "Error al enviar email: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Actualiza el perfil del usuario actual
     */
    private fun updateUserProfile(data: Map<String, Any>) {
        viewModelScope.launch {
            authService.updateUser(data)
                .onSuccess { user ->
                    Timber.d("Perfil actualizado")
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { error ->
                    Timber.e(error, "Error al actualizar perfil")
                }
        }
    }

    // ========== Base de Datos ==========

    /**
     * Obtiene todos los servicios desde Supabase
     * Nota: El nombre de la tabla en Supabase debe coincidir con Prisma: "ProfessionalService"
     */
    fun loadServices() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            databaseService.getAll<SupabaseService>("ProfessionalService")
                .onSuccess { servicesList ->
                    Timber.d("Servicios cargados: ${servicesList.size}")
                    _services.value = servicesList
                }
                .onFailure { error ->
                    Timber.e(error, "Error al cargar servicios")
                    _errorMessage.value = "Error al cargar servicios: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo servicio
     */
    fun createService(
        title: String,
        description: String,
        price: Double,
        professionalId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val service = SupabaseService(
                id = "", // Supabase generará el ID
                professionalId = professionalId,
                title = title,
                slug = title.lowercase().replace(" ", "-"),
                description = description,
                price = price,
                categoryId = "default-category", // TODO: Pasar categoryId real
                isActive = true
            )
            
            databaseService.insert<SupabaseService>("ProfessionalService", service)
                .onSuccess { newService ->
                    Timber.d("Servicio creado: ${newService?.id}")
                    // Recargar servicios
                    loadServices()
                }
                .onFailure { error ->
                    Timber.e(error, "Error al crear servicio")
                    _errorMessage.value = "Error al crear servicio: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Actualiza un servicio existente
     */
    fun updateService(
        id: String,
        title: String,
        description: String,
        price: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Buscar el servicio actual para mantener los campos no modificados
            databaseService.getById<SupabaseService>("ProfessionalService", id)
                .onSuccess { currentService ->
                    currentService?.let { service ->
                        val updatedService = service.copy(
                            title = title,
                            description = description,
                            price = price
                        )
                        
                        databaseService.update<SupabaseService>("ProfessionalService", id, updatedService)
                            .onSuccess {
                                Timber.d("Servicio actualizado: $id")
                                loadServices()
                            }
                            .onFailure { error ->
                                Timber.e(error, "Error al actualizar servicio")
                                _errorMessage.value = "Error al actualizar: ${error.message}"
                            }
                    }
                }
                .onFailure { error ->
                    Timber.e(error, "Error al buscar servicio")
                    _errorMessage.value = "Error al buscar servicio: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Elimina un servicio
     */
    fun deleteService(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            databaseService.delete("ProfessionalService", id)
                .onSuccess {
                    Timber.d("Servicio eliminado: $id")
                    loadServices()
                }
                .onFailure { error ->
                    Timber.e(error, "Error al eliminar servicio")
                    _errorMessage.value = "Error al eliminar: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Busca servicios por profesional
     */
    fun loadServicesByProfessional(professionalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            databaseService.findBy<SupabaseService>("ProfessionalService", "professionalId", professionalId)
                .onSuccess { servicesList ->
                    Timber.d("Servicios del profesional cargados: ${servicesList.size}")
                    _services.value = servicesList
                }
                .onFailure { error ->
                    Timber.e(error, "Error al cargar servicios del profesional")
                    _errorMessage.value = "Error al cargar servicios: ${error.message}"
                }
            
            _isLoading.value = false
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

// ========== Estados de Autenticación ==========

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: UserInfo?) : AuthState()
    data class Error(val message: String) : AuthState()
}

