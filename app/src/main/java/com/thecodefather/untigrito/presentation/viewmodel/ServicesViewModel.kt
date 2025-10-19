package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfession
import com.thecodefather.untigrito.data.datasource.remote.ServiceWithProfessional
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.toProfessional
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
    
    // Cache para datos de profesionales por servicio
    private val _servicesWithProfessionals = MutableStateFlow<List<ServiceWithProfessional>>(emptyList())
    val servicesWithProfessionals: StateFlow<List<ServiceWithProfessional>> = _servicesWithProfessionals.asStateFlow()

    init {
        Timber.d("ServicesViewModel initialized")
    }

    // Cargar servicios disponibles para clientes desde Supabase
    fun loadServices() {
        viewModelScope.launch {
            Timber.d("loadServices() called")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Consulta con JOIN para obtener información del profesional
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalService with User JOIN")
                Timber.d("OPERATION: getServicesWithProfessional()")
                Timber.d("Fetching services with professional info from Supabase...")
                val result = supabaseDatabase.getServicesWithProfessional()
                
                result.onSuccess { servicesWithProfessionals ->
                    Timber.d("Successfully fetched ${servicesWithProfessionals.size} services with professional info")
                    Timber.d("Raw services data: $servicesWithProfessionals")

                    val activeServices = servicesWithProfessionals.filter { it.isActive }
                    Timber.d("Filtered to ${activeServices.size} active services")

                    // Guardar datos de profesionales para uso posterior
                    _servicesWithProfessionals.value = activeServices
                    
                    val domainServices = activeServices.map { serviceWithProf ->
                        Timber.d("Mapping service: id=${serviceWithProf.id}, title=${serviceWithProf.title}, price=${serviceWithProf.price}, professional=${serviceWithProf.professional.name}")
                        mapServiceWithProfessionalToService(serviceWithProf)
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
                Timber.d("========== SUPABASE CONNECTION ==========")
                Timber.d("TABLE: ProfessionalProfile")
                Timber.d("OPERATION: getAll()")
                Timber.d("Fetching all professional profiles from Supabase...")
                val result = supabaseDatabase.getAll<SupabaseProfessionalProfile>("ProfessionalProfile")
                
                result.onSuccess { profiles ->
                    Timber.d("Successfully fetched ${profiles.size} professional profiles")
                    Timber.d("Raw profiles data: $profiles")

                    val activeProfessionals = profiles.filter { it.isVerified }
                    Timber.d("Filtered to ${activeProfessionals.size} verified professionals")

                    val domainProfessionals = activeProfessionals.map { profile ->
                        Timber.d("Mapping profile: id=${profile.id}, bio=${profile.bio}, rating=${profile.rating}")
                        profile.toProfessional()
                    }
                    Timber.d("Mapped ${domainProfessionals.size} domain professionals successfully")

                    // Aplicar filtro de búsqueda
                    var filteredProfessionals = domainProfessionals
                    if (_uiState.value.searchQuery.isNotEmpty()) {
                        Timber.d("Applying search query: ${_uiState.value.searchQuery}")
                        filteredProfessionals = filteredProfessionals.filter { professional ->
                            professional.bio?.contains(_uiState.value.searchQuery, ignoreCase = true) == true ||
                            professional.specialties.any { it.contains(_uiState.value.searchQuery, ignoreCase = true) }
                        }
                        Timber.d("After search: ${filteredProfessionals.size} professionals")
                    }

                    val sortedProfessionals = filteredProfessionals.sortedByDescending { it.rating ?: 0.0 }
                    Timber.d("Final professionals count: ${sortedProfessionals.size}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        professional = sortedProfessionals,
                        errorMessage = null
                    )
                    Timber.d("UI state updated successfully")
                    Timber.d("==========================================")
                }.onFailure { exception ->
                    Timber.e(exception, "Failed to load professional profiles")
                    Timber.d("==========================================")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar profesionales"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadProfessionals()")
                Timber.d("==========================================")
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

    // Mapper helper para ServiceWithProfessional
    private fun mapServiceWithProfessionalToService(serviceWithProf: ServiceWithProfessional): Service {
        Timber.d("Mapping ServiceWithProfessional to Service: id=${serviceWithProf.id}")

        try {
            val domainService = Service(
                id = serviceWithProf.id,
                title = serviceWithProf.title,
                description = serviceWithProf.description,
                category = serviceWithProf.categoryId,
                minPrice = serviceWithProf.price,
                maxPrice = serviceWithProf.price,
                serviceArea = "", // Se obtiene por separado si es necesario
                status = if (serviceWithProf.isActive) ServiceStatus.ACTIVE else ServiceStatus.INACTIVE,
                images = emptyList(),
                createdAt = try {
                    Date(serviceWithProf.createdAt ?: System.currentTimeMillis().toString())
                } catch (e: Exception) {
                    Timber.w(e, "Failed to parse createdAt: ${serviceWithProf.createdAt}")
                    Date()
                },
                updatedAt = Date(), // Usar fecha actual como fallback
                isActive = serviceWithProf.isActive,
                rating = 0.0,
                reviewCount = 0,
                completedJobs = 0
            )

            Timber.d("Successfully mapped service with professional: title=${domainService.title}, professional=${serviceWithProf.professional.name}")
            return domainService
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ServiceWithProfessional to Service")
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

    // Cargar categorías desde Supabase
    fun loadCategories() {
        viewModelScope.launch {
            Timber.d("loadCategories() called")
            try {
                val result = supabaseDatabase.getProfessions()
                result.onSuccess { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                    Timber.d("Categories loaded: ${categories.size}")
                }.onFailure { exception ->
                    Timber.e(exception, "Error loading categories")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Error al cargar categorías"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadCategories()")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }
}

data class ServicesUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val professional: List<Professional> = emptyList(),
    val categories: List<SupabaseProfession> = emptyList(),
    val selectedFilter: ServiceFilter = ServiceFilter.ALL,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
    val showCreateSuccess: Boolean = false,
    val showUpdateSuccess: Boolean = false,
    val showDeleteSuccess: Boolean = false,
    val showToggleSuccess: Boolean = false
)