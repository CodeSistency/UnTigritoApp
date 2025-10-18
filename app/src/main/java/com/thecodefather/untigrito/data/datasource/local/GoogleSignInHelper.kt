package com.thecodefather.untigrito.data.datasource.local

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.thecodefather.untigrito.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for Google Sign-In integration
 * Handles Google OAuth flow and token retrieval
 */
@Singleton
class GoogleSignInHelper @Inject constructor(
    private val context: Context
) {
    private var googleSignInClient: GoogleSignInClient

    init {
        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // TODO: Add to strings.xml
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Get the sign-in intent for starting Google OAuth flow
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Handle the result from Google Sign-In
     */
    fun handleSignInResult(data: Intent?): Result<GoogleSignInData> {
        return try {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val signInData = GoogleSignInData(
                idToken = account.idToken,
                accessToken = account.id, // This is actually the Google account ID, not access token
                email = account.email,
                displayName = account.displayName,
                photoUrl = account.photoUrl?.toString()
            )

            Timber.d("✅ GOOGLE SIGN-IN SUCCESS - Email: ${signInData.email}")
            Result.success(signInData)

        } catch (e: ApiException) {
            Timber.e("❌ GOOGLE SIGN-IN ERROR - Code: ${e.statusCode}, Message: ${e.message}")
            val errorMessage = when (e.statusCode) {
                12501 -> "Google Sign-In was cancelled"
                12502 -> "Google Sign-In failed"
                else -> "Google Sign-In error: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Timber.e("❌ GOOGLE SIGN-IN UNEXPECTED ERROR - ${e.message}")
            Result.failure(Exception("Unexpected error during Google Sign-In"))
        }
    }

    /**
     * Sign out from Google account
     */
    fun signOut(): Task<Void> {
        Timber.d("🔐 GOOGLE SIGN-OUT START")
        return googleSignInClient.signOut()
    }

    /**
     * Revoke Google access (removes app permissions)
     */
    fun revokeAccess(): Task<Void> {
        Timber.d("🔐 GOOGLE REVOKE ACCESS START")
        return googleSignInClient.revokeAccess()
    }

    /**
     * Get currently signed-in Google account
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Check if user is currently signed in with Google
     */
    fun isSignedIn(): Boolean {
        return getLastSignedInAccount() != null
    }
}

/**
 * Data class containing Google Sign-In result information
 */
data class GoogleSignInData(
    val idToken: String?,
    val accessToken: String?,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
