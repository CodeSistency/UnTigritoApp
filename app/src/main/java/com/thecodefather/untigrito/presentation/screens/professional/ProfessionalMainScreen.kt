package com.thecodefather.untigrito.presentation.screens.professional

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thecodefather.untigrito.presentation.screens.professional.jobs.*
import com.thecodefather.untigrito.presentation.screens.professional.proposals.*
import com.thecodefather.untigrito.presentation.screens.professional.messages.*
import com.thecodefather.untigrito.presentation.screens.professional.services.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalMainScreen(
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (isMainScreen(currentRoute)) {
                ProfessionalBottomNavigation(
                    currentRoute = currentRoute ?: ProfessionalRoutes.JOBS,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ProfessionalRoutes.JOBS,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Subflujo de Trabajos
            composable(ProfessionalRoutes.JOBS) {
                JobsScreen(
                    onJobClick = { jobId ->
                        navController.navigate("${ProfessionalRoutes.JOB_DETAIL}/$jobId")
                    },
                    onNavigateToProposal = { jobId ->
                        navController.navigate("${ProfessionalRoutes.CREATE_PROPOSAL}/$jobId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.JOB_DETAIL}/{jobId}") { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                JobDetailScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProposal = { jobId ->
                        navController.navigate("${ProfessionalRoutes.CREATE_PROPOSAL}/$jobId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.CREATE_PROPOSAL}/{jobId}") { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                CreateProposalScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onProposalCreated = { 
                        navController.navigate(ProfessionalRoutes.PROPOSALS) {
                            popUpTo(ProfessionalRoutes.JOBS) { inclusive = false }
                        }
                    }
                )
            }

            // Subflujo de Propuestas
            composable(ProfessionalRoutes.PROPOSALS) {
                ProposalsScreen(
                    onProposalClick = { proposalId ->
                        navController.navigate("${ProfessionalRoutes.PROPOSAL_DETAIL}/$proposalId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.PROPOSAL_DETAIL}/{proposalId}") { backStackEntry ->
                val proposalId = backStackEntry.arguments?.getString("proposalId") ?: ""
                ProposalDetailScreen(
                    proposalId = proposalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Subflujo de Mensajes
            composable(ProfessionalRoutes.MESSAGES) {
                MessagesScreen(
                    onConversationClick = { conversationId ->
                        navController.navigate("${ProfessionalRoutes.CHAT}/$conversationId")
                    },
                    onSupportClick = {
                        navController.navigate("${ProfessionalRoutes.CHAT}/support")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.CHAT}/{conversationId}") { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                ChatScreen(
                    conversationId = conversationId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Subflujo de Mis Servicios
            composable(ProfessionalRoutes.SERVICES) {
                ServicesScreen(
                    onServiceClick = { serviceId ->
                        navController.navigate("${ProfessionalRoutes.SERVICE_DETAIL}/$serviceId")
                    },
                    onCreateService = {
                        navController.navigate(ProfessionalRoutes.CREATE_SERVICE)
                    },
                    onEditService = { serviceId ->
                        navController.navigate("${ProfessionalRoutes.EDIT_SERVICE}/$serviceId")
                    }
                )
            }
            
            composable(ProfessionalRoutes.CREATE_SERVICE) {
                CreateEditServiceScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onServiceSaved = { navController.popBackStack() }
                )
            }
            
            composable("${ProfessionalRoutes.EDIT_SERVICE}/{serviceId}") { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                CreateEditServiceScreen(
                    serviceId = serviceId,
                    onNavigateBack = { navController.popBackStack() },
                    onServiceSaved = { navController.popBackStack() }
                )
            }
            
            composable("${ProfessionalRoutes.SERVICE_DETAIL}/{serviceId}") { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                // Aquí implementarías la pantalla de detalle del servicio
                // Por ahora mostramos un placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Detalle del Servicio: $serviceId")
                }
            }
        }
    }
}

@Composable
fun ProfessionalBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        val items = listOf(
            ProfessionalNavigationItem(
                route = ProfessionalRoutes.JOBS,
                label = "Trabajos",
                icon = Icons.Default.Work
            ),
            ProfessionalNavigationItem(
                route = ProfessionalRoutes.PROPOSALS,
                label = "Propuestas",
                icon = Icons.Default.Assignment
            ),
            ProfessionalNavigationItem(
                route = ProfessionalRoutes.MESSAGES,
                label = "Mensajes",
                icon = Icons.Default.Chat
            ),
            ProfessionalNavigationItem(
                route = ProfessionalRoutes.SERVICES,
                label = "Mis Servicios",
                icon = Icons.Default.Business
            )
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

data class ProfessionalNavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object ProfessionalRoutes {
    const val JOBS = "professional/jobs"
    const val JOB_DETAIL = "professional/jobs/detail"
    const val CREATE_PROPOSAL = "professional/proposals/create"
    const val PROPOSALS = "professional/proposals"
    const val PROPOSAL_DETAIL = "professional/proposals/detail"
    const val MESSAGES = "professional/messages"
    const val CHAT = "professional/chat"
    const val SERVICES = "professional/services"
    const val SERVICE_DETAIL = "professional/services/detail"
    const val CREATE_SERVICE = "professional/services/create"
    const val EDIT_SERVICE = "professional/services/edit"
}

private fun isMainScreen(route: String?): Boolean {
    return route in listOf(
        ProfessionalRoutes.JOBS,
        ProfessionalRoutes.PROPOSALS,
        ProfessionalRoutes.MESSAGES,
        ProfessionalRoutes.SERVICES
    )
}
