package com.thecodefather.untigrito.core.exception

/**
 * Excepción para errores de validación
 */
class ValidationException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)
