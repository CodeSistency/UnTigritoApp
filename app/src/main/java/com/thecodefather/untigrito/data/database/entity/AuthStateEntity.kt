package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for persisting authentication state locally
 */
@Entity(tableName = "auth_state")
data class AuthStateEntity(
    @PrimaryKey
    val id: Int = 1, // Always use ID 1 for single auth state record

    // Authentication state
    val authState: String, // "Authenticated", "Unauthenticated", etc.

    // User information (null if not authenticated)
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhone: String? = null,
    val userType: String? = null, // "CLIENT", "PROFESSIONAL"

    // Verification status
    val isPhoneVerified: Boolean = false,
    val isCedulaVerified: Boolean = false,

    // Metadata
    val timestamp: Long = System.currentTimeMillis()
)