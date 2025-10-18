package com.thecodefather.untigrito.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User database entity for Room
 *
 * Represents the user table in the local SQLite database.
 * This is the data class used by Room for database operations.
 *
 * @param id Primary key - unique identifier
 * @param name User's full name
 * @param email User's email address
 * @param userType Type of user (CLIENT or PROFESSIONAL)
 * @param createdAt Timestamp of creation
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val userType: String, // CLIENT or PROFESSIONAL
    val createdAt: Long
)

