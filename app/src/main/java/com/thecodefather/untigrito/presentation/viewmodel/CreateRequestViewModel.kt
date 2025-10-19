package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.repository.ClientRepository
import timber.log.Timber
import java.time.Instant
import java.util.UUID

@HiltViewModel
class CreateRequestViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _budget = MutableStateFlow("")
    val budget = _budget.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateDescription(newDesc: String) {
        _description.value = newDesc
    }

    fun updateCategory(newCategory: String) {
        _category.value = newCategory
    }

    fun updateBudget(newBudget: String) {
        _budget.value = newBudget
    }

    fun submitRequest() {
        if (validateForm()) {
            _loading.value = true
            viewModelScope.launch {
                try {
                    val currentUserId = authStateManager.getCurrentUserId()
                    if (currentUserId == null) {
                        _error.value = "Usuario no autenticado"
                        _loading.value = false
                        return@launch
                    }
                    
                    val currentTimestamp = Instant.now().toString()
                    val newId = UUID.randomUUID().toString()
                    
                    // Create service posting in Supabase
                    val supabasePosting = SupabaseServicePosting(
                        id = newId,
                        clientId = currentUserId,
                        title = _title.value,
                        description = _description.value,
                        categoryId = _category.value,
                        budget = _budget.value.toDoubleOrNull() ?: 0.0,
                        status = "OPEN",
                        createdAt = currentTimestamp,
                        updatedAt = currentTimestamp
                    )
                    
                    supabaseDatabaseService.insert("service_postings", supabasePosting)
                        .onSuccess {
                            _success.value = true
                            _error.value = null
                            Timber.d("Service posting created successfully with id: $newId")
                        }
                        .onFailure { exception ->
                            Timber.e(exception, "Error creating service posting")
                            _error.value = "Error: ${exception.message}"
                        }
                } catch (e: Exception) {
                    Timber.e(e, "Exception creating service posting")
                    _error.value = "Error: ${e.message}"
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (_title.value.isBlank()) {
            _error.value = "El título es requerido"
            return false
        }
        if (_description.value.isBlank()) {
            _error.value = "La descripción es requerida"
            return false
        }
        if (_category.value.isBlank()) {
            _error.value = "La categoría es requerida"
            return false
        }
        if (_budget.value.toDoubleOrNull() == null) {
            _error.value = "El presupuesto debe ser un número válido"
            return false
        }
        return true
    }

    fun clearForm() {
        _title.value = ""
        _description.value = ""
        _category.value = ""
        _budget.value = ""
        _error.value = null
        _success.value = false
    }
}
