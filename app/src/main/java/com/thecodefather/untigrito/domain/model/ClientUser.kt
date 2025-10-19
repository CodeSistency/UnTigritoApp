package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser

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

// ========== Extension Functions for Supabase Integration ==========

/**
 * Converts SupabaseUser to ClientUser domain model
 */
fun SupabaseUser.toClientUser(): ClientUser {
    return ClientUser(
        id = this.id,
        email = this.email,
        phone = this.phone,
        name = this.name,
        role = this.role,
        isVerified = this.isVerified,
        isIDVerified = this.isIDVerified,
        balance = this.balance,
        isSuspended = this.isSuspended,
        locationLat = this.locationLat,
        locationLng = this.locationLng,
        locationAddress = this.locationAddress,
        createdAt = this.createdAt ?: "",
        updatedAt = this.updatedAt ?: ""
    )
}

/**
 * Converts ClientUser domain model to SupabaseUser
 */
fun ClientUser.toSupabaseUser(): SupabaseUser {
    return SupabaseUser(
        id = this.id,
        email = this.email,
        phone = this.phone,
        name = this.name,
        role = this.role,
        isVerified = this.isVerified,
        isIDVerified = this.isIDVerified,
        balance = this.balance,
        isSuspended = this.isSuspended,
        locationLat = this.locationLat,
        locationLng = this.locationLng,
        locationAddress = this.locationAddress,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
