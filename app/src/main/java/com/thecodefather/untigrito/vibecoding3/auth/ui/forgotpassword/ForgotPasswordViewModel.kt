package com.example.vibecoding3.auth.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibecoding3.auth.domain.repository.IAuthRepository
import com.example.vibecoding3.core.validation.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para recuperación de contraseña
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Solicita recuperación de contraseña
     */
    fun requestPasswordReset(email: String) {
        if (!EmailValidator.isValid(email)) {
            _errorMessage.value = "Email inválido"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            val result = authRepository.forgotPassword(email)
            
            result.onSuccess {
                _successMessage.value = "Se ha enviado un enlace de recuperación a tu email"
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Error en recuperación"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Limpia mensajes
     */
    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}
