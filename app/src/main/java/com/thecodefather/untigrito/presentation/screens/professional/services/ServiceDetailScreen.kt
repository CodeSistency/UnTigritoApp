package com.thecodefather.untigrito.presentation.screens.professional.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.presentation.components.SolicitudCard
import com.thecodefather.untigrito.presentation.viewmodel.NavigationEvent
import com.thecodefather.untigrito.presentation.viewmodel.ServiceDetailViewModel

/**
 * Pantalla de detalles de servicio con solicitudes de clientes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProposals: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ServiceDetailViewModel = hiltViewModel()
) {
    val service by viewModel.service.collectAsState()
    val requests by viewModel.serviceRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(serviceId) {
        viewModel.loadServiceDetail(serviceId)
    }
    
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            NavigationEvent.NavigateToProposals -> {
                onNavigateToProposals()
                viewModel.clearNavigationEvent()
            }
            null -> {}
        }
    }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalles de servicio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { service?.id?.let { onNavigateToEdit(it) } }) {
                        Icon(Icons.Default.Edit, "Editar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading && service == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE67822))
            }
        } else {
            service?.let { serviceData ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagen del servicio
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Imagen del servicio",
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                    
                    // Estado y precio
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        if (serviceData.isActive) "activo" else "inactivo",
                                        color = if (serviceData.isActive) Color(0xFF4CAF50) else Color.Red
                                    )
                                }
                            )
                            Text(
                                text = "Bs. ${serviceData.price.toInt()} - Bs. ${(serviceData.price * 1.2).toInt()} Estimado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                    
                    // Título y descripción
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = serviceData.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = serviceData.description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    
                    // Tags de categorías
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("#plomeria", "#albanileria", "#albanileria", "#lavaderodetanques", "#tuberias", 
                                  "#lavanderia", "#albanileria", "#lavaderodetanques").forEach { tag ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(tag, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                    
                    // Ubicación
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Ubicación",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Av. Principal 123, Barrio Norte",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            
                            // Mapa simulado
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Mapa de la Zona de Servicio (Simulado)", color = Color.Gray)
                            }
                        }
                    }
                    
                    // Sección de Solicitudes
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Solicitudes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.InsertEmoticon,
                                contentDescription = "Emoji",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Lista de solicitudes
                    items(requests) { request ->
                        SolicitudCard(
                            request = request,
                            onAccept = { viewModel.acceptRequest(request.transaction.id) },
                            onDecline = { viewModel.declineRequest(request.transaction.id) }
                        )
                    }
                    
                    // Estado vacío
                    if (requests.isEmpty() && !isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay solicitudes pendientes",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    
                    // Botón Actualizar
                    item {
                        Button(
                            onClick = { viewModel.loadServiceDetail(serviceId) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822))
                        ) {
                            Text("Actualizar")
                        }
                    }
                }
            }
        }
    }
}
