package com.thecodefather.untigrito.auth.domain.model

/**
 * Datos de verificación de identidad y teléfono
 */
data class VerificationData(
    val cedula: String = "",
    val cedulaImagePath: String? = null,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isPhoneVerified: Boolean = false,
    val isCedulaVerified: Boolean = false,
    val verificationSkipped: Boolean = false
)
