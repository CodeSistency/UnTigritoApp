package com.thecodefather.untigrito.core.validation

/**
 * Validador de cédula de identidad
 */
object CedulaValidator {
    fun isValid(cedula: String): Boolean {
        val cleanCedula = cedula.replace(Regex("[^0-9]"), "")
        return cleanCedula.length == 8 && cleanCedula.all { it.isDigit() }
    }
}
