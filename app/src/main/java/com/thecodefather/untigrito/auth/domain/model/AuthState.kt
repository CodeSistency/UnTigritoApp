package com.thecodefather.untigrito.auth.domain.model

import com.thecodefather.untigrito.domain.model.User

/**
 * Estados posibles de autenticación
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
