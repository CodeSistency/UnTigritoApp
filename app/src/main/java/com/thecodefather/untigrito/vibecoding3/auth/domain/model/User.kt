package com.example.vibecoding3.auth.domain.model

/**
 * Modelo de usuario para la autenticación
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String = "",
    val cedula: String = "",
    val isPhoneVerified: Boolean = false,
    val isCedulaVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
