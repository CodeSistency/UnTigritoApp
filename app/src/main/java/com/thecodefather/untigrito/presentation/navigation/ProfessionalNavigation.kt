package com.thecodefather.untigrito.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.thecodefather.untigrito.presentation.screens.professional.ProfessionalMainScreen

/**
 * Configuración de navegación para el módulo profesional
 */
object ProfessionalNavigation {
    const val PROFESSIONAL_MAIN = "professional_main"
}

/**
 * Extensión para agregar las rutas del módulo profesional al NavGraph
 */
fun NavGraphBuilder.professionalNavigation(
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    composable(ProfessionalNavigation.PROFESSIONAL_MAIN) {
        ProfessionalMainScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

/**
 * Función de conveniencia para navegar al módulo profesional
 */
fun NavController.navigateToProfessional() {
    navigate(ProfessionalNavigation.PROFESSIONAL_MAIN)
}
