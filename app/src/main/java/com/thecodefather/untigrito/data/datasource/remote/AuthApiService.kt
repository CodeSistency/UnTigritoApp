package com.thecodefather.untigrito.data.datasource.remote

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API service interface for authentication operations
 * Defines all authentication-related endpoints from the backend API
 */
interface AuthApiService {

    // ========== Basic Authentication ==========

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("/api/auth/logout")
    suspend fun logout(): ApiResponse<MessageResponse>

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<TokenResponse>

    // ========== Email Verification ==========

    @POST("/api/auth/verify-email")
    suspend fun verifyEmail(@Body request: EmailVerificationRequest): ApiResponse<MessageResponse>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<MessageResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiResponse<MessageResponse>

    // ========== Phone/OTP Verification ==========

    @POST("/api/user/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): ApiResponse<SendOtpResponse>

    @POST("/api/user/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): ApiResponse<VerifyOtpResponse>

    @POST("/api/user/verify-phone")
    suspend fun verifyPhone(@Body request: PhoneVerificationRequest): ApiResponse<PhoneVerificationResponse>

    // ========== ID Verification ==========

    @POST("/api/user/verify-id")
    suspend fun verifyId(@Body request: IdVerificationRequest): ApiResponse<IdVerificationResponse>

    // ========== Google OAuth ==========

    @POST("/api/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): ApiResponse<AuthResponse>

    // ========== Request/Response Data Classes ==========

    data class LoginRequest(
        val email: String? = null,
        val phone: String? = null,
        val password: String
    )

    data class RegisterRequest(
        val email: String? = null,
        val phone: String? = null,
        val password: String,
        val name: String,
        val role: String = "CLIENT"
    )

    data class RefreshTokenRequest(
        val token: String
    )

    data class EmailVerificationRequest(
        val token: String
    )

    data class ForgotPasswordRequest(
        val email: String
    )

    data class ResetPasswordRequest(
        val token: String,
        val newPassword: String
    )

    data class SendOtpRequest(
        val phoneNumber: String
    )

    data class VerifyOtpRequest(
        val phoneNumber: String,
        val otpCode: String
    )

    data class PhoneVerificationRequest(
        val phoneNumber: String
    )

    data class IdVerificationRequest(
        val cedula: String,
        val cedulaImage: String, // Base64 encoded
        val faceScanData: String // Base64 encoded
    )

    data class GoogleAuthRequest(
        val token: String,
        val idToken: String? = null
    )

    // ========== Response Data Classes ==========

    data class AuthResponse(
        val user: UserData,
        val token: String,
        val refreshToken: String? = null
    )

    data class TokenResponse(
        val token: String
    )

    data class SendOtpResponse(
        val message: String,
        val expiresIn: Int
    )

    data class VerifyOtpResponse(
        val message: String,
        val verified: Boolean
    )

    data class PhoneVerificationResponse(
        val message: String,
        val verified: Boolean
    )

    data class IdVerificationResponse(
        val message: String,
        val verified: Boolean,
        val verificationId: String? = null
    )

    data class UserData(
        val id: String,
        val email: String? = null,
        val phone: String? = null,
        val name: String? = null,
        val role: String,
        val isVerified: Boolean,
        val isIDVerified: Boolean,
        val balance: Double,
        val isSuspended: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val locationLat: Double? = null,
        val locationLng: Double? = null,
        val locationAddress: String? = null
    )

    data class MessageResponse(
        val message: String
    )

    data class ApiResponse<T>(
        val success: Boolean,
        val message: String,
        val data: T? = null,
        val error: ErrorData? = null
    )

    data class ErrorData(
        val code: String,
        val message: String,
        val details: Map<String, Any>? = null
    )
}
