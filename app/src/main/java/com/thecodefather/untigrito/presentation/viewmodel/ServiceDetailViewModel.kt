package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.domain.model.ServiceRequestWithClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel para la pantalla de detalles de servicio
 */
@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _service = MutableStateFlow<SupabaseService?>(null)
    val service: StateFlow<SupabaseService?> = _service.asStateFlow()

    private val _serviceRequests = MutableStateFlow<List<ServiceRequestWithClient>>(emptyList())
    val serviceRequests: StateFlow<List<ServiceRequestWithClient>> = _serviceRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    /**
     * Carga los detalles del servicio y sus solicitudes
     */
    fun loadServiceDetail(serviceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔍 SERVICE DETAIL VIEWMODEL - Loading service details: $serviceId")
                
                // Cargar servicio
                val serviceResult = supabaseDatabase.getById<SupabaseService>("ProfessionalService", serviceId)
                serviceResult.onSuccess { service ->
                    _service.value = service
                    Timber.d("✅ SERVICE DETAIL VIEWMODEL - Service loaded: ${service?.title}")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICE DETAIL VIEWMODEL - Error loading service")
                    _errorMessage.value = exception.message ?: "Error al cargar servicio"
                }
                
                // Cargar solicitudes
                loadServiceRequests(serviceId)
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICE DETAIL VIEWMODEL - Error loading service details")
                _errorMessage.value = e.message ?: "Error al cargar detalles"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga las solicitudes de clientes para el servicio
     */
    private suspend fun loadServiceRequests(serviceId: String) {
        try {
            Timber.d("🔍 SERVICE DETAIL VIEWMODEL - Loading service requests for: $serviceId")
            
            val requestsResult = supabaseDatabase.getServiceRequests(serviceId)
            requestsResult.onSuccess { transactions ->
                Timber.d("📋 SERVICE DETAIL VIEWMODEL - Found ${transactions.size} transactions")
                
                // Obtener datos de clientes
                val requestsWithClients = transactions.mapNotNull { transaction ->
                    val clientResult = supabaseDatabase.getClientById(transaction.clientId)
                    clientResult.getOrNull()?.let { client ->
                        ServiceRequestWithClient(transaction, client)
                    }
                }
                
                _serviceRequests.value = requestsWithClients
                Timber.d("✅ SERVICE DETAIL VIEWMODEL - Loaded ${requestsWithClients.size} requests with client data")
            }.onFailure { exception ->
                Timber.e(exception, "❌ SERVICE DETAIL VIEWMODEL - Error loading service requests")
                _errorMessage.value = exception.message ?: "Error al cargar solicitudes"
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ SERVICE DETAIL VIEWMODEL - Error in loadServiceRequests")
            _errorMessage.value = e.message ?: "Error al cargar solicitudes"
        }
    }

    /**
     * Acepta una solicitud de cliente
     */
    fun acceptRequest(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("✅ SERVICE DETAIL VIEWMODEL - Accepting request: $transactionId")
                
                val result = supabaseDatabase.acceptServiceRequest(transactionId)
                result.onSuccess {
                    Timber.d("✅ SERVICE DETAIL VIEWMODEL - Request accepted successfully")
                    _navigationEvent.value = NavigationEvent.NavigateToProposals
                    // Recargar solicitudes
                    _service.value?.id?.let { loadServiceRequests(it) }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICE DETAIL VIEWMODEL - Error accepting request")
                    _errorMessage.value = exception.message ?: "Error al aceptar solicitud"
                }
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICE DETAIL VIEWMODEL - Error accepting request")
                _errorMessage.value = e.message ?: "Error al aceptar solicitud"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Rechaza una solicitud de cliente
     */
    fun declineRequest(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("❌ SERVICE DETAIL VIEWMODEL - Declining request: $transactionId")
                
                val result = supabaseDatabase.declineServiceRequest(transactionId)
                result.onSuccess {
                    Timber.d("✅ SERVICE DETAIL VIEWMODEL - Request declined successfully")
                    // Remover de la lista
                    _serviceRequests.value = _serviceRequests.value.filter { 
                        it.transaction.id != transactionId 
                    }
                }.onFailure { exception ->
                    Timber.e(exception, "❌ SERVICE DETAIL VIEWMODEL - Error declining request")
                    _errorMessage.value = exception.message ?: "Error al rechazar solicitud"
                }
            } catch (e: Exception) {
                Timber.e(e, "❌ SERVICE DETAIL VIEWMODEL - Error declining request")
                _errorMessage.value = e.message ?: "Error al rechazar solicitud"
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
     * Limpia los eventos de navegación
     */
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
}

/**
 * Eventos de navegación
 */
sealed class NavigationEvent {
    object NavigateToProposals : NavigationEvent()
}