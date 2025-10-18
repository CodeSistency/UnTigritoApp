package com.example.vibecoding3.core.exception

/**
 * Excepción para errores de autenticación
 */
class AuthException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)
