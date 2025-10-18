package com.thecodefather.untigrito.presentation.components.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thecodefather.untigrito.domain.model.Job

/**
 * Componente de tarjeta para mostrar un trabajo
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCard(
    job: Job,
    onJobClick: (String) -> Unit = {},
    onFavoriteClick: (String, Boolean) -> Unit = { _, _ -> },
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onJobClick(job.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagen del trabajo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.LightGray)
            ) {
                if (job.clientAvatar != null) {
                    AsyncImage(
                        model = job.clientAvatar,
                        contentDescription = job.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Botón de favorito
                IconButton(
                    onClick = { onFavoriteClick(job.id, !isFavorite) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            Color.White.copy(alpha = 0.8f),
                            RoundedCornerShape(topEnd = 12.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.Gray
                    )
                }
            }
            
            // Contenido de la tarjeta
            Column(modifier = Modifier.padding(12.dp)) {
                // Título
                Text(
                    text = job.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Descripción
                Text(
                    text = job.description,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Información del cliente
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Avatar del cliente (círculo placeholder)
                        Surface(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color(0xFFE0E0E0)
                        ) {
                            if (job.clientAvatar != null) {
                                AsyncImage(
                                    model = job.clientAvatar,
                                    contentDescription = job.clientName,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = job.clientName.first().toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = job.clientName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color(0xFFFFC107)
                                )
                                Text(
                                    text = "1$",
//                                    text = String.format("%.1f", job.clientRating),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                
                // Ubicación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF1976D2)
                    )
                    Text(
                        text = job.location?.address.orEmpty(),
                        fontSize = 12.sp,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(start = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Presupuesto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Presupuesto",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("$%.2f", job.budget),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                // Tags (categorías)
                if (job.requiredSkills.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        job.requiredSkills.take(3).forEach { tag ->
                            Surface(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                                color = Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 10.sp,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Preview para ver la tarjeta de trabajo
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JobCardPreview() {
    val sampleJob = Job(
        id = "1",
        title = "Reparación de Tuberías",
        description = "Necesito reparar las tuberías del baño que tienen fugas. Es urgente porque está goteando mucha agua.",
        clientId = "client1",
        clientName = "Juan Pérez",
        clientAvatar = "https://picsum.photos/200",
        category = "Plomería",
        budget = 150.0,
        status = com.thecodefather.untigrito.domain.model.JobStatus.OPEN,
        location = com.thecodefather.untigrito.domain.model.Location(
            address = "Av. Siempre Viva 742",
            city = "Lima",
            state = "Lima",
            coordinates = com.thecodefather.untigrito.domain.model.Coordinates(
                latitude = -12.0464,
                longitude = -77.0428
            )
        ),
        deadline = java.util.Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000), // 3 días desde ahora
        createdAt = java.util.Date(),
        isRecommended = true,
        isFavorite = false,
        urgency = com.thecodefather.untigrito.domain.model.UrgencyLevel.HIGH,
        estimatedDuration = "2-3 horas",
        requiredSkills = listOf("Plomería", "Reparación", "Instalación"),
        attachments = listOf(
            "https://picsum.photos/400/300",
            "https://picsum.photos/400/301"
        )
    )
    
    JobCard(
        job = sampleJob,
        isFavorite = false,
        modifier = Modifier.padding(8.dp)
    )
}
