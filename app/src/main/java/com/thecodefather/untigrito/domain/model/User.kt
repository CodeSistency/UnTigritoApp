package com.thecodefather.untigrito.domain.model

/**
 * User domain model
 *
 * Represents a user in the system. This is a core business entity
 * used throughout the application's domain layer.
 *
 * @param id Unique identifier for the user
 * @param name Full name of the user
 * @param email Email address of the user
 * @param userType Type of user (CLIENT or PROFESSIONAL)
 * @param createdAt Timestamp when the user was created
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val userType: UserType,
    val phoneNumber: String = "",
    val cedula: String = "",
    val isPhoneVerified: Boolean = false,
    val isCedulaVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Enumeration of user types in the UnTigritoApp system
 */
enum class UserType {
    CLIENT,
    PROFESSIONAL
}
