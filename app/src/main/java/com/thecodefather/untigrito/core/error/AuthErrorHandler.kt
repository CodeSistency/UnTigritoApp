package com.thecodefather.untigrito.core.error

import com.thecodefather.untigrito.core.exception.AuthException
import com.thecodefather.untigrito.data.datasource.remote.AuthApiService
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Handles authentication errors and translates them into user-friendly messages
 */
object AuthErrorHandler {

    /**
     * Translates API error responses into AuthException instances
     */
    fun handleApiError(error: AuthApiService.ErrorData): AuthException {
        Timber.w("🔐 AUTH ERROR HANDLER - API Error: ${error.code} - ${error.message}")

        return when (error.code) {
            // Authentication errors
            "INVALID_CREDENTIALS" -> AuthException.InvalidCredentials()
            "ACCOUNT_SUSPENDED" -> AuthException.AccountSuspended()
            "EMAIL_NOT_VERIFIED" -> AuthException.EmailNotVerified()

            // Token errors
            "TOKEN_EXPIRED" -> AuthException.TokenExpired()
            "INVALID_TOKEN" -> AuthException.InvalidToken()

            // OTP errors
            "INVALID_OTP" -> AuthException.InvalidOtp()
            "OTP_EXPIRED" -> AuthException.OtpExpired()
            "OTP_ATTEMPTS_EXCEEDED" -> AuthException.OtpAttemptsExceeded()

            // ID verification errors
            "INVALID_ID_DOCUMENT" -> AuthException.InvalidIdDocument()
            "ID_VERIFICATION_FAILED" -> AuthException.IdVerificationFailed()

            // Registration errors
            "EMAIL_ALREADY_EXISTS" -> AuthException.EmailAlreadyExists()
            "PHONE_ALREADY_EXISTS" -> AuthException.PhoneAlreadyExists()
            "WEAK_PASSWORD" -> AuthException.WeakPassword()

            // Server errors
            "SERVER_ERROR" -> AuthException.ServerError()
            "SERVICE_UNAVAILABLE" -> AuthException.ServiceUnavailable()

            // Validation errors
            "VALIDATION_ERROR" -> {
                val fieldErrors = error.details?.mapValues { it.value.toString() } ?: emptyMap()
                AuthException.ValidationError(error.message, fieldErrors)
            }

            // Default to unknown error
            else -> AuthException.UnknownError(error.message)
        }
    }

    /**
     * Handles network and HTTP exceptions
     */
    fun handleNetworkError(exception: Throwable): AuthException {
        Timber.e("🔐 AUTH ERROR HANDLER - Network Error: ${exception::class.java.simpleName} - ${exception.message}")

        return when (exception) {
            is HttpException -> handleHttpException(exception)
            is SocketTimeoutException -> AuthException.TimeoutError()
            is UnknownHostException -> AuthException.NetworkError("No internet connection")
            is IOException -> AuthException.NetworkError("Network connection failed")
            else -> AuthException.UnknownError("Unexpected error occurred", exception)
        }
    }

    /**
     * Handles HTTP exceptions specifically
     */
    private fun handleHttpException(exception: HttpException): AuthException {
        return when (exception.code()) {
            400 -> AuthException.ValidationError("Invalid request data")
            401 -> AuthException.InvalidCredentials("Authentication failed")
            403 -> AuthException.AccountSuspended("Access forbidden")
            404 -> AuthException.ServerError("Service not found")
            408 -> AuthException.TimeoutError("Request timed out")
            409 -> AuthException.EmailAlreadyExists("Resource already exists")
            422 -> AuthException.ValidationError("Validation failed")
            429 -> AuthException.ServerError("Too many requests, please wait")
            in 500..599 -> AuthException.ServerError("Server temporarily unavailable")
            else -> AuthException.ServerError("Server error (${exception.code()})")
        }
    }

    /**
     * Gets user-friendly error message from AuthException
     */
    fun getUserFriendlyMessage(exception: AuthException): String {
        return when (exception) {
            is AuthException.ValidationError -> {
                if (exception.fieldErrors.isNotEmpty()) {
                    // Return the first field error if available
                    exception.fieldErrors.values.firstOrNull() ?: exception.message ?: "Validation error"
                } else {
                    exception.message ?: "Validation error"
                }
            }
            else -> exception.message ?: "Unknown error occurred"
        }
    }

    /**
     * Gets error code from AuthException for analytics/logging
     */
    fun getErrorCode(exception: AuthException): String {
        return exception.errorCode
    }

    /**
     * Determines if error is recoverable (user can retry)
     */
    fun isRecoverableError(exception: AuthException): Boolean {
        return when (exception) {
            is AuthException.NetworkError,
            is AuthException.TimeoutError,
            is AuthException.ServerError,
            is AuthException.ServiceUnavailable -> true

            is AuthException.GoogleSignInFailed,
            is AuthException.GoogleSignInCancelled -> true

            else -> false
        }
    }

    /**
     * Determines if error requires user to re-authenticate
     */
    fun requiresReAuthentication(exception: AuthException): Boolean {
        return when (exception) {
            is AuthException.TokenExpired,
            is AuthException.InvalidToken,
            is AuthException.AccountSuspended -> true

            else -> false
        }
    }

    /**
     * Gets recovery action suggestion for the user
     */
    fun getRecoverySuggestion(exception: AuthException): String {
        return when (exception) {
            is AuthException.NetworkError -> "Check your internet connection and try again"
            is AuthException.TimeoutError -> "Connection is slow, please try again"
            is AuthException.ServerError -> "Server is temporarily unavailable, please try again later"
            is AuthException.ServiceUnavailable -> "Service is under maintenance, please try again later"

            is AuthException.TokenExpired -> "Your session has expired, please login again"
            is AuthException.InvalidToken -> "Authentication failed, please login again"
            is AuthException.AccountSuspended -> "Your account has been suspended, contact support"

            is AuthException.InvalidCredentials -> "Please check your email/phone and password"
            is AuthException.EmailNotVerified -> "Please verify your email address first"
            is AuthException.EmailAlreadyExists -> "This email is already registered, try logging in instead"
            is AuthException.PhoneAlreadyExists -> "This phone number is already registered"

            is AuthException.InvalidOtp -> "Please check the verification code and try again"
            is AuthException.OtpExpired -> "The code has expired, please request a new one"
            is AuthException.OtpAttemptsExceeded -> "Too many attempts, please wait and request a new code"

            is AuthException.IdVerificationFailed -> "ID verification failed, please try again with better photos"
            is AuthException.InvalidIdDocument -> "Invalid ID document, please check and try again"

            is AuthException.GoogleSignInFailed -> "Google sign-in failed, please try again"
            is AuthException.GoogleSignInCancelled -> "Sign-in was cancelled, please try again"

            else -> "Please try again or contact support if the problem persists"
        }
    }
}
