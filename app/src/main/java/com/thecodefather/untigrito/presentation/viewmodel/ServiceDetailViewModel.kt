package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceDetailUiState())
    val uiState: StateFlow<ServiceDetailUiState> = _uiState.asStateFlow()

    /**
     * Load service details by ID
     */
    fun loadServiceDetails(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.getById<SupabaseService>("ProfessionalService", serviceId)
                
                result.onSuccess { supabaseService ->
                    if (supabaseService != null) {
                        val service = mapSupabaseToService(supabaseService)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            service = service,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            service = null,
                            errorMessage = "Servicio no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        service = null,
                        errorMessage = exception.message ?: "Error al cargar el servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    service = null,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    /**
     * Request service - placeholder for future implementation
     */
    fun requestService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // TODO: Implement service request logic
            // This would typically create a service request/booking
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                showRequestSuccess = true
            )
        }
    }

    /**
     * Contact professional - placeholder for future implementation
     */
    fun contactProfessional() {
        viewModelScope.launch {
            // TODO: Implement contact professional logic
            // This would typically open chat or contact form
        }
    }

    /**
     * Clear success messages
     */
    fun clearSuccessMessages() {
        _uiState.value = _uiState.value.copy(showRequestSuccess = false)
    }

    /**
     * Clear error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Mapper helper to convert SupabaseService to Service domain model
     */
    private fun mapSupabaseToService(supabaseService: SupabaseService): Service {
        return Service(
            id = supabaseService.id,
            title = supabaseService.title,
            description = supabaseService.description,
            category = supabaseService.categoryId,
            minPrice = supabaseService.price,
            maxPrice = supabaseService.price, // Use same price for both min and max
            serviceArea = supabaseService.serviceLocations ?: "",
            status = if (supabaseService.isActive) ServiceStatus.ACTIVE else ServiceStatus.INACTIVE,
            images = emptyList(), // TODO: Implement image loading
            createdAt = try {
                Date(supabaseService.createdAt?.toLongOrNull() ?: System.currentTimeMillis())
            } catch (e: Exception) {
                Date()
            },
            updatedAt = try {
                Date(supabaseService.updatedAt?.toLongOrNull() ?: System.currentTimeMillis())
            } catch (e: Exception) {
                Date()
            },
            isActive = supabaseService.isActive,
            rating = 4.5, // TODO: Get real rating from reviews
            reviewCount = 0, // TODO: Get real review count
            completedJobs = 0 // TODO: Get real completed jobs count
        )
    }
}

data class ServiceDetailUiState(
    val isLoading: Boolean = false,
    val service: Service? = null,
    val errorMessage: String? = null,
    val showRequestSuccess: Boolean = false
)