package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceFilter
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val servicesRepository: ServicesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()
    val professionals: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            servicesRepository.getServices(_uiState.value.selectedFilter)
                .collect { services ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        services = services,
                        errorMessage = null
                    )
                }
        }
    }

    fun updateFilter(filter: ServiceFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadServices()
    }

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
            
            servicesRepository.createService(
                title = title,
                description = description,
                category = category,
                minPrice = minPrice,
                maxPrice = maxPrice,
                serviceArea = serviceArea,
                images = images
            ).collect { result ->
                result.fold(
                    onSuccess = { service ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            showCreateSuccess = true
                        )
                        loadServices() // Recargar la lista
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al crear servicio"
                        )
                    }
                )
            }
        }
    }

    fun updateService(service: Service) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            servicesRepository.updateService(service)
                .collect { result ->
                    result.fold(
                        onSuccess = { updatedService ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                showUpdateSuccess = true
                            )
                            loadServices() // Recargar la lista
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al actualizar servicio"
                            )
                        }
                    )
                }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            servicesRepository.deleteService(serviceId)
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                showDeleteSuccess = true
                            )
                            loadServices() // Recargar la lista
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al eliminar servicio"
                            )
                        }
                    )
                }
        }
    }

    fun toggleServiceStatus(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            servicesRepository.toggleServiceStatus(serviceId)
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                showToggleSuccess = true
                            )
                            loadServices() // Recargar la lista
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al cambiar estado del servicio"
                            )
                        }
                    )
                }
        }
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
    val errorMessage: String? = null,
    val showCreateSuccess: Boolean = false,
    val showUpdateSuccess: Boolean = false,
    val showDeleteSuccess: Boolean = false,
    val showToggleSuccess: Boolean = false
)