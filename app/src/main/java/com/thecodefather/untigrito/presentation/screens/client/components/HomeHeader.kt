package com.thecodefather.untigrito.presentation.screens.client.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.R

// Definición de colores personalizados si no están en tu tema de Material 3
val OrangeUntigrito = Color(0xFFE65100) // Un naranja oscuro similar al del logo
val PrimaryTextColor = Color(0xFF212121) // Un gris oscuro para el texto principal
val SecondaryTextColor = Color(0xFF616161) // Un gris más claro para el texto secundario

@Composable
fun HomeHeader(
    userName: String,
    modifier: Modifier = Modifier,
    onMessageClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White) // Añadir fondo blanco al encabezado
            .padding(horizontal = 16.dp, vertical = 8.dp) // Relleno global para el encabezado
    ) {
        // --- Parte Superior: Logo ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UnTigrito®",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = OrangeUntigrito
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el logo y la sección de bienvenida

        // --- Parte Inferior: Bienvenida, Avatar y Acciones ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Empuja el contenido a los extremos
        ) {
            // Columna Izquierda: Avatar y Texto de Bienvenida
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del usuario
                Box(
                    modifier = Modifier
                        .size(48.dp) // Tamaño del avatar
                        .clip(CircleShape) // Forma circular
                        .background(Color(0xFFB0BEC5)), // Un color gris claro para el fondo del avatar
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                        contentDescription = "Avatar de usuario",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }


                Spacer(modifier = Modifier.width(12.dp)) // Espacio entre avatar y texto

                Column {
                    Text(
                        text = "Bienvenido de nuevo",
                        style = MaterialTheme.typography.bodySmall, // Un tamaño de texto más pequeño
                        color = SecondaryTextColor // Un gris más suave para el texto de bienvenida
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge, // Tamaño de texto para el nombre
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor // Color más oscuro para el nombre
                    )
                }
            }

            // Columna Derecha: Iconos de Notificaciones y Mensajes
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre los iconos
            ) {
                // Icono de Mensajes
                IconButton(
                    onClick = onMessageClick,
                    modifier = Modifier
                        .size(40.dp) // Tamaño del IconButton (más pequeño)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)) // Fondo gris claro
                ) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Mensajes",
                        tint = Color(0xFF424242), // Icono gris oscuro
                        modifier = Modifier.size(20.dp) // Tamaño del icono real
                    )
                }

                // Icono de Notificaciones
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(40.dp) // Tamaño del IconButton
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)) // Fondo gris claro
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notificaciones",
                        tint = Color(0xFF424242), // Icono gris oscuro
                        modifier = Modifier.size(20.dp) // Tamaño del icono real
                    )
                }
            }
        }
    }
}

// --- Preview para visualizar el componente en Android Studio ---
@Preview(showBackground = true)
@Composable
fun HomeHeaderPreview() {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeHeader(userName = "Juan Pérez")
        }
}   