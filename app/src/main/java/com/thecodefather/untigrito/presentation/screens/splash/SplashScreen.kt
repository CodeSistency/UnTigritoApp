package com.thecodefather.untigrito.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.R

/**
 * Splash Screen Composable
 *
 * Displays while the app initializes. Shows a loading indicator and app title.
 *
 * @param viewModel The ViewModel managing splash screen state
 * @param onNavigateToHome Callback to navigate to home screen
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    // Handle navigation based on state
    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is SplashUiState.NavigateToClientHome -> onNavigateToHome()
            is SplashUiState.NavigateToProfessionalHome -> onNavigateToHome() // TODO: Implement professional home navigation
            is SplashUiState.NavigateToLogin -> onNavigateToLogin()
            SplashUiState.Loading -> {} // Stay on splash screen
            is SplashUiState.Error -> {
                // TODO: Show error dialog or retry mechanism
                // For now, navigate to login as fallback
                onNavigateToLogin()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.untiger),
            contentDescription = "App Logo",
            Modifier.size(200.dp)

        )
    }
}
