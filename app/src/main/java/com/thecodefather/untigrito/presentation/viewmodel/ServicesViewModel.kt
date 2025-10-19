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
import timber.log.Timber
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

    init {
        Timber.d("ServicesViewModel initialized")
    }

    // Cargar servicios disponibles para clientes desde Supabase
    fun loadServices() {
        viewModelScope.launch {
            Timber.d("loadServices() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Consulta directa a ProfessionalService - TODOS los servicios activos
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService")
                Timber.d("OPERATION: getAll()")
                Timber.d("Fetching all professional services from Supabase...")
                val result = supabaseDatabase.getAll<SupabaseService>("ProfessionalService")
                
                result.onSuccess { services ->
                    Timber.d("Successfully fetched ${services.size} services from table 'ProfessionalService'")
                    Timber.d("Raw services data: $services")

                    val activeServices = services.filter { it.isActive }
                    Timber.d("Filtered to ${activeServices.size} active services")

                    val domainServices = activeServices.map { service ->
                        Timber.d("Mapping service: id=${service.id}, title=${service.title}, price=${service.price}, isActive=${service.isActive}")
                        mapSupabaseToService(service)
                    }
                    Timber.d("Mapped ${domainServices.size} domain services successfully")

                    var filteredServices = when (_uiState.value.selectedFilter) {
                        ServiceFilter.ALL -> {
                            Timber.d("Applying ALL filter")
                            domainServices
                        }
                        ServiceFilter.ACTIVE -> {
                            Timber.d("Applying ACTIVE filter")
                            domainServices.filter { it.isActive }
                        }

                        ServiceFilter.INACTIVE -> {
                            Timber.d("Applying INACTIVE filter")
                            domainServices.filter { !it.isActive }
                        }
                    }
                    Timber.d("After filter: ${filteredServices.size} services")

                    // Aplicar filtro de búsqueda
                    if (_uiState.value.searchQuery.isNotEmpty()) {
                        Timber.d("Applying search query: ${_uiState.value.searchQuery}")
                        filteredServices = filteredServices.filter { service ->
                            service.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    service.description.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    ) ||
                                    service.category.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    )
                        }
                        Timber.d("After search: ${filteredServices.size} services")
                    }

                    // Aplicar filtro de categoría
                    if (_uiState.value.selectedCategory != null) {
                        Timber.d("Applying category filter: ${_uiState.value.selectedCategory}")
                        filteredServices = filteredServices.filter { service ->
                            service.category.equals(
                                _uiState.value.selectedCategory,
                                ignoreCase = true
                            )
                        }
                        Timber.d("After category filter: ${filteredServices.size} services")
                    }

                    val sortedServices = filteredServices.sortedByDescending { it.createdAt }
                    Timber.d("Final services count: ${sortedServices.size}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        services = sortedServices,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully")
                    Timber.d("==========================================")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load services from table 'ProfessionalService'")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar servicios"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadServices()")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun updateFilter(filter: ServiceFilter) {
        Timber.d("updateFilter() called with filter: $filter")
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadServices()
    }

    // Búsqueda de servicios
    fun searchServices(query: String) {
        Timber.d("searchServices() called with query: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadServices()
    }

    // Filtro por categoría
    fun filterByCategory(category: String?) {
        Timber.d("filterByCategory() called with category: $category")
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadServices()
    }

    // Cargar profesionales disponibles
    fun loadProfessionals() {
        viewModelScope.launch {
            Timber.d("loadProfessionals() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // TODO: Implementar carga de profesionales desde Supabase
                Timber.w("loadProfessionals() - Not yet implemented, returning empty list")
                // Por ahora, retornar lista vacía hasta implementar la integración real
                val exampleProfessionals = emptyList<Professional>()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    professional = exampleProfessionals,
                    errorMessage = null
                )
                Timber.d("Professionals list loaded (empty for now)")
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProfessionals()")
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
            Timber.d("createService() called")
            Timber.d("Parameters: title=$title, category=$category, minPrice=$minPrice, maxPrice=$maxPrice")
            Timber.d("Description length: ${description.length} characters")
            Timber.d("Service area: $serviceArea")
            Timber.d("Images count: ${images.size}")

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                Timber.d("Current professional ID: $professionalId")
                
                if (professionalId == null) {
                    Timber.w("User not authenticated, cannot create service")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }

                val serviceId = UUID.randomUUID().toString()
                val slug = title.lowercase().replace(" ", "-")
                Timber.d("Generated service UUID: $serviceId")
                Timber.d("Generated slug: $slug")

                val service = SupabaseService(
                    id = serviceId,
                    professionalId = professionalId,
                    title = title,
                    slug = slug,
                    description = description,
                    price = minPrice, // Usar precio mínimo
                    categoryId = category,
                    isActive = true,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )

                Timber.d("Creating SupabaseService object:")
                Timber.d("  - id: ${service.id}")
                Timber.d("  - professionalId: ${service.professionalId}")
                Timber.d("  - title: ${service.title}")
                Timber.d("  - slug: ${service.slug}")
                Timber.d("  - description: ${service.description}")
                Timber.d("  - price: ${service.price}")
                Timber.d("  - categoryId: ${service.categoryId}")
                Timber.d("  - isActive: ${service.isActive}")
                Timber.d("  - createdAt: ${service.createdAt}")
                Timber.d("  - updatedAt: ${service.updatedAt}")

                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService")
                Timber.d("OPERATION: insert()")
                Timber.d("Inserting new service into database...")
                val result = supabaseDatabase.insert("ProfessionalService", service)
                
                result.onSuccess { insertedService ->
                    if (insertedService != null) {
                        Timber.d("Service inserted successfully into table 'ProfessionalService'")
                        Timber.d("Inserted service ID: ${insertedService.id}")
                        Timber.d("Inserted service title: ${insertedService.title}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showCreateSuccess = true
                        )
                        Timber.d("Service creation completed successfully")
                        Timber.d("==========================================")
                        // Recargar servicios
                        loadServices()
                    } else {
                        Timber.w("Insert operation succeeded but returned null")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al crear servicio"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to insert service into table 'ProfessionalService'")
                    Timber.d("Error details: ${exception.message}")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear servicio"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in createService()")
                Timber.d("Error details: ${e.message}")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun updateService(service: Service) {
        viewModelScope.launch {
            Timber.d("updateService() called for serviceId: ${service.id}")
            Timber.d("New values: title=${service.title}, category=${service.category}, minPrice=${service.minPrice}")

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del profesional actual
                val professionalId = authStateManager.getCurrentUserId()
                Timber.d("Current professional ID: $professionalId")
                
                if (professionalId == null) {
                    Timber.w("User not authenticated, cannot update service")
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

                Timber.d("Updating SupabaseService object:")
                Timber.d("  - id: ${supabaseService.id}")
                Timber.d("  - title: ${supabaseService.title}")
                Timber.d("  - price: ${supabaseService.price}")
                Timber.d("  - isActive: ${supabaseService.isActive}")
                Timber.d("  - updatedAt: ${supabaseService.updatedAt}")

                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService")
                Timber.d("OPERATION: update()")
                Timber.d("Updating service with id: ${service.id}")
                val result = supabaseDatabase.update("ProfessionalService", service.id, supabaseService)
                
                result.onSuccess { updatedService ->
                    if (updatedService != null) {
                        Timber.d("Service updated successfully in table 'ProfessionalService'")
                        Timber.d("Updated service ID: ${updatedService.id}")
                        Timber.d("Updated service title: ${updatedService.title}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showUpdateSuccess = true
                        )
                        Timber.d("Service update completed successfully")
                        Timber.d("==========================================")
                        // Recargar servicios
                        loadServices()
                    } else {
                        Timber.w("Update operation succeeded but returned null")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al actualizar servicio"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to update service in table 'ProfessionalService'")
                    Timber.d("Error details: ${exception.message}")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al actualizar servicio"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in updateService()")
                Timber.d("Error details: ${e.message}")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            Timber.d("deleteService() called with serviceId: $serviceId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService")
                Timber.d("OPERATION: delete()")
                Timber.d("Deleting service with id: $serviceId")
                val result = supabaseDatabase.delete("ProfessionalService", serviceId)
                
                result.onSuccess {
                    Timber.d("Service deleted successfully from table 'ProfessionalService'")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        showDeleteSuccess = true
                    )
                    Timber.d("Service deletion completed successfully")
                    Timber.d("==========================================")
                    // Recargar servicios
                    loadServices()
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to delete service from table 'ProfessionalService'")
                    Timber.d("Error details: ${exception.message}")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al eliminar servicio"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in deleteService()")
                Timber.d("Error details: ${e.message}")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun toggleServiceStatus(serviceId: String) {
        viewModelScope.launch {
            Timber.d("toggleServiceStatus() called with serviceId: $serviceId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Primero obtener el servicio actual
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService")
                Timber.d("OPERATION: getById()")
                Timber.d("Fetching service with id: $serviceId")
                val result = supabaseDatabase.getById<SupabaseService>("ProfessionalService", serviceId)
                
                result.onSuccess { service ->
                    if (service != null) {
                        Timber.d("Service found: title=${service.title}, currentStatus=${service.isActive}")

                        val newStatus = !service.isActive
                        Timber.d("Toggling service status from ${service.isActive} to $newStatus")

                        val updatedService = service.copy(
                            isActive = newStatus,
                            updatedAt = System.currentTimeMillis().toString()
                        )

                        Timber.d("========== SUPABASE CONNECTION ==========")
                        Timber.d("TABLE: ProfessionalService")
                        Timber.d("OPERATION: update()")
                        Timber.d("Updating service status to: $newStatus")
                        val updateResult = supabaseDatabase.update("ProfessionalService", serviceId, updatedService)
                        
                        updateResult.onSuccess {
                            Timber.d("Service status updated successfully in table 'ProfessionalService'")
                            Timber.d("New status: $newStatus")

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                showToggleSuccess = true
                            )
                            Timber.d("Service status toggle completed successfully")
                            Timber.d("==========================================")
                            // Recargar servicios
                            loadServices()
                        }.onFailure { exception ->
                            Timber.e(
                                exception,
                                "Failed to update service status in table 'ProfessionalService'"
                            )
                            Timber.d("Error details: ${exception.message}")
                            Timber.d("==========================================")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al cambiar estado del servicio"
                            )
                        }
                    } else {
                        Timber.w("Service not found with id: $serviceId")
                        Timber.d("==========================================")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Servicio no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to fetch service from table 'ProfessionalService'")
                    Timber.d("Error details: ${exception.message}")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cambiar estado del servicio"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in toggleServiceStatus()")
                Timber.d("Error details: ${e.message}")
                Timber.d("==========================================")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
    
    // Mapper helper
    private fun mapSupabaseToService(service: SupabaseService): Service {
        Timber.d("Mapping SupabaseService to Service: id=${service.id}")

        try {
            val domainService = Service(
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
                    Timber.w(e, "Failed to parse createdAt: ${service.createdAt}")
                    Date()
                },
                updatedAt = try {
                    Date(service.updatedAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse updatedAt: ${service.updatedAt}")
                    Date()
                },
                isActive = service.isActive,
                rating = 0.0,
                reviewCount = 0,
                completedJobs = 0
            )

            Timber.d("Successfully mapped service: title=${domainService.title}, status=${domainService.status}")
            return domainService
        } catch (e: Exception) {
            Timber.e(e, "Error mapping SupabaseService to Service")
            throw e
        }
    }

    fun clearError() {
        Timber.d("clearError() called")
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessages() {
        Timber.d("clearSuccessMessages() called")
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