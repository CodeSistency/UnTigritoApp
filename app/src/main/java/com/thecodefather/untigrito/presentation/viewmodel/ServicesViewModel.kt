package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceFilter
import com.thecodefather.untigrito.domain.model.ServiceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()
    val professionals: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    // Cargar servicios disponibles para clientes desde Supabase
    fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Consulta directa a ProfessionalService - TODOS los servicios activos
                val result = supabaseDatabase.getAll<SupabaseService>("ProfessionalService")
                
                result.onSuccess { services ->
                    val domainServices = services
                        .filter { it.isActive } // Solo servicios activos
                        .map { service -> mapSupabaseToService(service) }
                        .let { allServices ->
                            // Aplicar filtros
                            var filteredServices = when (_uiState.value.selectedFilter) {
                                ServiceFilter.ALL -> allServices
                                ServiceFilter.ACTIVE -> allServices.filter { it.isActive }
                                ServiceFilter.INACTIVE -> allServices.filter { !it.isActive }
                            }
                            
                            // Aplicar filtro de búsqueda
                            if (_uiState.value.searchQuery.isNotEmpty()) {
                                filteredServices = filteredServices.filter { service ->
                                    service.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    service.description.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    service.category.contains(_uiState.value.searchQuery, ignoreCase = true)
                                }
                            }
                            
                            // Aplicar filtro de categoría
                            if (_uiState.value.selectedCategory != null) {
                                filteredServices = filteredServices.filter { service ->
                                    service.category.equals(_uiState.value.selectedCategory, ignoreCase = true)
                                }
                            }
                            
                            filteredServices
                        }
                        .sortedByDescending { it.createdAt }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        services = domainServices,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar servicios"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun updateFilter(filter: ServiceFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadServices()
    }

    // Búsqueda de servicios
    fun searchServices(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadServices()
    }

    // Filtro por categoría
    fun filterByCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadServices()
    }

    // Cargar profesionales disponibles
    fun loadProfessionals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // TODO: Implementar carga de profesionales desde Supabase
                // Por ahora, retornar lista vacía hasta implementar la integración real
                val exampleProfessionals = emptyList<Professional>()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    professional = exampleProfessionals,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Crear servicio directamente en Supabase
    fun createService(
        title: String,
        description: String,
        category: String,
        minPrice: Double,
        maxPrice: Double,
        serviceArea: String,
        images: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                
                if (professionalId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                val service = SupabaseService(
                    id = UUID.randomUUID().toString(),
                    professionalId = professionalId,
                    title = title,
                    slug = title.lowercase().replace(" ", "-"),
                    description = description,
                    price = minPrice, // Usar precio mínimo
                    categoryId = category,
                    isActive = true,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )
                
                val result = supabaseDatabase.insert("ProfessionalService", service)
                
                result.onSuccess { insertedService ->
                    if (insertedService != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showCreateSuccess = true
                        )
                        // Recargar servicios
                        loadServices()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al crear servicio"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun updateService(service: Service) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                
                if (professionalId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                val supabaseService = SupabaseService(
                    id = service.id,
                    professionalId = professionalId,
                    title = service.title,
                    slug = service.title.lowercase().replace(" ", "-"),
                    description = service.description,
                    price = service.minPrice,
                    categoryId = service.category,
                    isActive = service.isActive,
                    createdAt = service.createdAt.time.toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )
                
                val result = supabaseDatabase.update("ProfessionalService", service.id, supabaseService)
                
                result.onSuccess { updatedService ->
                    if (updatedService != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showUpdateSuccess = true
                        )
                        // Recargar servicios
                        loadServices()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al actualizar servicio"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al actualizar servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.delete("ProfessionalService", serviceId)
                
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        showDeleteSuccess = true
                    )
                    // Recargar servicios
                    loadServices()
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al eliminar servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun toggleServiceStatus(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Primero obtener el servicio actual
                val result = supabaseDatabase.getById<SupabaseService>("ProfessionalService", serviceId)
                
                result.onSuccess { service ->
                    if (service != null) {
                        val updatedService = service.copy(
                            isActive = !service.isActive,
                            updatedAt = System.currentTimeMillis().toString()
                        )
                        
                        val updateResult = supabaseDatabase.update("ProfessionalService", serviceId, updatedService)
                        
                        updateResult.onSuccess {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                showToggleSuccess = true
                            )
                            // Recargar servicios
                            loadServices()
                        }.onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al cambiar estado del servicio"
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Servicio no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cambiar estado del servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Mapper helper
    private fun mapSupabaseToService(service: SupabaseService): Service {
        return Service(
            id = service.id,
            title = service.title,
            description = service.description,
            category = service.categoryId,
            minPrice = service.price,
            maxPrice = service.price, // Usar el mismo precio
            serviceArea = "", // Se obtiene por separado si es necesario
            status = if (service.isActive) ServiceStatus.ACTIVE else ServiceStatus.INACTIVE,
            images = emptyList(),
            createdAt = try {
                Date(service.createdAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            updatedAt = try {
                Date(service.updatedAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            isActive = service.isActive,
            rating = 0.0,
            reviewCount = 0,
            completedJobs = 0
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessages() {
        _uiState.value = _uiState.value.copy(
            showCreateSuccess = false,
            showUpdateSuccess = false,
            showDeleteSuccess = false,
            showToggleSuccess = false
        )
    }
}

data class ServicesUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val professional: List<Professional> = emptyList(),
    val selectedFilter: ServiceFilter = ServiceFilter.ALL,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
    val showCreateSuccess: Boolean = false,
    val showUpdateSuccess: Boolean = false,
    val showDeleteSuccess: Boolean = false,
    val showToggleSuccess: Boolean = false
)