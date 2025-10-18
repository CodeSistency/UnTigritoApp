package com.example.vibecoding3.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.vibecoding3.auth.ui.forgotpassword.ForgotPasswordScreen
import com.example.vibecoding3.auth.ui.login.AuthViewModel
import com.example.vibecoding3.auth.ui.login.LoginScreen
import com.example.vibecoding3.auth.ui.register.RegisterScreen
import com.example.vibecoding3.auth.ui.splash.SplashScreen
import com.example.vibecoding3.auth.ui.verification.cedula.CedulaVerificationScreen
import com.example.vibecoding3.auth.ui.verification.phone.PhoneRegistrationScreen
import com.example.vibecoding3.auth.ui.verification.validatephone.PhoneValidationScreen
import com.example.vibecoding3.auth.ui.verification.introduction.VerificationIntroductionScreen
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel

/**
 * Construye el grafo de navegación para autenticación
 */
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    verificationViewModel: VerificationViewModel,
    onAuthSuccess: () -> Unit
) {
    navigation(
        route = "auth",
        startDestination = AuthRoutes.SPLASH
    ) {
        // Splash Screen
        composable(route = AuthRoutes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(AuthRoutes.LOGIN) }
            )
        }
        
        // Login Screen
        composable(route = AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(AuthRoutes.REGISTER) }
            )
        }
        
        // Register Screen
        composable(route = AuthRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(AuthRoutes.LOGIN) }
            )
        }
        
        // Forgot Password Screen
        composable(route = AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigateUp() }
            )
        }
        
        // Verification Flow
        verificationNavGraph(navController, verificationViewModel, onAuthSuccess)
    }
}

/**
 * Construye el grafo de navegación para verificación
 */
fun NavGraphBuilder.verificationNavGraph(
    navController: NavHostController,
    verificationViewModel: VerificationViewModel,
    onAuthSuccess: () -> Unit
) {
    navigation(
        route = "verification",
        startDestination = AuthRoutes.VERIFICATION_INTRO
    ) {
        // Verification Introduction
        composable(route = AuthRoutes.VERIFICATION_INTRO) {
            VerificationIntroductionScreen(
                onContinue = { navController.navigate(AuthRoutes.CEDULA_VERIFICATION) },
                onSkip = onAuthSuccess
            )
        }
        
        // Cedula Verification
        composable(route = AuthRoutes.CEDULA_VERIFICATION) {
            CedulaVerificationScreen(
                viewModel = verificationViewModel,
                onNavigateToPhone = { navController.navigate(AuthRoutes.PHONE_REGISTRATION) }
            )
        }
        
        // Phone Registration
        composable(route = AuthRoutes.PHONE_REGISTRATION) {
            PhoneRegistrationScreen(
                viewModel = verificationViewModel,
                onNavigateToValidation = { navController.navigate(AuthRoutes.PHONE_VALIDATION) }
            )
        }
        
        // Phone Validation
        composable(route = AuthRoutes.PHONE_VALIDATION) {
            PhoneValidationScreen(
                viewModel = verificationViewModel,
                onNavigateToHome = onAuthSuccess
            )
        }
    }
}
