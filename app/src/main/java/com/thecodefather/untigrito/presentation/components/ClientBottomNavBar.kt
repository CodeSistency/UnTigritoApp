package com.thecodefather.untigrito.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.thecodefather.untigrito.presentation.navigation.Routes

@Composable
fun ClientBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 5.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.CLIENT_HOME,
            onClick = { onNavigate(Routes.CLIENT_HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE67822),
                selectedTextColor = Color(0xFFE67822),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.CLIENT_SERVICES,
            onClick = { onNavigate(Routes.CLIENT_SERVICES) },
            icon = { Icon(Icons.Default.Email, contentDescription = "Servicios") },
            label = { Text("Servicios", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE67822),
                selectedTextColor = Color(0xFFE67822),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.CLIENT_REQUESTS,
            onClick = { onNavigate(Routes.CLIENT_REQUESTS) },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Solicitudes") },
            label = { Text("Solicitudes", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE67822),
                selectedTextColor = Color(0xFFE67822),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.CLIENT_PROFILE,
            onClick = { onNavigate(Routes.CLIENT_PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE67822),
                selectedTextColor = Color(0xFFE67822),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewClientBottomNavBar() {
    ClientBottomNavBar(currentRoute = Routes.CLIENT_HOME) {}
}
