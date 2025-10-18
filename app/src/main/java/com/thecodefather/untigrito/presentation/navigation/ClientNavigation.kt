package com.thecodefather.untigrito.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Client Module Navigation Routes
 */
object ClientRoutes {
    const val HOME = "client_home"
    const val SERVICES = "client_services"
    const val REQUESTS = "client_requests"
    const val PROFILE = "client_profile"
    const val SERVICE_DETAIL = "service_detail/{serviceId}"
    const val CREATE_REQUEST = "create_request"
    const val PAYMENT = "payment"
}

/**
 * Client Navigation Graph
 * Defines all navigation routes and screen transitions for client module
 */
@Composable
fun ClientNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ClientRoutes.HOME
    ) {
        // Home Screen
        composable(ClientRoutes.HOME) {
            // ClientHomeScreen(navController = navController)
            // TODO: Implement ClientHomeScreen
        }

        // Services/Professionals Screen
        composable(ClientRoutes.SERVICES) {
            // ServicesScreen(navController = navController)
            // TODO: Implement ServicesScreen
        }

        // Requests Management Screen
        composable(ClientRoutes.REQUESTS) {
            // RequestsScreen(navController = navController)
            // TODO: Implement RequestsScreen
        }

        // Profile Screen
        composable(ClientRoutes.PROFILE) {
            // ClientProfileScreen(navController = navController)
            // TODO: Implement ClientProfileScreen
        }

        // Service Detail Screen
        composable(ClientRoutes.SERVICE_DETAIL) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            // ServiceDetailScreen(serviceId = serviceId, navController = navController)
            // TODO: Implement ServiceDetailScreen
        }

        // Create Request Screen
        composable(ClientRoutes.CREATE_REQUEST) {
            // CreateRequestScreen(navController = navController)
            // TODO: Implement CreateRequestScreen
        }

        // Payment Screen
        composable(ClientRoutes.PAYMENT) {
            // PaymentScreen(navController = navController)
            // TODO: Implement PaymentScreen
        }
    }
}
