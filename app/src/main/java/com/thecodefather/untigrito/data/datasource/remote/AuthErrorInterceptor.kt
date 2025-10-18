package com.thecodefather.untigrito.data.datasource.remote

import com.thecodefather.untigrito.core.error.AuthErrorHandler
import com.thecodefather.untigrito.core.exception.AuthException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that handles authentication-specific HTTP errors and converts them to AuthExceptions
 */
@Singleton
class AuthErrorInterceptor @Inject constructor() : Interceptor {

    private val json = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        try {
            val response = chain.proceed(request)

            // Log successful auth requests
            if (isAuthRequest(url)) {
                Timber.d("🔐 AUTH INTERCEPTOR - ${request.method} ${url} -> ${response.code}")
            }

            // Handle error responses
            if (!response.isSuccessful) {
                Timber.w("🔐 AUTH INTERCEPTOR - Error response: ${response.code} for ${request.method} ${url}")

                // Try to parse error from response body
                val errorBody = response.body?.string()
                val authException = if (errorBody != null) {
                    try {
                        parseApiError(errorBody)
                    } catch (e: Exception) {
                        Timber.w("Failed to parse error response, using HTTP status: ${e.message}")
                        createExceptionFromHttpStatus(response.code, errorBody)
                    }
                } else {
                    createExceptionFromHttpStatus(response.code, "No error details provided")
                }

                throw authException
            }

            return response

        } catch (e: Exception) {
            // Re-throw AuthExceptions as-is
            if (e is AuthException) {
                throw e
            }

            // Convert other exceptions to AuthExceptions
            val authException = AuthErrorHandler.handleNetworkError(e)
            Timber.e("🔐 AUTH INTERCEPTOR - Network error for ${request.method} ${url}: ${authException.message}")
            throw authException
        }
    }

    /**
     * Checks if the request is authentication-related
     */
    private fun isAuthRequest(url: String): Boolean {
        return url.contains("/api/auth/") ||
               url.contains("/api/user/send-otp") ||
               url.contains("/api/user/verify-otp") ||
               url.contains("/api/user/verify-id") ||
               url.contains("/api/user/verify-phone")
    }

    /**
     * Parses API error response into AuthException
     */
    private fun parseApiError(errorBody: String): AuthException {
        return try {
            val jsonObject = json.parseToJsonElement(errorBody).jsonObject

            // Check if it's our API error format
            val errorData = if (jsonObject.containsKey("error")) {
                val errorObj = jsonObject["error"]?.jsonObject
                if (errorObj != null) {
                    AuthApiService.ErrorData(
                        code = errorObj["code"]?.jsonPrimitive?.content ?: "UNKNOWN_ERROR",
                        message = errorObj["message"]?.jsonPrimitive?.content ?: "Unknown error",
                        details = errorObj["details"] as? Map<String, Any>
                    )
                } else {
                    // Fallback: try to parse as simple error message
                    AuthApiService.ErrorData(
                        code = "UNKNOWN_ERROR",
                        message = jsonObject["message"]?.jsonPrimitive?.content ?: errorBody
                    )
                }
            } else {
                // Fallback: treat entire response as error message
                AuthApiService.ErrorData(
                    code = "UNKNOWN_ERROR",
                    message = errorBody
                )
            }

            AuthErrorHandler.handleApiError(errorData)

        } catch (e: Exception) {
            Timber.w("Failed to parse error JSON: ${e.message}, raw body: $errorBody")
            AuthException.UnknownError("Server error occurred")
        }
    }

    /**
     * Creates AuthException from HTTP status code
     */
    private fun createExceptionFromHttpStatus(statusCode: Int, message: String): AuthException {
        return when (statusCode) {
            400 -> AuthException.ValidationError("Bad request: $message")
            401 -> AuthException.InvalidCredentials("Authentication required")
            403 -> AuthException.AccountSuspended("Access forbidden")
            404 -> AuthException.ServerError("Service not found")
            408 -> AuthException.TimeoutError("Request timed out")
            409 -> AuthException.EmailAlreadyExists("Resource conflict")
            422 -> AuthException.ValidationError("Validation failed: $message")
            429 -> AuthException.ServerError("Too many requests")
            in 500..599 -> AuthException.ServerError("Server error")
            else -> AuthException.UnknownError("HTTP $statusCode: $message")
        }
    }
}
