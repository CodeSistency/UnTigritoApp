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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.presentation.screens.client.components.ServiceCard
import com.thecodefather.untigrito.presentation.screens.client.components.ProfessionalCard
import com.thecodefather.untigrito.presentation.screens.client.components.CategoryChip
import com.thecodefather.untigrito.presentation.viewmodel.ServicesViewModel
import com.thecodefather.untigrito.utils.LocationUtils
import com.thecodefather.untigrito.utils.ServiceHelper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.clip
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import com.thecodefather.untigrito.domain.model.toProfessionalService

/**
 * Services/Professionals Screen
 * Allows searching and filtering services and professionals
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    navController: NavController,
    viewModel: ServicesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val servicesWithProfessionals by viewModel.servicesWithProfessionals.collectAsState()
    var selectedContentTypeIndex by remember { mutableStateOf(0) }
    val contentTypeTabs = listOf("Servicios", "Profesionales")

    // Pull-to-refresh state
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    // Cargar datos al inicializar
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        if (selectedContentTypeIndex == 0) {
            viewModel.loadServices()
        } else {
            viewModel.loadProfessionals()
        }
    }

    // Cargar datos cuando cambie la pestaña
    LaunchedEffect(selectedContentTypeIndex) {
        if (selectedContentTypeIndex == 0) {
            viewModel.loadServices()
        } else {
            viewModel.loadProfessionals()
        }
    }

    // Pull-to-refresh logic
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            if (selectedContentTypeIndex == 0) {
                viewModel.loadServices()
            } else {
                viewModel.loadProfessionals()
            }
            pullToRefreshState.endRefresh()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
    ) {
        HomeHeader(
            userName = "Juan Pérez",
            onMessageClick = {
                navController.navigate(Routes.createChatRoute("test_conversation"))
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Search bar
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.searchServices(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Busca servicios o profesionales...", style = MaterialTheme.typography.bodySmall,
                            ) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE67822)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Category filter
                item {
                    Text(
                        text = "Categorías",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // LazyRow de categorías
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        items(uiState.categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = uiState.selectedCategory == category.id,
                                onClick = {
                                    viewModel.filterByCategory(category.id)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Segmented control para Servicios/Profesionales
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFFF0F0F0)),
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

                // Estados de carga y error
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cargando servicios...", color = Color.Gray)
                        }
                    }
                } else if (uiState.errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Text(
                                text = "Error: ${uiState.errorMessage}",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    // Contenido dinámico basado en la pestaña seleccionada
                    if (selectedContentTypeIndex == 0) { // Pestaña de Servicios
                        if (uiState.services.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay servicios disponibles", color = Color.Gray)
                                }
                            }
                        } else {
                            items(uiState.services) { service ->
                                // Buscar datos del profesional para este servicio
                                val serviceWithProf = servicesWithProfessionals.find { it.id == service.id }
                                val professionalName = ServiceHelper.getProfessionalName(serviceWithProf)
                                val distance = ServiceHelper.calculateServiceDistance(
                                    userLat = null, // TODO: Obtener ubicación del usuario
                                    userLng = null,
                                    serviceWithProf = serviceWithProf
                                )

                                ServiceCard(
                                    service = service.toProfessionalService(),
                                    professionalName = professionalName,
                                    distance = distance,
                                    rating = service.rating,
                                    reviewCount = service.reviewCount,
                                    onClick = {
                                        navController.navigate("service_detail/${service.id}")
                                    }
                                )
                            }
                        }
                    } else { // Pestaña de Profesionales
                        if (uiState.professional.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay profesionales disponibles", color = Color.Gray)
                                }
                            }
                        } else {
                            items(uiState.professional) { professional ->
                                ProfessionalCard(
                                    professional = professional,
                                    onClick = {
                                        navController.navigate("professional_profile/${professional.id}")
                                    }
                                )
                            }
                        }
                    }
                }

                // Pull-to-refresh container
                // PullToRefreshContainer(
                //     state = pullToRefreshState,
                //     modifier = Modifier.align(Alignment.TopCenter)
                // )
            }
        }
    }
}