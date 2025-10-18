package com.example.vibecoding3.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibecoding3.auth.domain.model.AuthState
import com.example.vibecoding3.auth.domain.model.User
import com.example.vibecoding3.auth.domain.repository.IAuthRepository
import com.example.vibecoding3.core.validation.EmailValidator
import com.example.vibecoding3.core.validation.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el flujo de autenticación (login, register, forgot password)
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()
    
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
    
    /**
     * Realiza login con email y contraseña
     */
    fun login(email: String, password: String) {
        // Validar datos
        if (!validateLoginData(email, password)) return
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            
            result.onSuccess { user ->
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
            }
        }
    }
    
    /**
     * Realiza registro de nuevo usuario
     */
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        // Validar datos
        if (!validateRegisterData(name, email, password, confirmPassword)) return
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.register(name, email, password)
            
            result.onSuccess { user ->
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
            }
        }
    }
    
    /**
     * Solicita recuperación de contraseña
     */
    fun forgotPassword(email: String) {
        if (!EmailValidator.isValid(email)) {
            _emailError.value = "Email inválido"
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.forgotPassword(email)
            
            result.onSuccess {
                _authState.value = AuthState.Unauthenticated
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
            }
        }
    }
    
    /**
     * Realiza logout
     */
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
        clearErrors()
    }
    
    /**
     * Valida datos de login
     */
    private fun validateLoginData(email: String, password: String): Boolean {
        var isValid = true
        
        if (!EmailValidator.isValid(email)) {
            _emailError.value = "Email inválido"
            isValid = false
        } else {
            _emailError.value = null
        }
        
        if (!PasswordValidator.isValid(password)) {
            _passwordError.value = "Contraseña mínimo 6 caracteres"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        return isValid
    }
    
    /**
     * Valida datos de registro
     */
    private fun validateRegisterData(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            isValid = false
        }
        
        if (!EmailValidator.isValid(email)) {
            _emailError.value = "Email inválido"
            isValid = false
        } else {
            _emailError.value = null
        }
        
        if (!PasswordValidator.doPasswordsMatch(password, confirmPassword)) {
            _passwordError.value = "Las contraseñas no coinciden"
            isValid = false
        } else if (!PasswordValidator.isValid(password)) {
            _passwordError.value = "Contraseña mínimo 6 caracteres"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        return isValid
    }
    
    /**
     * Limpia los errores
     */
    fun clearErrors() {
        _emailError.value = null
        _passwordError.value = null
    }
    
    /**
     * Valida email en tiempo real
     */
    fun validateEmail(email: String) {
        _emailError.value = if (EmailValidator.isValid(email)) null else "Email inválido"
    }
    
    /**
     * Valida contraseña en tiempo real
     */
    fun validatePassword(password: String) {
        _passwordError.value = if (PasswordValidator.isValid(password)) null else "Mínimo 6 caracteres"
    }
}
