package com.thecodefather.untigrito.auth.domain.repository

import com.thecodefather.untigrito.domain.model.User

/**
 * Interface for authentication repository
 * Provides all authentication operations including login, registration, verification, and token management
 */
interface IAuthRepository {

    // ========== Basic Authentication ==========
    suspend fun login(email: String?, phone: String?, password: String): Result<User>
    suspend fun register(name: String, email: String?, phone: String?, password: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Boolean>
    fun logout()
    fun getCurrentUser(): User?
    fun isAuthenticated(): Boolean
    fun shouldRefreshToken(): Boolean

    // ========== Email Verification ==========
    suspend fun resetPassword(token: String, newPassword: String): Result<Boolean>
    suspend fun verifyEmail(token: String): Result<Boolean>

    // ========== Phone/OTP Verification ==========
    suspend fun sendOtp(phoneNumber: String): Result<String>
    suspend fun verifyOtp(phoneNumber: String, otpCode: String): Result<Boolean>

    // ========== ID Verification ==========
    suspend fun verifyId(cedula: String, cedulaImage: String, faceScanData: String): Result<Boolean>

    // ========== Token Management ==========
    suspend fun refreshToken(): Result<String>

    // ========== Social Authentication ==========
    suspend fun googleAuth(token: String, idToken: String?): Result<User>
}
