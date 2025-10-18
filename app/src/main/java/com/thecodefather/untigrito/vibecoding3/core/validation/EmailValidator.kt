package com.example.vibecoding3.core.validation

/**
 * Validador de correo electrónico
 */
object EmailValidator {
    private val emailPattern = Regex(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    )
    
    fun isValid(email: String): Boolean {
        return email.isNotBlank() && emailPattern.matches(email)
    }
}
