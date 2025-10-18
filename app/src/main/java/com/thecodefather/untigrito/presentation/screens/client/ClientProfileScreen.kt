package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
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
    val loading by viewModel.loading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Mi Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Profile Card
            item {
                user?.let { profile ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE67822).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = profile.name ?: "Usuario",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = profile.email ?: "Sin email",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = profile.phone ?: "Sin teléfono",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Estado", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        if (profile.isVerified) "Verificado" else "No verificado",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (profile.isVerified) Color.Green else Color.Red
                                    )
                                }

                                Column {
                                    Text("Balance", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "$${"%.2f".format(profile.balance)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE67822)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Verification Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Verificación de Identidad",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = user?.let { if (it.isIDVerified) "Identidad verificada" else "Verifica tu identidad para mayor seguridad" } ?: "",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        if (user?.isIDVerified == false) {
                            Button(
                                onClick = { viewModel.requestVerification() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE67822)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Verificar Identidad",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Settings Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Configuración",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "Notificaciones",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Privacidad",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Ayuda",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Red,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Cerrar Sesión",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }

        ClientBottomNavBar(
            currentRoute = "client_profile",
            onNavigate = { route ->
                if (route != "client_profile") {
                    navController.navigate(route)
                }
            }
        )
    }
}
