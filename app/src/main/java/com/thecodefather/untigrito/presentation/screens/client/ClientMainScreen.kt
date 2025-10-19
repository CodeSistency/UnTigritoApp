package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.navigation.ClientRoutes
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.screens.account.AccountDetailsScreen
import com.thecodefather.untigrito.presentation.screens.client.ClientMessagesScreen
import com.thecodefather.untigrito.presentation.screens.professional.messages.ChatScreen

@Composable
fun ClientMainScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Routes.CLIENT_HOME, Routes.CLIENT_SERVICES, Routes.CLIENT_REQUESTS, Routes.CLIENT_PROFILE)) {
                ClientBottomNavBar(
                    currentRoute = currentRoute ?: Routes.CLIENT_HOME,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.CLIENT_HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.CLIENT_HOME) {
                HomeScreenClient(
                    navController = navController,
                    onNavigateToAccountDetails = { navController.navigate(ClientRoutes.ACCOUNT_DETAILS) }
                )
            }
            composable(Routes.CLIENT_SERVICES) {
                ServicesScreen(navController = mainNavController)
            }
            composable(Routes.CLIENT_REQUESTS) {
                RequestsScreen(navController = mainNavController)
            }
            composable(Routes.CLIENT_PROFILE) {
                ClientProfileScreen(navController = mainNavController)
            }
            composable(ClientRoutes.ACCOUNT_DETAILS) {
                AccountDetailsScreen(navController = navController)
            }
            composable(ClientRoutes.MESSAGES) {
                ClientMessagesScreen(
                    onConversationClick = { conversationId ->
                        mainNavController.navigate(Routes.createChatRoute(conversationId))
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onSupportClick = { 
                        mainNavController.navigate(Routes.SUPPORT_CHAT)
                    },
                    navController = mainNavController
                )
            }
            composable(Routes.CHAT) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                ChatScreen(
                    conversationId = conversationId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
