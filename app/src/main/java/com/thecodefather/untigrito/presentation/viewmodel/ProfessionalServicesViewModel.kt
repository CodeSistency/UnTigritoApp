package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

/**
 * ViewModel para gestión de servicios profesionales
 */
@HiltViewModel
class ProfessionalServicesViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    /**
     * Generates current timestamp in ISO 8601 format
     */
    private fun getCurrentTimestamp(): String {
        return Instant.now().toString()
    }

    private val _services = MutableStateFlow<List<SupabaseService>>(emptyList())
    val services: StateFlow<List<SupabaseService>> = _services.asStateFlow()

    private val _professions = MutableStateFlow<List<SupabaseProfession>>(emptyList())
    val professions: StateFlow<List<SupabaseProfession>> = _professions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingProfessions = MutableStateFlow(false)
    val isLoadingProfessions: StateFlow<Boolean> = _isLoadingProfessions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess.asStateFlow()

    /**
     * Carga los servicios de un profesional
     */
    fun loadServices(professionalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔍 SERVICES VIEWMODEL - Loading services for professional: $professionalId")
                
                val result = supabaseDatabase.getProfessionalServices(professionalId)
                
                result.onSuccess { servicesList ->
                    _services.value = servicesList
                    Timber.d("✅ SERVICES VIEWMODEL - Services loaded successfully: ${servicesList.size} services")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error loading services")
                    _errorMessage.value = exception.message ?: "Error al cargar servicios"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error loading services")
                _errorMessage.value = e.message ?: "Error al cargar servicios"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga las profesiones/categorías disponibles
     */
    fun loadProfessions() {
        viewModelScope.launch {
            _isLoadingProfessions.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔍 SERVICES VIEWMODEL - Loading professions")
                
                val result = supabaseDatabase.getProfessions()
                
                result.onSuccess { professionsList ->
                    _professions.value = professionsList
                    Timber.d("✅ SERVICES VIEWMODEL - Professions loaded successfully: ${professionsList.size} professions")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error loading professions")
                    _errorMessage.value = exception.message ?: "Error al cargar categorías"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error loading professions")
                _errorMessage.value = e.message ?: "Error al cargar categorías"
            } finally {
                _isLoadingProfessions.value = false
            }
        }
    }

    /**
     * Crea un nuevo servicio
     */
    fun createService(service: CreateServiceRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val user = authStateManager.getCurrentUser()
                if (user == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }

                // Validaciones
                if (service.title.isBlank()) {
                    _errorMessage.value = "El título es requerido"
                    return@launch
                }
                if (service.description.isBlank()) {
                    _errorMessage.value = "La descripción es requerida"
                    return@launch
                }
                if (service.price <= 0) {
                    _errorMessage.value = "El precio debe ser mayor a 0"
                    return@launch
                }
                if (service.categoryId.isBlank()) {
                    _errorMessage.value = "La categoría es requerida"
                    return@launch
                }

                Timber.d("💾 SERVICES VIEWMODEL - Creating service: ${service.title}")
                
                val currentTimestamp = getCurrentTimestamp()
                val newService = SupabaseService(
                    id = "", // Se generará automáticamente
                    professionalId = user.id,
                    title = service.title,
                    slug = service.title.lowercase().replace(" ", "-"),
                    description = service.description,
                    price = service.price,
                    categoryId = service.categoryId,
                    isActive = service.isActive,
                    createdAt = currentTimestamp,
                    updatedAt = currentTimestamp
                )
                
                val result = supabaseDatabase.createProfessionalService(newService)
                
                result.onSuccess { createdService ->
                    if (createdService != null) {
                        _services.value = _services.value + createdService
                        _operationSuccess.value = true
                        Timber.d("✅ SERVICES VIEWMODEL - Service created successfully")
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error creating service")
                    _errorMessage.value = exception.message ?: "Error al crear servicio"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error creating service")
                _errorMessage.value = e.message ?: "Error al crear servicio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza un servicio existente
     */
    fun updateService(serviceId: String, service: UpdateServiceRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Validaciones
                if (service.title.isBlank()) {
                    _errorMessage.value = "El título es requerido"
                    return@launch
                }
                if (service.description.isBlank()) {
                    _errorMessage.value = "La descripción es requerida"
                    return@launch
                }
                if (service.price <= 0) {
                    _errorMessage.value = "El precio debe ser mayor a 0"
                    return@launch
                }
                if (service.categoryId.isBlank()) {
                    _errorMessage.value = "La categoría es requerida"
                    return@launch
                }

                Timber.d("💾 SERVICES VIEWMODEL - Updating service: $serviceId")
                
                val existingService = _services.value.find { it.id == serviceId }
                if (existingService == null) {
                    _errorMessage.value = "Servicio no encontrado"
                    return@launch
                }
                
                val updatedService = existingService.copy(
                    title = service.title,
                    slug = service.title.lowercase().replace(" ", "-"),
                    description = service.description,
                    price = service.price,
                    categoryId = service.categoryId,
                    isActive = service.isActive,
                    updatedAt = getCurrentTimestamp()
                )
                
                val result = supabaseDatabase.updateProfessionalService(serviceId, updatedService)
                
                result.onSuccess { updated ->
                    if (updated != null) {
                        _services.value = _services.value.map { 
                            if (it.id == serviceId) updated else it 
                        }
                        _operationSuccess.value = true
                        Timber.d("✅ SERVICES VIEWMODEL - Service updated successfully")
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error updating service")
                    _errorMessage.value = exception.message ?: "Error al actualizar servicio"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error updating service")
                _errorMessage.value = e.message ?: "Error al actualizar servicio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un servicio
     */
    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🗑️ SERVICES VIEWMODEL - Deleting service: $serviceId")
                
                val result = supabaseDatabase.deleteProfessionalService(serviceId)
                
                result.onSuccess {
                    _services.value = _services.value.filter { it.id != serviceId }
                    _operationSuccess.value = true
                    Timber.d("✅ SERVICES VIEWMODEL - Service deleted successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error deleting service")
                    _errorMessage.value = exception.message ?: "Error al eliminar servicio"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error deleting service")
                _errorMessage.value = e.message ?: "Error al eliminar servicio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cambia el estado activo/inactivo de un servicio
     */
    fun toggleServiceStatus(serviceId: String, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔄 SERVICES VIEWMODEL - Toggling service status: $serviceId to $isActive")
                
                val existingService = _services.value.find { it.id == serviceId }
                if (existingService == null) {
                    _errorMessage.value = "Servicio no encontrado"
                    return@launch
                }
                
                val updatedService = existingService.copy(
                    isActive = isActive,
                    updatedAt = getCurrentTimestamp()
                )
                
                val result = supabaseDatabase.updateProfessionalService(serviceId, updatedService)
                
                result.onSuccess { updated ->
                    if (updated != null) {
                        _services.value = _services.value.map { 
                            if (it.id == serviceId) updated else it 
                        }
                        _operationSuccess.value = true
                        Timber.d("✅ SERVICES VIEWMODEL - Service status toggled successfully")
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICES VIEWMODEL - Error toggling service status")
                    _errorMessage.value = exception.message ?: "Error al cambiar estado del servicio"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICES VIEWMODEL - Error toggling service status")
                _errorMessage.value = e.message ?: "Error al cambiar estado del servicio"
            } finally {
                _isLoading.value = false
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
     * Resetea el estado de éxito
     */
    fun resetOperationSuccess() {
        _operationSuccess.value = false
    }
}

/**
 * Request para crear un servicio
 */
data class CreateServiceRequest(
    val title: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val isActive: Boolean = true
)

/**
 * Request para actualizar un servicio
 */
data class UpdateServiceRequest(
    val title: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val isActive: Boolean
)

