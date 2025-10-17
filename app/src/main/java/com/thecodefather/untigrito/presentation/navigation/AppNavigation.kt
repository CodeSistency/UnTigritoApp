package com.thecodefather.untigrito.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thecodefather.untigrito.presentation.screens.home.HomeScreen
import com.thecodefather.untigrito.presentation.screens.home.HomeViewModel
import com.thecodefather.untigrito.presentation.screens.splash.SplashScreen
import com.thecodefather.untigrito.presentation.screens.splash.SplashViewModel

/**
 * Route definitions for navigation
 */
object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
}

/**
 * Main Application Navigation Graph
 *
 * Defines all navigation routes and screen transitions.
 * Uses Jetpack Compose Navigation for managing navigation state.
 *
 * @param navController The NavController managing navigation state
 */
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel)
        }
    }
}
