package com.thecodefather.untigrito.core.validation

/**
 * Validador de teléfono para formato venezolano
 */
object PhoneValidator {
    fun isValidVenezuelanPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        return cleanPhone.length == 11 && cleanPhone.startsWith("04")
    }
    
    fun formatVenezuelanPhone(phone: String): String {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        return if (cleanPhone.length == 11) {
            "+58 ${cleanPhone.substring(1, 4)} ${cleanPhone.substring(4, 7)}-${cleanPhone.substring(7)}"
        } else {
            phone
        }
    }
}
