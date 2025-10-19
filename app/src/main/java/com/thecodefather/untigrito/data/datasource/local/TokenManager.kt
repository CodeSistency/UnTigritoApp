package com.thecodefather.untigrito.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Token manager for handling JWT authentication tokens securely
 * Provides encrypted storage, validation, and automatic refresh capabilities
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if we have valid tokens on initialization
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()

        if (accessToken != null && refreshToken != null) {
            if (isTokenValid(accessToken)) {
                _authState.value = AuthState.Authenticated
            } else if (isTokenValid(refreshToken)) {
                // Token expired but refresh token is valid
                _authState.value = AuthState.TokenExpired
            } else {
                // Both tokens invalid, clear them
                clearTokens()
            }
        }
    }

    /**
     * Save authentication tokens
     */
    fun saveTokens(accessToken: String, refreshToken: String? = null) {
        sharedPreferences.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .putLong(TOKEN_TIMESTAMP_KEY, System.currentTimeMillis())
            .apply()

        _authState.value = AuthState.Authenticated
    }

    /**
     * Get stored access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    /**
     * Get stored refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    /**
     * Check if access token is valid (not expired)
     */
    fun isAccessTokenValid(): Boolean {
        val token = getAccessToken() ?: return false
        return isTokenValid(token)
    }

    /**
     * Check if refresh token is valid
     */
    fun isRefreshTokenValid(): Boolean {
        val token = getRefreshToken() ?: return false
        return isTokenValid(token)
    }

    /**
     * Validate JWT token (check expiration and basic structure)
     */
    private fun isTokenValid(token: String): Boolean {
        return try {
            val decoded = JWT.decode(token)
            val expiresAt = decoded.expiresAt?.time ?: return false
            expiresAt > System.currentTimeMillis()
        } catch (e: JWTDecodeException) {
            false
        }
    }

    /**
     * Extract user information from access token
     */
    fun getUserFromToken(): UserInfo? {
        val token = getAccessToken() ?: return null
        return try {
            val decoded = JWT.decode(token)
            UserInfo(
                userId = decoded.subject ?: return null,
                role = decoded.getClaim("role")?.asString() ?: "CLIENT",
                email = decoded.getClaim("email")?.asString(),
                phone = decoded.getClaim("phone")?.asString()
            )
        } catch (e: JWTDecodeException) {
            null
        }
    }

    /**
     * Check if user needs to refresh token soon (within 5 minutes)
     */
    fun shouldRefreshToken(): Boolean {
        val token = getAccessToken() ?: return false
        return try {
            val decoded = JWT.decode(token)
            val expiresAt = decoded.expiresAt?.time ?: return true
            val fiveMinutesFromNow = System.currentTimeMillis() + (5 * 60 * 1000)
            expiresAt <= fiveMinutesFromNow
        } catch (e: JWTDecodeException) {
            true
        }
    }

    /**
     * Clear all stored tokens (logout)
     */
    fun clearTokens() {
        sharedPreferences.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(TOKEN_TIMESTAMP_KEY)
            .apply()

        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Get token age in milliseconds
     */
    fun getTokenAge(): Long {
        val timestamp = sharedPreferences.getLong(TOKEN_TIMESTAMP_KEY, 0)
        return if (timestamp > 0) {
            System.currentTimeMillis() - timestamp
        } else {
            0
        }
    }

    companion object {
        private const val PREFS_NAME = "auth_tokens"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val TOKEN_TIMESTAMP_KEY = "token_timestamp"
    }
}

/**
 * Authentication state
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticated : AuthState()
    object TokenExpired : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * User information extracted from JWT token
 */
data class UserInfo(
    val userId: String,
    val role: String,
    val email: String? = null,
    val phone: String? = null
)
