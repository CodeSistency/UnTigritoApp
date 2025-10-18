package com.thecodefather.untigrito.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data object NavigateToHome : SplashUiState()
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
class SplashViewModel @Inject constructor() : ViewModel() {

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
                
                // TODO: Perform actual initialization:
                // - Check if user is authenticated (AuthRepository.getCurrentUser())
                // - Load app configuration from SharedPreferences
                // - Initialize other services (Analytics, Crashlytics, etc.)
                // - Check network connectivity
                // - Load user preferences
                
                // For now, always navigate to login
                // TODO: Implement proper authentication check:
                // val currentUser = authRepository.getCurrentUser()
                // _uiState.value = if (currentUser != null) {
                //     SplashUiState.NavigateToHome
                // } else {
                //     SplashUiState.NavigateToLogin
                // }
                _uiState.value = SplashUiState.NavigateToLogin
            } catch (e: Exception) {
                _uiState.value = SplashUiState.Error("Error initializing app: ${e.message}")
            }
        }
    }
}
