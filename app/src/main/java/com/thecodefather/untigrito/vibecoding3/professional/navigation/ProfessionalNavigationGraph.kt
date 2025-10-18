package com.thecodefather.untigrito.vibecoding3.professional.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.jobs.JobsListScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.jobs.JobDetailScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.proposals.CreateProposalScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.proposals.ProposalsListScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.proposals.ProposalDetailScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.messages.MessagesInboxScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.messages.ChatScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.messages.SupportChatScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.services.MyServicesListScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.services.ServiceDetailScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.services.CreateEditServiceScreen
import com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.profile.ProfessionalProfileScreen

/**
 * Configura el grafo de navegación para el módulo profesional
 */
fun NavGraphBuilder.professionalNavigationGraph(
    navController: NavController,
    onNavigateToAuth: () -> Unit
) {
    navigation(
        route = "professional",
        startDestination = ProfessionalRoutes.JOBS_LIST
    ) {
        // === SUBFLUJO TRABAJOS ===
        composable(ProfessionalRoutes.JOBS_LIST) {
            JobsListScreen(
                onJobClick = { jobId ->
                    navController.navigate("${ProfessionalRoutes.JOB_DETAIL_ROUTE}/$jobId")
                },
                onNavigateToProposals = {
                    navController.navigate(ProfessionalRoutes.PROPOSALS_LIST) {
                        popUpTo(ProfessionalRoutes.JOBS_LIST) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = ProfessionalRoutes.JOB_DETAIL,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.JOB_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(ProfessionalNavArgs.JOB_ID) ?: ""
            JobDetailScreen(
                jobId = jobId,
                onBackClick = { navController.popBackStack() },
                onCreateProposalClick = { 
                    navController.navigate("${ProfessionalRoutes.CREATE_PROPOSAL_ROUTE}/$jobId")
                }
            )
        }
        
        composable(
            route = ProfessionalRoutes.CREATE_PROPOSAL,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.JOB_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString(ProfessionalNavArgs.JOB_ID) ?: ""
            CreateProposalScreen(
                jobId = jobId,
                onBackClick = { navController.popBackStack() },
                onProposalCreated = {
                    navController.navigate(ProfessionalRoutes.PROPOSALS_LIST) {
                        popUpTo(ProfessionalRoutes.JOBS_LIST) { inclusive = false }
                    }
                }
            )
        }
        
        // === SUBFLUJO PROPUESTAS ===
        composable(ProfessionalRoutes.PROPOSALS_LIST) {
            ProposalsListScreen(
                onProposalClick = { proposalId ->
                    navController.navigate("${ProfessionalRoutes.PROPOSAL_DETAIL_ROUTE}/$proposalId")
                },
                onNavigateToJobs = {
                    navController.navigate(ProfessionalRoutes.JOBS_LIST) {
                        popUpTo(ProfessionalRoutes.PROPOSALS_LIST) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(
            route = ProfessionalRoutes.PROPOSAL_DETAIL,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.PROPOSAL_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val proposalId = backStackEntry.arguments?.getString(ProfessionalNavArgs.PROPOSAL_ID) ?: ""
            ProposalDetailScreen(
                proposalId = proposalId,
                onBackClick = { navController.popBackStack() },
                onChatClick = { conversationId ->
                    navController.navigate("${ProfessionalRoutes.CHAT_SCREEN_ROUTE}/$conversationId")
                }
            )
        }
        
        // === SUBFLUJO MENSAJES ===
        composable(ProfessionalRoutes.MESSAGES_INBOX) {
            MessagesInboxScreen(
                onConversationClick = { conversationId ->
                    navController.navigate("${ProfessionalRoutes.CHAT_SCREEN_ROUTE}/$conversationId")
                },
                onSupportClick = {
                    navController.navigate(ProfessionalRoutes.SUPPORT_CHAT)
                }
            )
        }
        
        composable(
            route = ProfessionalRoutes.CHAT_SCREEN,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.CONVERSATION_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString(ProfessionalNavArgs.CONVERSATION_ID) ?: ""
            ChatScreen(
                conversationId = conversationId,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(ProfessionalRoutes.SUPPORT_CHAT) {
            SupportChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // === SUBFLUJO MIS SERVICIOS ===
        composable(ProfessionalRoutes.MY_SERVICES_LIST) {
            MyServicesListScreen(
                onServiceClick = { serviceId ->
                    navController.navigate("${ProfessionalRoutes.SERVICE_DETAIL_ROUTE}/$serviceId")
                },
                onCreateServiceClick = {
                    navController.navigate(ProfessionalRoutes.CREATE_SERVICE)
                }
            )
        }
        
        composable(
            route = ProfessionalRoutes.SERVICE_DETAIL,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.SERVICE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(ProfessionalNavArgs.SERVICE_ID) ?: ""
            ServiceDetailScreen(
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate("${ProfessionalRoutes.EDIT_SERVICE_ROUTE}/$serviceId")
                }
            )
        }
        
        composable(ProfessionalRoutes.CREATE_SERVICE) {
            CreateEditServiceScreen(
                serviceId = null,
                onBackClick = { navController.popBackStack() },
                onServiceSaved = { navController.popBackStack() }
            )
        }
        
        composable(
            route = ProfessionalRoutes.EDIT_SERVICE,
            arguments = listOf(
                navArgument(ProfessionalNavArgs.SERVICE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(ProfessionalNavArgs.SERVICE_ID) ?: ""
            CreateEditServiceScreen(
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() },
                onServiceSaved = { navController.popBackStack() }
            )
        }
        
        // === PERFIL ===
        composable(ProfessionalRoutes.PROFESSIONAL_PROFILE) {
            ProfessionalProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSettings = {
                    navController.navigate(ProfessionalRoutes.SETTINGS)
                },
                onLogout = onNavigateToAuth
            )
        }
    }
}

/**
 * Extensión para navegar a trabajos
 */
fun NavController.navigateToJobs() {
    navigate(ProfessionalRoutes.JOBS_LIST) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Extensión para navegar a propuestas
 */
fun NavController.navigateToProposals() {
    navigate(ProfessionalRoutes.PROPOSALS_LIST) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Extensión para navegar a mensajes
 */
fun NavController.navigateToMessages() {
    navigate(ProfessionalRoutes.MESSAGES_INBOX) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Extensión para navegar a mis servicios
 */
fun NavController.navigateToServices() {
    navigate(ProfessionalRoutes.MY_SERVICES_LIST) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Extensión para navegar a perfil
 */
fun NavController.navigateToProfile() {
    navigate(ProfessionalRoutes.PROFESSIONAL_PROFILE) {
        launchSingleTop = true
    }
}
