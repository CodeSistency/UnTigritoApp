package com.thecodefather.untigrito.auth.domain.model

import com.thecodefather.untigrito.domain.model.User

/**
 * Estados posibles de autenticación
 * Incluye estados para diversos flujos de autenticación (login, OTP, verificación de ID, etc.)
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()

    // Estados de verificación OTP
    data class OtpSent(val message: String) : AuthState()
    object OtpVerified : AuthState()

    // Estados de verificación de ID
    object IdVerified : AuthState()
}
