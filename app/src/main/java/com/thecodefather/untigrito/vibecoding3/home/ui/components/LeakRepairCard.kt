package com.example.vibecoding3.home.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definimos el color dorado personalizado para la calificación y el nombre
val Gold = Color(0xFFFBC02D)

@Composable
fun ServiceCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp), // Esquinas redondeadas como en la imagen
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.Top // Alineamos al top para que la imagen y el texto empiecen igual
        ) {
            // --- Columna Izquierda: Imagen ---
            // Usamos un Box como placeholder para la imagen
            Box(
                modifier = Modifier
                    .size(72.dp) // Tamaño de la imagen
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // Icono de placeholder
                Icon(
                    imageVector = Icons.Filled.Engineering,
                    contentDescription = "Imagen de servicio",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
                // Si usaras una imagen real (ej. con Coil):
                // AsyncImage(
                //    model = "https://url.de.tu.imagen.com/worker.jpg",
                //    contentDescription = "Trabajador de servicio",
                //    contentScale = ContentScale.Crop,
                //    modifier = Modifier.fillMaxSize()
                // )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- Columna Derecha: Información ---
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                // --- Fila 1: Título y Distancia ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Empuja los elementos a los extremos
                ) {
                    // Título
                    Text(
                        text = "Reparación de Fugas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Indicador de Distancia
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Distancia",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, // Color grisáceo
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "10 Km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Color grisáceo
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- Fila 2: Subtítulo/Descripción ---
                Text(
                    text = "Servicio de plomería urgente 24/7",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Color grisáceo
                )

                Spacer(modifier = Modifier.height(10.dp))

                // --- Fila 3: Proveedor y Calificación ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
                ) {
                    // Nombre del proveedor (con estilo mixto)
                    Text(
                        text = buildAnnotatedString {
                            append("Por: ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold // Color dorado personalizado
                                )
                            ) {
                                append("Andrés Rodríguez")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp // Ajuste fino de tamaño
                    )

                    // Divisor
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )

                    // Calificación
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Calificación",
                            tint = Gold, // Color dorado
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "4.8 (120)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Gold,
                            fontSize = 13.sp // Ajuste fino de tamaño
                        )
                    }
                }
            }
        }
    }
}

// --- Preview para visualizar el componente en Android Studio ---
@Preview(showBackground = true)
@Composable
fun ServiceCardPreview() {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            ServiceCard()
        }

}