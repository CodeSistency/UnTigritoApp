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

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError = _titleError.asStateFlow()

    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError = _descriptionError.asStateFlow()

    private val _categoryError = MutableStateFlow<String?>(null)
    val categoryError = _categoryError.asStateFlow()

    private val _budgetError = MutableStateFlow<String?>(null)
    val budgetError = _budgetError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid = _isFormValid.asStateFlow()

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        validateTitle(newTitle)
        checkFormValidity()
    }

    fun updateDescription(newDesc: String) {
        _description.value = newDesc
        validateDescription(newDesc)
        checkFormValidity()
    }

    fun updateCategory(newCategory: String) {
        _category.value = newCategory
        validateCategory(newCategory)
        checkFormValidity()
    }

    fun updateBudget(newBudget: String) {
        _budget.value = newBudget
        validateBudget(newBudget)
        checkFormValidity()
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
                    
                    supabaseDatabaseService.insert("ServicePosting", supabasePosting)
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

    private fun validateTitle(title: String) {
        _titleError.value = when {
            title.isBlank() -> "El título es requerido"
            title.length > 100 -> "El título no puede exceder 100 caracteres"
            else -> null
        }
    }

    private fun validateDescription(description: String) {
        _descriptionError.value = when {
            description.isBlank() -> "La descripción es requerida"
            description.length > 500 -> "La descripción no puede exceder 500 caracteres"
            else -> null
        }
    }

    private fun validateCategory(category: String) {
        _categoryError.value = if (category.isBlank()) "La categoría es requerida" else null
    }

    private fun validateBudget(budget: String) {
        _budgetError.value = when {
            budget.isBlank() -> "El presupuesto es requerido"
            budget.toDoubleOrNull() == null -> "El presupuesto debe ser un número válido"
            budget.toDoubleOrNull()?.let { it <= 0 } == true -> "El presupuesto debe ser mayor a 0"
            else -> null
        }
    }

    private fun checkFormValidity() {
        _isFormValid.value = _titleError.value == null &&
                _descriptionError.value == null &&
                _categoryError.value == null &&
                _budgetError.value == null &&
                _title.value.isNotBlank() &&
                _description.value.isNotBlank() &&
                _category.value.isNotBlank() &&
                _budget.value.isNotBlank()
    }

    private fun validateForm(): Boolean {
        validateTitle(_title.value)
        validateDescription(_description.value)
        validateCategory(_category.value)
        validateBudget(_budget.value)
        checkFormValidity()
        
        return _isFormValid.value
    }

    fun clearForm() {
        _title.value = ""
        _description.value = ""
        _category.value = ""
        _budget.value = ""
        _error.value = null
        _success.value = false
        _titleError.value = null
        _descriptionError.value = null
        _categoryError.value = null
        _budgetError.value = null
        _isFormValid.value = false
    }

    fun onSuccessHandled() {
        _success.value = false
    }
}
