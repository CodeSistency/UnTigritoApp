package com.thecodefather.untigrito.domain.model

/**
 * Extended User model for client module
 * Represents a user with client-specific fields
 */
data class ClientUser(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val role: String = "CLIENT", // CLIENT, PROFESSIONAL, ADMIN
    val isVerified: Boolean = false,
    val isIDVerified: Boolean = false,
    val balance: Double = 0.0,
    val isSuspended: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationAddress: String? = null
)
