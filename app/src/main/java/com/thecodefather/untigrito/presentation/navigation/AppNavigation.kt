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
import com.thecodefather.untigrito.presentation.screens.auth.login.LoginScreen
import com.thecodefather.untigrito.presentation.screens.auth.register.RegisterScreen
import com.thecodefather.untigrito.presentation.screens.auth.forgotpassword.ForgotPasswordScreen
import com.thecodefather.untigrito.presentation.screens.auth.forgotpassword.ForgotPasswordViewModel
import com.thecodefather.untigrito.presentation.screens.auth.login.AuthViewModel
import com.thecodefather.untigrito.presentation.screens.auth.verification.introduction.VerificationIntroductionScreen
import com.thecodefather.untigrito.presentation.screens.auth.verification.phone.PhoneVerificationScreen
import com.thecodefather.untigrito.presentation.screens.auth.verification.cedula.CedulaVerificationScreen
import com.thecodefather.untigrito.presentation.screens.client.ClientHomeScreen

/**
 * Route definitions for navigation
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFICATION_INTRO = "verification_intro"
    const val PHONE_VERIFICATION = "phone_verification"
    const val CEDULA_VERIFICATION = "cedula_verification"
    const val HOME = "home"
    const val CLIENT_FLOW = "client_flow"
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
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.CLIENT_FLOW) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.VERIFICATION_INTRO) {
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

        // Verification Screens
        composable(Routes.VERIFICATION_INTRO) {
            VerificationIntroductionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartPhoneVerification = {
                    navController.navigate(Routes.PHONE_VERIFICATION)
                },
                onStartIdVerification = {
                    navController.navigate(Routes.CEDULA_VERIFICATION)
                },
                onSkipForNow = {
                    navController.navigate(Routes.CLIENT_FLOW) {
                        popUpTo(Routes.VERIFICATION_INTRO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PHONE_VERIFICATION) {
            PhoneVerificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(Routes.CEDULA_VERIFICATION) {
                        popUpTo(Routes.PHONE_VERIFICATION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CEDULA_VERIFICATION) {
            CedulaVerificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(Routes.CLIENT_FLOW) {
                        popUpTo(Routes.CEDULA_VERIFICATION) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel)
        }

        composable(Routes.CLIENT_FLOW) {
            ClientHomeScreen()
        }
    }
}
