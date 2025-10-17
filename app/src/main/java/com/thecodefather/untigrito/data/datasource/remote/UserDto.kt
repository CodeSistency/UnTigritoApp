package com.thecodefather.untigrito.data.datasource.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for User API responses
 *
 * Represents the user data structure as received from the API.
 * Uses kotlinx serialization for JSON serialization/deserialization.
 *
 * @param id User's unique identifier
 * @param name User's full name
 * @param email User's email address
 * @param userType Type of user (CLIENT or PROFESSIONAL)
 * @param createdAt Timestamp of user creation
 */
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    @SerialName("user_type")
    val userType: String,
    @SerialName("created_at")
    val createdAt: Long
)
