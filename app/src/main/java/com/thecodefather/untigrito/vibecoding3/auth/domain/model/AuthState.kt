package com.example.vibecoding3.auth.domain.model

/**
 * Estados posibles de autenticación
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
