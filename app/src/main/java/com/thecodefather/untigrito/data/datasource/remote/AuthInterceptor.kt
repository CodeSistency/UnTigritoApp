package com.thecodefather.untigrito.data.datasource.remote

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Authentication interceptor for OkHttp
 * Handles token management and automatic refresh
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        // Get current token
        val token = getAccessToken()
        
        // Add token to request if exists
        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        
        // Execute request
        var response = chain.proceed(request)
        
        // Handle 401 (token expired)
        if (response.code == 401) {
            synchronized(this) {
                // Try to refresh token
                val refreshed = refreshToken()
                
                if (refreshed) {
                    // Retry original request with new token
                    val newToken = getAccessToken()
                    if (newToken != null) {
                        response.close()
                        val retryRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                        response = chain.proceed(retryRequest)
                    }
                } else {
                    // Token refresh failed, clear and logout
                    clearTokens()
                }
            }
        }
        
        return response
    }
    
    private fun refreshToken(): Boolean {
        return try {
            val refreshToken = getRefreshToken() ?: return false
            
            // In a real app, call API to refresh
            // For now, return false to let app handle logout
            false
        } catch (e: Exception) {
            false
        }
    }
    
    fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }
    
    private fun getAccessToken(): String? {
        return encryptedPrefs.getString(ACCESS_TOKEN_KEY, null)
    }
    
    private fun getRefreshToken(): String? {
        return encryptedPrefs.getString(REFRESH_TOKEN_KEY, null)
    }
    
    fun clearTokens() {
        encryptedPrefs.edit().apply {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }
    
    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
