package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.repository.ClientRepository

/**
 * ViewModel for ServicesScreen
 * Manages professionals list, search, and filtering
 */
@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val repository: ClientRepository
) : ViewModel() {

    private val _professionals = MutableStateFlow<List<Professional>>(emptyList())
    val professionals = _professionals.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadProfessionals()
    }

    private fun loadProfessionals() {
        _loading.value = true
        viewModelScope.launch {
            try {
                // TODO: Load professionals from repository
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error loading professionals: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchProfessionals(query: String) {
        // TODO: Implement search
    }

    fun filterBySpecialty(specialty: String) {
        viewModelScope.launch {
            try {
                repository.getProfessionalsBySpecialty(specialty).collect { professionals ->
                    _professionals.value = professionals
                }
            } catch (e: Exception) {
                _error.value = "Error filtering: ${e.message}"
            }
        }
    }
}
