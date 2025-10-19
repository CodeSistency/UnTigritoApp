package com.thecodefather.untigrito.presentation.screens.client

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.navigation.ProfessionalNavigation
import com.thecodefather.untigrito.presentation.navigation.ClientRoutes
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.presentation.viewmodel.ClientProfileViewModel

/**
 * Client Profile Screen
 * Displays user profile information and account settings
 */
@Composable
fun ClientProfileScreen(
    navController: NavController,
    viewModel: ClientProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = null)
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    var isProfessional by remember { mutableStateOf(false) } // Estado para el switch "Soy un profesional"

    // Navegar al login cuando el logout sea exitoso
    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.CLIENT_MAIN) { inclusive = true }
            }
        }
    }
    
    val context= LocalContext.current


    // Navegar a la sección de profesionales cuando el switch esté en true
    LaunchedEffect(isProfessional) {
        if (isProfessional) {
            val currentUser = user
            if (currentUser?.role == "PROFESSIONAL") {
                // Ya es profesional, navegar directamente
                navController.navigate(ProfessionalNavigation.PROFESSIONAL_MAIN)
            } else {
                Toast.makeText(context, "Verifica tu cuenta para activar tu perfil profesional", Toast.LENGTH_SHORT).show()
                isProfessional = false // Reset toggle
                
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.account_circle), // Placeholder
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier.size(140.dp)
                )
                Text(
                    "Juan Pérez",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Teléfono:+58 412 123 4567",
                    textAlign = TextAlign.Center
                )
                Text(
                    "Dirección:Calle 10, Edificio El Tigre, Caracas",
                    textAlign = TextAlign.Center
                )
            }

        }

        item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Soy un profesional",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Switch(
                        checked = isProfessional,
                        onCheckedChange = { isProfessional = it }
                    )
                }
        }

        item {
            // Verificaciones (adaptado de la imagen)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val currentUser = user
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (currentUser?.role == "PROFESSIONAL") {
                                    // Ya es profesional, navegar directamente
                                    navController.navigate(ProfessionalNavigation.PROFESSIONAL_MAIN)
                                } else {
                                    // No es profesional, ir a verificación
                                    navController.navigate(Routes.IDENTITY_VERIFICATION)
                                    isProfessional = false // Reset toggle
                                }
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Verificaciones", fontSize = 16.sp, color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (user?.isVerified == true) "Verificado" else "No verificado",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Navegar a Historial de Servicios */ }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Historial de Servicios", fontSize = 16.sp, color = Color.Black)
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.logout()
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cerrar Sesión", fontSize = 16.sp, color = Color.Black)
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }

                }
            }
        }
    }
}
