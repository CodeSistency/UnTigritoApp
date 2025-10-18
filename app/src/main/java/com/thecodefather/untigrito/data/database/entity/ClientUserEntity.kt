package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for ClientUser
 * Represents a user stored in local database
 */
@Entity(tableName = "client_users")
data class ClientUserEntity(
    @PrimaryKey
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val role: String = "CLIENT",
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
