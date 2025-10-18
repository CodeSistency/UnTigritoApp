package com.example.vibecoding3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.vibecoding3.auth.domain.model.AuthState
import com.example.vibecoding3.auth.ui.login.AuthViewModel
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel
import com.example.vibecoding3.home.ui.HomeViewModel
import com.example.vibecoding3.home.ui.MainScreenContainer
import androidx.navigation.navigation
import androidx.navigation.compose.composable

/**
 * NavHost principal de la aplicación
 * Maneja la navegación entre flujo de autenticación y flujo principal
 */
@Composable
fun VibecodingNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    verificationViewModel: VerificationViewModel,
    homeViewModel: HomeViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> MainRoutes.NAVIGATION_GRAPH
        else -> AuthRoutes.NAVIGATION_GRAPH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navController,
            authViewModel = authViewModel,
            verificationViewModel = verificationViewModel,
            onAuthSuccess = {
                navController.navigate(MainRoutes.NAVIGATION_GRAPH) {
                    popUpTo(AuthRoutes.NAVIGATION_GRAPH) {
                        inclusive = true
                    }
                }
            }
        )
        mainNavGraph(navController = navController, homeViewModel = homeViewModel)
    }
}
