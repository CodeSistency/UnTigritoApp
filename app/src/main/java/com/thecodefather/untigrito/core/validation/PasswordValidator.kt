package com.thecodefather.untigrito.core.validation

/**
 * Validador de contraseña
 */
object PasswordValidator {
    private const val MIN_LENGTH = 6
    
    fun isValid(password: String): Boolean {
        return password.length >= MIN_LENGTH
    }
    
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && isValid(password)
    }
}
