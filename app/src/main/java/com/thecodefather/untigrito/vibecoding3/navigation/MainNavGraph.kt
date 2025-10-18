package com.example.vibecoding3.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.vibecoding3.home.ui.HomeViewModel
import com.example.vibecoding3.home.ui.MainScreenContainer
import com.example.vibecoding3.payment.ui.recharge.RechargeMethodsScreen
import com.example.vibecoding3.payment.ui.PaymentScreen

/**
 * Construye el grafo de navegación para la aplicación principal
 */
fun NavGraphBuilder.mainNavGraph(navController: NavHostController, homeViewModel: HomeViewModel) {
    navigation(
        route = MainRoutes.NAVIGATION_GRAPH,
        startDestination = MainRoutes.HOME
    ) {
        composable(route = MainRoutes.HOME) {
            MainScreenContainer(navController = navController)
        }
        composable(route = MainRoutes.RECHARGE_METHODS) {
            RechargeMethodsScreen(onBack = { navController.navigateUp() })
        }
        composable(route = MainRoutes.PAYMENT_SCREEN) {
            PaymentScreen(onBack = { navController.navigateUp() })
        }
    }
}
