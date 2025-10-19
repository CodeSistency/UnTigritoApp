package com.thecodefather.untigrito.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thecodefather.untigrito.presentation.screens.splash.SplashScreen
import com.thecodefather.untigrito.presentation.screens.splash.SplashViewModel
import com.thecodefather.untigrito.presentation.screens.auth.login.LoginScreen
import com.thecodefather.untigrito.presentation.screens.auth.register.RegisterScreen
import com.thecodefather.untigrito.presentation.screens.auth.forgotpassword.ForgotPasswordScreen
import com.thecodefather.untigrito.presentation.screens.auth.forgotpassword.ForgotPasswordViewModel
import com.thecodefather.untigrito.presentation.screens.auth.login.AuthViewModel
import com.thecodefather.untigrito.presentation.screens.client.ClientProfileScreen
import com.thecodefather.untigrito.presentation.screens.client.RequestsScreen
import com.thecodefather.untigrito.presentation.screens.client.ServicesScreen
import com.thecodefather.untigrito.presentation.screens.client.ClientMainScreen
import com.thecodefather.untigrito.presentation.screens.professional.messages.ChatScreen
import com.thecodefather.untigrito.presentation.screens.professionals.profile.ProfessionalProfileScreen
import com.thecodefather.untigrito.presentation.screens.professional.ProfessionalMainScreen

/**
 * Route definitions for navigation
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val CLIENT_MAIN = "client_main" // Nueva ruta
    const val CLIENT_HOME = "client_home"
    const val CLIENT_SERVICES = "client_services"
    const val CLIENT_REQUESTS = "client_requests"
    const val CLIENT_PROFILE = "client_profile"
    const val CHAT = "chat/{conversationId}"
    const val PROFESSIONAL_PROFILE = "professional_profile"

    fun createChatRoute(conversationId: String) = "chat/$conversationId"
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
                    navController.navigate(Routes.CLIENT_MAIN) { // Cambiado
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Auth Screens
        composable(Routes.LOGIN) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.CLIENT_MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CLIENT_REQUESTS){
            RequestsScreen(
                navController = navController
            )
        }

        composable(Routes.REGISTER) {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.CLIENT_MAIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Routes.HOME) {
//            ClientMainScreen(mainNavController = navController) // Cambiado
            ProfessionalMainScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CLIENT_MAIN) { // Nueva pantalla principal del cliente
            ClientMainScreen(mainNavController = navController)
        }

        composable(Routes.CLIENT_SERVICES) {
            ServicesScreen(navController = navController)
        }

        composable(Routes.CLIENT_PROFILE) {
            ClientProfileScreen(navController = navController)
        }

        composable(Routes.CHAT) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = conversationId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFESSIONAL_PROFILE) {
            ProfessionalProfileScreen(
                professionalId = "default-id",
                onBackClick = { navController.popBackStack() }
            )
        }

        // Ruta para el módulo profesional
        composable(ProfessionalNavigation.PROFESSIONAL_MAIN) {
            ProfessionalMainScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClient = { 
                    navController.navigate(Routes.CLIENT_MAIN) {
                        popUpTo(ProfessionalNavigation.PROFESSIONAL_MAIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { 
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(ProfessionalNavigation.PROFESSIONAL_MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
