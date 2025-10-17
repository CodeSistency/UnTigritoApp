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
            // Simulate initialization tasks
            delay(2000)
            
            // TODO: Perform actual initialization:
            // - Check if user is authenticated
            // - Load app configuration
            // - Initialize other services
            
            _uiState.value = SplashUiState.NavigateToHome
        }
    }
}
