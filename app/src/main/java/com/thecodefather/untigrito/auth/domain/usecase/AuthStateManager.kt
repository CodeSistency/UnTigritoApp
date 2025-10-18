package com.thecodefather.untigrito.auth.domain.usecase

import com.thecodefather.untigrito.auth.domain.model.AuthState
import com.thecodefather.untigrito.data.database.entity.AuthStateEntity
import com.thecodefather.untigrito.data.database.dao.AuthStateDao
import com.thecodefather.untigrito.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager for authentication state persistence and session management
 * Handles authentication state across app restarts and provides reactive state updates
 */
@Singleton
class AuthStateManager @Inject constructor(
    private val authStateDao: AuthStateDao
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Load persisted auth state on initialization
        loadPersistedAuthState()
    }

    /**
     * Updates the authentication state and persists it
     */
    suspend fun updateAuthState(state: AuthState, user: User? = null) {
        Timber.d("🔐 AUTH STATE MANAGER - Updating auth state: ${state::class.simpleName}")

        _authState.value = state
        _currentUser.value = user

        // Persist the state
        val authStateEntity = AuthStateEntity(
            id = 1, // Single row for auth state
            authState = state::class.simpleName ?: "Unauthenticated",
            userId = user?.id,
            userName = user?.name,
            userEmail = user?.email,
            userPhone = user?.phoneNumber,
            userType = user?.userType?.name,
            isPhoneVerified = user?.isPhoneVerified ?: false,
            isCedulaVerified = user?.isCedulaVerified ?: false,
            timestamp = System.currentTimeMillis()
        )

        authStateDao.saveAuthState(authStateEntity)
        Timber.d("✅ AUTH STATE MANAGER - Auth state persisted")
    }

    /**
     * Clears authentication state (logout)
     */
    suspend fun clearAuthState() {
        Timber.d("🔐 AUTH STATE MANAGER - Clearing auth state (logout)")

        _authState.value = AuthState.Unauthenticated
        _currentUser.value = null

        authStateDao.clearAuthState()
        Timber.d("✅ AUTH STATE MANAGER - Auth state cleared")
    }

    /**
     * Gets the current authentication state
     */
    fun getCurrentAuthState(): AuthState = _authState.value

    /**
     * Gets the current user
     */
    fun getCurrentUser(): User? = _currentUser.value

    /**
     * Checks if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return _authState.value is AuthState.Authenticated
    }

    /**
     * Checks if user is in loading state
     */
    fun isLoading(): Boolean {
        return _authState.value is AuthState.Loading
    }

    /**
     * Gets the current user ID if authenticated
     */
    fun getCurrentUserId(): String? {
        return (_authState.value as? AuthState.Authenticated)?.user?.id
    }

    /**
     * Gets the current user role if authenticated
     */
    fun getCurrentUserRole(): String? {
        return (_authState.value as? AuthState.Authenticated)?.user?.userType?.name
    }

    /**
     * Observable flow of authentication status (authenticated/not authenticated)
     */
    fun observeAuthStatus(): Flow<Boolean> {
        return _authState.combine(_currentUser) { state, user ->
            state is AuthState.Authenticated && user != null
        }
    }

    /**
     * Loads persisted authentication state from database
     */
    private fun loadPersistedAuthState() {
        try {
            // Use runBlocking to call suspend function from non-suspend context
            val savedState = kotlinx.coroutines.runBlocking { 
                authStateDao.getAuthState().first() 
            }

            if (savedState != null) {
                val state = when (savedState.authState) {
                    "Authenticated" -> {
                        val user = reconstructUserFromEntity(savedState)
                        if (user != null) {
                            _currentUser.value = user
                            AuthState.Authenticated(user)
                        } else {
                            AuthState.Unauthenticated
                        }
                    }
                    else -> AuthState.Unauthenticated
                }

                _authState.value = state
                Timber.d("✅ AUTH STATE MANAGER - Loaded persisted auth state: ${state::class.simpleName}")
            } else {
                Timber.d("ℹ️ AUTH STATE MANAGER - No persisted auth state found")
            }
        } catch (e: Exception) {
            Timber.e("❌ AUTH STATE MANAGER - Failed to load persisted auth state: ${e.message}")
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Reconstructs User object from database entity
     */
    private fun reconstructUserFromEntity(entity: AuthStateEntity): User? {
        return try {
            if (entity.userId != null && entity.userName != null) {
                User(
                    id = entity.userId!!,
                    name = entity.userName!!,
                    email = entity.userEmail ?: "",
                    phoneNumber = entity.userPhone ?: "",
                    userType = when (entity.userType) {
                        "PROFESSIONAL" -> com.thecodefather.untigrito.domain.model.UserType.PROFESSIONAL
                        else -> com.thecodefather.untigrito.domain.model.UserType.CLIENT
                    },
                    isPhoneVerified = entity.isPhoneVerified,
                    isCedulaVerified = entity.isCedulaVerified
                )
            } else null
        } catch (e: Exception) {
            Timber.w("Failed to reconstruct user from entity: ${e.message}")
            null
        }
    }

    /**
     * Forces a refresh of the authentication state
     * Useful when tokens might have changed externally
     */
    suspend fun refreshAuthState() {
        Timber.d("🔐 AUTH STATE MANAGER - Refreshing auth state")
        loadPersistedAuthState()
    }
}
