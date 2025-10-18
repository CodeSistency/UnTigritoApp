package com.example.vibecoding3.core.exception

/**
 * Excepción para errores de validación
 */
class ValidationException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)
