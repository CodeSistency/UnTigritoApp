package com.thecodefather.untigrito.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.thecodefather.untigrito.presentation.screens.professional.ProfessionalMainScreen
import com.thecodefather.untigrito.presentation.screens.professional.profile.ProfessionalOwnProfileScreen
import com.thecodefather.untigrito.presentation.screens.professional.services.CreateEditServiceScreen
import com.thecodefather.untigrito.presentation.screens.professional.services.MyServicesScreen
import com.thecodefather.untigrito.presentation.screens.professional.messages.SupportChatScreen

/**
 * Configuración de navegación para el módulo profesional
 */
object ProfessionalNavigation {
    const val PROFESSIONAL_MAIN = "professional_main"
    const val PROFESSIONAL_PROFILE = "professional_profile"
    const val PROFESSIONAL_SERVICES = "professional_services"
    const val CREATE_SERVICE = "create_service"
    const val EDIT_SERVICE = "edit_service/{serviceId}"
    const val SUPPORT_CHAT = "support_chat"
}

/**
 * Extensión para agregar las rutas del módulo profesional al NavGraph
 */
fun NavGraphBuilder.professionalNavigation(
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    composable(ProfessionalNavigation.PROFESSIONAL_MAIN) {
//        ProfessionalMainScreen(
//            navigationFather = navController,
//            onNavigateBack = onNavigateBack
//        )
    }
    
    composable(ProfessionalNavigation.PROFESSIONAL_PROFILE) {
        ProfessionalOwnProfileScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditService = { serviceId ->
                navController.navigate("edit_service/$serviceId")
            },
            onCreateService = {
                navController.navigate(ProfessionalNavigation.CREATE_SERVICE)
            },
            onViewAllServices = {
                navController.navigate(ProfessionalNavigation.PROFESSIONAL_SERVICES)
            }
        )
    }
    
    composable(ProfessionalNavigation.PROFESSIONAL_SERVICES) {
        MyServicesScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditService = { serviceId ->
                navController.navigate("edit_service/$serviceId")
            },
            onCreateService = {
                navController.navigate(ProfessionalNavigation.CREATE_SERVICE)
            }
        )
    }
    
    composable(ProfessionalNavigation.CREATE_SERVICE) {
        CreateEditServiceScreen(
            serviceId = null,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable(ProfessionalNavigation.EDIT_SERVICE) { backStackEntry ->
        val serviceId = backStackEntry.arguments?.getString("serviceId")
        CreateEditServiceScreen(
            serviceId = serviceId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable(ProfessionalNavigation.SUPPORT_CHAT) {
        SupportChatScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

/**
 * Funciones de conveniencia para navegación
 */
fun NavController.navigateToProfessional() {
    navigate(ProfessionalNavigation.PROFESSIONAL_MAIN)
}

fun NavController.navigateToProfessionalProfile() {
    navigate(ProfessionalNavigation.PROFESSIONAL_PROFILE)
}

fun NavController.navigateToProfessionalServices() {
    navigate(ProfessionalNavigation.PROFESSIONAL_SERVICES)
}

fun NavController.navigateToCreateService() {
    navigate(ProfessionalNavigation.CREATE_SERVICE)
}

fun NavController.navigateToEditService(serviceId: String) {
    navigate("edit_service/$serviceId")
}

fun NavController.navigateToSupportChat() {
    navigate(ProfessionalNavigation.SUPPORT_CHAT)
}
