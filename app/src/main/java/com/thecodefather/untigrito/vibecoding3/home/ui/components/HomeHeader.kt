package com.example.vibecoding3.home.ui.components


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
import com.example.vibecoding3.R

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
            .padding(horizontal = 16.dp, vertical = 8.dp) // Relleno global para el encabezado
    ) {
        // --- Parte Superior: Logo ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reemplaza con tu propio drawable para el logo
            // Por ejemplo, R.drawable.logo_untigrito
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Asegúrate de crear este drawable
                contentDescription = "Logo de UnTigrito",
                modifier = Modifier.height(28.dp) // Ajusta el tamaño del logo si es necesario
            )
        }

        Spacer(modifier = Modifier.size(16.dp)) // Espacio entre el logo y la sección de bienvenida

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
                // Avatar del usuario (Placeholder)
                // Usamos un Box con un color de fondo y un icono o imagen de ejemplo
                Box(
                    modifier = Modifier
                        .size(56.dp) // Tamaño del avatar
                        .clip(CircleShape) // Forma circular
                        .background(Color(0xFF8BC34A)), // Color de fondo del avatar (verde de ejemplo)
                    contentAlignment = Alignment.Center
                ) {
                    // Puedes reemplazar esto con una imagen real del usuario:
                    // AsyncImage(
                    //     model = "URL_DE_LA_IMAGEN_DEL_AVATAR",
                    //     contentDescription = "Avatar de usuario",
                    //     contentScale = ContentScale.Crop,
                    //     modifier = Modifier.fillMaxSize()
                    // )
                    // Para el preview, usaré un icono simple
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Otro drawable de ejemplo
                        contentDescription = "Avatar de usuario",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }


                Spacer(modifier = Modifier.width(12.dp)) // Espacio entre avatar y texto

                Column {
                    Text(
                        text = "Bienvenido de nuevo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryTextColor // Un gris más suave para el texto de bienvenida
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall, // Tamaño de texto más grande para el nombre
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
                        .size(48.dp) // Tamaño del IconButton (incluye el padding interno)
                        .clip(CircleShape)
                        .background(Color.DarkGray.copy(alpha = 0.8f)) // Fondo oscuro y circular
                ) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Mensajes",
                        tint = Color.White, // Icono blanco
                        modifier = Modifier.size(24.dp) // Tamaño del icono real
                    )
                }

                // Icono de Notificaciones
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(48.dp) // Tamaño del IconButton
                        .clip(CircleShape)
                        .background(Color.DarkGray.copy(alpha = 0.8f)) // Fondo oscuro y circular
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notificaciones",
                        tint = Color.White, // Icono blanco
                        modifier = Modifier.size(24.dp) // Tamaño del icono real
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