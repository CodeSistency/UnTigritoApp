package com.thecodefather.untigrito.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.domain.model.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State for the Splash Screen
 */
sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NavigateToClientHome : SplashUiState()
    data object NavigateToProfessionalHome : SplashUiState()
    data object NavigateToLogin : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}

/**
 * ViewModel for the Splash Screen
 *
 * Manages splash screen initialization and navigation.
 * Typically used for app initialization tasks before showing the main UI.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Simulate initialization tasks
                delay(2000)
                
                // Check if user is authenticated
                val currentUser = authStateManager.getCurrentUser()
                val isAuthenticated = authStateManager.isAuthenticated()
                
                if (isAuthenticated && currentUser != null) {
                    // User is authenticated, navigate based on user type
                    when (currentUser.userType) {
                        UserType.CLIENT -> {
                            _uiState.value = SplashUiState.NavigateToClientHome
                        }
                        UserType.PROFESSIONAL -> {
                            _uiState.value = SplashUiState.NavigateToProfessionalHome
                        }
                    }
                } else {
                    // User is not authenticated, navigate to login
                    _uiState.value = SplashUiState.NavigateToLogin
                }
            } catch (e: Exception) {
                _uiState.value = SplashUiState.Error("Error initializing app: ${e.message}")
            }
        }
    }
}
