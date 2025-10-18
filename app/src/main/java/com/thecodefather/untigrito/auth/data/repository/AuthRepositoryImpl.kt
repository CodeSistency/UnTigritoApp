package com.thecodefather.untigrito.auth.data.repository

import com.thecodefather.untigrito.data.datasource.remote.AuthApiService
import com.thecodefather.untigrito.data.datasource.local.TokenManager
import com.thecodefather.untigrito.domain.model.User
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import com.thecodefather.untigrito.domain.model.UserType
import com.thecodefather.untigrito.core.exception.AuthException
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of authentication repository with real API integration
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : IAuthRepository {

    override suspend fun login(email: String?, phone: String?, password: String): Result<User> {
        return try {
            Timber.d("🔐 AUTH LOGIN_START - Attempting login with identifier: ${email ?: phone}")

            val request = AuthApiService.LoginRequest(
                email = email,
                phone = phone,
                password = password
            )

            val response = authApiService.login(request)

            if (response.success && response.data != null) {
                val apiUser = response.data.user
                val user = mapApiUserToDomainUser(apiUser)

                // Save tokens securely
                response.data.token?.let { accessToken ->
                    response.data.refreshToken?.let { refreshToken ->
                        tokenManager.saveTokens(accessToken, refreshToken)
                    } ?: tokenManager.saveTokens(accessToken)
                }

                Timber.d("✅ AUTH LOGIN_SUCCESS - User: ${user.id}, Role: ${user.userType}")
                Result.success(user)
            } else {
                val errorMessage = response.error?.message ?: "Login failed"
                Timber.w("⚠️ AUTH LOGIN_FAILED - $errorMessage")
                Result.failure(AuthException.InvalidCredentials(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH LOGIN_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH LOGIN_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Login failed unexpectedly", e))
        }
    }

    override suspend fun register(
        name: String,
        email: String?,
        phone: String?,
        password: String
    ): Result<User> {
        return try {
            Timber.d("🔐 AUTH REGISTER_START - Registering user: $name")

            val request = AuthApiService.RegisterRequest(
                email = email,
                phone = phone,
                password = password,
                name = name,
                role = "CLIENT" // Default role for new registrations
            )

            val response = authApiService.register(request)

            if (response.success && response.data != null) {
                val apiUser = response.data.user
                val user = mapApiUserToDomainUser(apiUser)

                // Save tokens securely
                response.data.token?.let { accessToken ->
                    response.data.refreshToken?.let { refreshToken ->
                        tokenManager.saveTokens(accessToken, refreshToken)
                    } ?: tokenManager.saveTokens(accessToken)
                }

                Timber.d("✅ AUTH REGISTER_SUCCESS - User: ${user.id}, Role: ${user.userType}")
                Result.success(user)
            } else {
                val errorMessage = response.error?.message ?: "Registration failed"
                Timber.w("⚠️ AUTH REGISTER_FAILED - $errorMessage")
                Result.failure(AuthException.EmailAlreadyExists(errorMessage)) // Default to email conflict
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH REGISTER_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH REGISTER_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Registration failed unexpectedly", e))
        }
    }

    override suspend fun forgotPassword(email: String): Result<Boolean> {
        return try {
            Timber.d("🔐 AUTH FORGOT_PASSWORD_START - Email: $email")

            val request = AuthApiService.ForgotPasswordRequest(email = email)
            val response = authApiService.forgotPassword(request)

            if (response.success) {
                Timber.d("✅ AUTH FORGOT_PASSWORD_SUCCESS")
                Result.success(true)
            } else {
                val errorMessage = response.error?.message ?: "Forgot password failed"
                Timber.w("⚠️ AUTH FORGOT_PASSWORD_FAILED - $errorMessage")
                Result.failure(AuthException.ServerError(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH FORGOT_PASSWORD_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH FORGOT_PASSWORD_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Forgot password failed unexpectedly", e))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> {
        return try {
            Timber.d("🔐 AUTH RESET_PASSWORD_START")

            val request = AuthApiService.ResetPasswordRequest(
                token = token,
                newPassword = newPassword
            )
            val response = authApiService.resetPassword(request)

            if (response.success) {
                Timber.d("✅ AUTH RESET_PASSWORD_SUCCESS")
                Result.success(true)
            } else {
                val errorMessage = response.error?.message ?: "Reset password failed"
                Timber.w("⚠️ AUTH RESET_PASSWORD_FAILED - $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH RESET_PASSWORD_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH RESET_PASSWORD_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Reset password failed unexpectedly", e))
        }
    }

    override suspend fun verifyEmail(token: String): Result<Boolean> {
        return try {
            Timber.d("🔐 AUTH VERIFY_EMAIL_START")

            val request = AuthApiService.EmailVerificationRequest(token = token)
            val response = authApiService.verifyEmail(request)

            if (response.success) {
                Timber.d("✅ AUTH VERIFY_EMAIL_SUCCESS")
                Result.success(true)
            } else {
                val errorMessage = response.error?.message ?: "Email verification failed"
                Timber.w("⚠️ AUTH VERIFY_EMAIL_FAILED - $errorMessage")
                Result.failure(AuthException.ServerError(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH VERIFY_EMAIL_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH VERIFY_EMAIL_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Email verification failed unexpectedly", e))
        }
    }

    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return Result.failure(AuthException.InvalidToken("No refresh token available"))

            Timber.d("🔐 AUTH REFRESH_TOKEN_START")

            val request = AuthApiService.RefreshTokenRequest(token = refreshToken)
            val response = authApiService.refreshToken(request)

            if (response.success && response.data != null) {
                val newAccessToken = response.data.token
                tokenManager.saveTokens(newAccessToken, refreshToken)

                Timber.d("✅ AUTH REFRESH_TOKEN_SUCCESS")
                Result.success(newAccessToken)
            } else {
                val errorMessage = response.error?.message ?: "Token refresh failed"
                Timber.w("⚠️ AUTH REFRESH_TOKEN_FAILED - $errorMessage")
                // Clear tokens if refresh fails
                tokenManager.clearTokens()
                Result.failure(AuthException.TokenExpired(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH REFRESH_TOKEN_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            tokenManager.clearTokens()
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH REFRESH_TOKEN_ERROR - Unexpected error: ${e.message}")
            tokenManager.clearTokens()
            Result.failure(AuthException.UnknownError("Token refresh failed unexpectedly", e))
        }
    }

    override suspend fun sendOtp(phoneNumber: String): Result<String> {
        return try {
            Timber.d("🔐 AUTH SEND_OTP_START - Phone: $phoneNumber")

            val request = AuthApiService.SendOtpRequest(phoneNumber = phoneNumber)
            val response = authApiService.sendOtp(request)

            if (response.success && response.data != null) {
                Timber.d("✅ AUTH SEND_OTP_SUCCESS - Expires in: ${response.data.expiresIn}s")
                Result.success(response.data.message)
            } else {
                val errorMessage = response.error?.message ?: "Send OTP failed"
                Timber.w("⚠️ AUTH SEND_OTP_FAILED - $errorMessage")
                Result.failure(AuthException.ServerError(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH SEND_OTP_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH SEND_OTP_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Send OTP failed unexpectedly", e))
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otpCode: String): Result<Boolean> {
        return try {
            Timber.d("🔐 AUTH VERIFY_OTP_START - Phone: $phoneNumber")

            val request = AuthApiService.VerifyOtpRequest(
                phoneNumber = phoneNumber,
                otpCode = otpCode
            )
            val response = authApiService.verifyOtp(request)

            if (response.success && response.data != null) {
                Timber.d("✅ AUTH VERIFY_OTP_SUCCESS - Verified: ${response.data.verified}")
                Result.success(response.data.verified)
            } else {
                val errorMessage = response.error?.message ?: "Verify OTP failed"
                Timber.w("⚠️ AUTH VERIFY_OTP_FAILED - $errorMessage")
                Result.failure(AuthException.InvalidOtp(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH VERIFY_OTP_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH VERIFY_OTP_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Verify OTP failed unexpectedly", e))
        }
    }

    override suspend fun verifyId(cedula: String, cedulaImage: String, faceScanData: String): Result<Boolean> {
        return try {
            Timber.d("🔐 AUTH VERIFY_ID_START - Cedula: $cedula")

            val request = AuthApiService.IdVerificationRequest(
                cedula = cedula,
                cedulaImage = cedulaImage,
                faceScanData = faceScanData
            )
            val response = authApiService.verifyId(request)

            if (response.success && response.data != null) {
                Timber.d("✅ AUTH VERIFY_ID_SUCCESS - Verified: ${response.data.verified}")
                Result.success(response.data.verified)
            } else {
                val errorMessage = response.error?.message ?: "ID verification failed"
                Timber.w("⚠️ AUTH VERIFY_ID_FAILED - $errorMessage")
                Result.failure(AuthException.IdVerificationFailed(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH VERIFY_ID_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH VERIFY_ID_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("ID verification failed unexpectedly", e))
        }
    }

    override suspend fun googleAuth(token: String, idToken: String?): Result<User> {
        return try {
            Timber.d("🔐 AUTH GOOGLE_AUTH_START")

            val request = AuthApiService.GoogleAuthRequest(
                token = token,
                idToken = idToken
            )
            val response = authApiService.googleAuth(request)

            if (response.success && response.data != null) {
                val apiUser = response.data.user
                val user = mapApiUserToDomainUser(apiUser)

                // Save tokens securely
                response.data.token?.let { accessToken ->
                    response.data.refreshToken?.let { refreshToken ->
                        tokenManager.saveTokens(accessToken, refreshToken)
                    } ?: tokenManager.saveTokens(accessToken)
                }

                Timber.d("✅ AUTH GOOGLE_AUTH_SUCCESS - User: ${user.id}, Role: ${user.userType}")
                Result.success(user)
            } else {
                val errorMessage = response.error?.message ?: "Google authentication failed"
                Timber.w("⚠️ AUTH GOOGLE_AUTH_FAILED - $errorMessage")
                Result.failure(AuthException.GoogleSignInFailed(errorMessage))
            }
        } catch (e: AuthException) {
            Timber.w("⚠️ AUTH GOOGLE_AUTH_FAILED - AuthException: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e("❌ AUTH GOOGLE_AUTH_ERROR - Unexpected error: ${e.message}")
            Result.failure(AuthException.UnknownError("Google authentication failed unexpectedly", e))
        }
    }

    override fun logout() {
        Timber.d("🔐 AUTH LOGOUT - Clearing tokens and user data")
        tokenManager.clearTokens()
    }

    override fun getCurrentUser(): User? {
        val userInfo = tokenManager.getUserFromToken()
        return if (userInfo != null) {
            User(
                id = userInfo.userId,
                name = "", // Would need to be cached or fetched from API
                email = userInfo.email ?: "",
                phoneNumber = userInfo.phone ?: "",
                userType = when (userInfo.role) {
                    "PROFESSIONAL" -> UserType.PROFESSIONAL
                    else -> UserType.CLIENT
                }
            )
        } else null
    }

    override fun isAuthenticated(): Boolean {
        return tokenManager.isAccessTokenValid()
    }

    override fun shouldRefreshToken(): Boolean {
        return tokenManager.shouldRefreshToken()
    }

    /**
     * Maps API user model to domain user model
     */
    private fun mapApiUserToDomainUser(apiUser: AuthApiService.UserData): User {
        return User(
            id = apiUser.id,
            name = apiUser.name ?: "",
            email = apiUser.email ?: "",
            phoneNumber = apiUser.phone ?: "",
            userType = when (apiUser.role) {
                "PROFESSIONAL" -> UserType.PROFESSIONAL
                else -> UserType.CLIENT
            },
            isPhoneVerified = apiUser.isVerified, // Assuming API verified field applies to phone
            isCedulaVerified = apiUser.isIDVerified,
            createdAt = System.currentTimeMillis() // API might provide this, using current time for now
        )
    }
}
