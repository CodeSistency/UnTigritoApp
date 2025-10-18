package com.thecodefather.untigrito.core.exception

/**
 * Excepción para errores de autenticación
 */
class AuthException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)
