package com.thecodefather.untigrito.core.exception

/**
 * Authentication-specific exceptions with error codes for better error handling
 */
sealed class AuthException(
    message: String,
    val errorCode: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    companion object {
        // Error code constants
        const val NETWORK_ERROR = "NETWORK_ERROR"
        const val TIMEOUT_ERROR = "TIMEOUT_ERROR"
        const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
        const val ACCOUNT_SUSPENDED = "ACCOUNT_SUSPENDED"
        const val EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"
        const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
        const val INVALID_TOKEN = "INVALID_TOKEN"
        const val INVALID_OTP = "INVALID_OTP"
        const val OTP_EXPIRED = "OTP_EXPIRED"
        const val OTP_ATTEMPTS_EXCEEDED = "OTP_ATTEMPTS_EXCEEDED"
        const val INVALID_ID_DOCUMENT = "INVALID_ID_DOCUMENT"
        const val ID_VERIFICATION_FAILED = "ID_VERIFICATION_FAILED"
        const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
        const val PHONE_ALREADY_EXISTS = "PHONE_ALREADY_EXISTS"
        const val WEAK_PASSWORD = "WEAK_PASSWORD"
        const val GOOGLE_SIGN_IN_FAILED = "GOOGLE_SIGN_IN_FAILED"
        const val GOOGLE_SIGN_IN_CANCELLED = "GOOGLE_SIGN_IN_CANCELLED"
        const val SERVER_ERROR = "SERVER_ERROR"
        const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
        const val VALIDATION_ERROR = "VALIDATION_ERROR"
        const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
    }

    // Network errors
    class NetworkError(message: String = "Network connection failed", cause: Throwable? = null)
        : AuthException(message, NETWORK_ERROR, cause)

    class TimeoutError(message: String = "Request timed out", cause: Throwable? = null)
        : AuthException(message, TIMEOUT_ERROR, cause)

    // Authentication errors
    class InvalidCredentials(message: String = "Invalid email/phone or password")
        : AuthException(message, INVALID_CREDENTIALS)

    class AccountSuspended(message: String = "Account has been suspended")
        : AuthException(message, ACCOUNT_SUSPENDED)

    class EmailNotVerified(message: String = "Please verify your email address")
        : AuthException(message, EMAIL_NOT_VERIFIED)

    // Token errors
    class TokenExpired(message: String = "Session expired, please login again")
        : AuthException(message, TOKEN_EXPIRED)

    class InvalidToken(message: String = "Invalid authentication token")
        : AuthException(message, INVALID_TOKEN)

    // OTP errors
    class InvalidOtp(message: String = "Invalid verification code")
        : AuthException(message, INVALID_OTP)

    class OtpExpired(message: String = "Verification code has expired")
        : AuthException(message, OTP_EXPIRED)

    class OtpAttemptsExceeded(message: String = "Too many failed attempts, please request a new code")
        : AuthException(message, OTP_ATTEMPTS_EXCEEDED)

    // ID verification errors
    class InvalidIdDocument(message: String = "Invalid ID document")
        : AuthException(message, INVALID_ID_DOCUMENT)

    class IdVerificationFailed(message: String = "ID verification failed, please try again")
        : AuthException(message, ID_VERIFICATION_FAILED)

    // Registration errors
    class EmailAlreadyExists(message: String = "Email address is already registered")
        : AuthException(message, EMAIL_ALREADY_EXISTS)

    class PhoneAlreadyExists(message: String = "Phone number is already registered")
        : AuthException(message, PHONE_ALREADY_EXISTS)

    class WeakPassword(message: String = "Password does not meet requirements")
        : AuthException(message, WEAK_PASSWORD)

    // Google OAuth errors
    class GoogleSignInFailed(message: String = "Google sign-in failed")
        : AuthException(message, GOOGLE_SIGN_IN_FAILED)

    class GoogleSignInCancelled(message: String = "Google sign-in was cancelled")
        : AuthException(message, GOOGLE_SIGN_IN_CANCELLED)

    // Server errors
    class ServerError(message: String = "Server error, please try again later")
        : AuthException(message, SERVER_ERROR)

    class ServiceUnavailable(message: String = "Service temporarily unavailable")
        : AuthException(message, SERVICE_UNAVAILABLE)

    // Validation errors
    class ValidationError(message: String = "Validation failed", val fieldErrors: Map<String, String> = emptyMap())
        : AuthException(message, VALIDATION_ERROR)

    // Unknown errors
    class UnknownError(message: String = "An unexpected error occurred", cause: Throwable? = null)
        : AuthException(message, UNKNOWN_ERROR, cause)
}
