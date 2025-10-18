package com.thecodefather.untigrito.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Client Bottom Navigation Bar
 * Provides navigation between main screens: Home, Services, Requests, Profile
 */
@Composable
fun ClientBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(
            label = "Inicio",
            icon = Icons.Default.Home,
            route = "client_home",
            contentDescription = "Home"
        ),
        BottomNavItem(
            label = "Servicios",
            icon = Icons.Default.Search,
            route = "client_services",
            contentDescription = "Services"
        ),
        BottomNavItem(
            label = "Solicitudes",
            icon = Icons.Default.ShoppingCart,
            route = "client_requests",
            contentDescription = "Requests"
        ),
        BottomNavItem(
            label = "Perfil",
            icon = Icons.Default.Person,
            route = "client_profile",
            contentDescription = "Profile"
        )
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFFE67822)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription,
                        modifier = androidx.compose.ui.Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE67822),
                    selectedTextColor = Color(0xFFE67822),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val contentDescription: String
)
