package com.example.vibecoding3.home.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.vibecoding3.ui.theme.Vibecoding3Theme
import androidx.navigation.NavHostController
import com.example.vibecoding3.navigation.MainRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContainer(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { /* Aquí iría tu contenido del menú lateral, si lo necesitas */ }
    ) {
        // Aquí se mostrará la HomeScreen
        HomeScreen(onNavigateToRechargeMethods = { navController.navigate(MainRoutes.RECHARGE_METHODS) })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenContainer() {
    Vibecoding3Theme {
        // Necesitamos pasar un NavHostController simulado para la vista previa
        // Esto es una simplificación y no maneja la navegación real en el Preview.
        // En una implementación real de Preview, se usaría un `rememberNavController()`
        // si se quisiera simular la navegación, pero para este caso no es crítico.
        MainScreenContainer(navController = androidx.navigation.compose.rememberNavController())
    }
}
