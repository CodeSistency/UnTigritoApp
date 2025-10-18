package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.repository.ClientRepository

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val serviceId: String = savedStateHandle["serviceId"] ?: ""

    private val _service = MutableStateFlow<ServicePosting?>(null)
    val service = _service.asStateFlow()

    private val _offers = MutableStateFlow<List<ClientRequest>>(emptyList())
    val offers = _offers.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        if (serviceId.isNotEmpty()) {
            loadServiceDetails()
        }
    }

    private fun loadServiceDetails() {
        _loading.value = true
        viewModelScope.launch {
            try {
                repository.getServicePostingById(serviceId).collect { service ->
                    _service.value = service
                }

                repository.getClientRequestsByPosting(serviceId).collect { requests ->
                    _offers.value = requests
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _loading.value = false
            }
        }
    }

    fun acceptOffer(offerId: String) {
        viewModelScope.launch {
            try {
                repository.updateClientRequestStatus(offerId, ClientRequest.STATUS_ACCEPTED)
                loadServiceDetails()
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun rejectOffer(offerId: String) {
        viewModelScope.launch {
            try {
                repository.updateClientRequestStatus(offerId, ClientRequest.STATUS_REJECTED)
                loadServiceDetails()
            } catch (e: Exception) {
                // Error handling
            }
        }
    }
}
