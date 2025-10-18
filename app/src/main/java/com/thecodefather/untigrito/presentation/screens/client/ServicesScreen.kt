package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.presentation.viewmodel.ServicesViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip

/**
 * Services/Professionals Screen
 * Allows searching and filtering professionals by specialty
 */
@Composable
fun ServicesScreen(
    navController: NavController,
    viewModel: ServicesViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedContentTypeIndex by remember { mutableStateOf(0) } // Estado para el control segmentado

    val contentTypeTabs = listOf("Servicios", "Profesionales")

    val professionals by viewModel.professionals.collectAsState(initial = emptyList())
    val loading by viewModel.loading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            HomeHeader(userName = "Juan Pérez") // Integrar HomeHeader
            Spacer(modifier = Modifier.height(16.dp))
        }
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Busca servicios o profesionales...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category filter (siempre visible)
        item {
            Text(
                text = "Categorías",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            CategoryFilterRow { category ->
                selectedCategory = if (selectedCategory == category) null else category
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Segmented control para Servicios/Profesionales
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp)) // Esquinas redondeadas para todo el Row
                    .background(Color(0xFFF0F0F0)), // Fondo para el control segmentado
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                contentTypeTabs.forEachIndexed { index, text ->
                    val isSelected = selectedContentTypeIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedContentTypeIndex = index }
                            .background(
                                color = if (isSelected) Color(0xFFE67822) else Color.Transparent,
                                shape = RoundedCornerShape(25.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Contenido dinámico basado en la pestaña seleccionada
        if (selectedContentTypeIndex == 0) { // Pestaña de Servicios (placeholder)
            items(5) { // 5 elementos de ejemplo para servicios
                // Usamos ProfessionalCard como placeholder por ahora
                // En una implementación real, sería una ServiceCard
                ProfessionalCard(
                    professional = Professional(
                        id = "service_id_$it",
                        userId = "Andrés Rodríguez",
                        bio = "Servicio de plomería urgente 24/7",
                        rating = 4.8,
                        totalReviews = 120,
                        yearsOfExperience = 5,
                        specialties = listOf("Plomería"),
                        hourlyRate = 45.0,
                        imageUrl = "https://via.placeholder.com/150" // Imagen de ejemplo
                    ),
                    onClick = { /* TODO: Navegar al detalle del servicio */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else { // Pestaña de Profesionales
            items(professionals) { professional ->
                ProfessionalCard(
                    professional = professional,
                    onClick = {
                        navController.navigate("professional_detail/${professional.id}")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Empty state for Professionals
            if (professionals.isEmpty() && !loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay profesionales disponibles",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(onCategorySelected: (String) -> Unit) {
    val categories = listOf(
        "Plomería",
        "Electricidad",
        "Albañilería",
        "Limpieza",
        "Mudanza"
    )
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Row( // Cambiado a Row para el icono y LazyRow para las categorías
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .height(40.dp)
                .width(56.dp), // Ancho fijo para el icono de filtro
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtrar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                if (isSelected) {
                    Card(
                        onClick = { onCategorySelected(category) ; selectedCategory = if (selectedCategory == category) null else category},
                        modifier = Modifier
                            .height(40.dp)
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE67822) // Naranja para la categoría seleccionada
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White // Texto blanco para la categoría seleccionada
                            )
                        }
                    }
                } else {
                    OutlinedCard(
                        onClick = { onCategorySelected(category) ; selectedCategory = if (selectedCategory == category) null else category},
                        modifier = Modifier
                            .height(40.dp)
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE67822)), // Borde naranja para la categoría no seleccionada
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE67822) // Texto naranja para la categoría no seleccionada
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalCard(
    professional: Professional,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre imagen y contenido
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del servicio/profesional
            // AsyncImage(
            //     model = professional.imageUrl ?: R.drawable.ic_launcher_foreground, // Usar ic_launcher_foreground como placeholder
            //     contentDescription = "Imagen del servicio",
            //     contentScale = ContentScale.Crop,
            //     modifier = Modifier
            //         .size(90.dp) // Tamaño de la imagen
            //         .clip(RoundedCornerShape(8.dp))
            // )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = professional.specialties.firstOrNull() ?: "Servicio Desconocido", // Usar la primera especialidad como título
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = professional.bio ?: "Sin descripción", // Usar bio como descripción del servicio
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1, // Limitar a una línea
                            overflow = TextOverflow.Ellipsis // Añadir puntos suspensivos si el texto es muy largo
                        )
                    }

                    // Distancia
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${(10..100).random()} Km", // Distancia aleatoria temporal
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre las filas

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A partir de: $${professional.hourlyRate ?: 0.0} ", // Usar hourlyRate
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Por: ${professional.userId}",
                            fontSize = 12.sp,
                            color = Color(0xFFE67822) // Naranja para el nombre del profesional
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Color de estrella
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${ "%.1f".format(professional.rating ?: 0.0)} (${professional.totalReviews ?: 0})", // Usar totalReviews
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
