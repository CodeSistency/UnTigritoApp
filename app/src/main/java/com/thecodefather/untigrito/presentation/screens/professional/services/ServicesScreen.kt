package com.thecodefather.untigrito.presentation.screens.professional.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ServiceFilter
import com.thecodefather.untigrito.presentation.components.FilterTabs
import com.thecodefather.untigrito.presentation.components.ServiceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onServiceClick: (String) -> Unit,
    onCreateService: () -> Unit,
    onEditService: (String) -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.ServicesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadServices()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateService
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Crear servicio"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "Mis Servicios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filtros
            FilterTabs(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = viewModel::updateFilter,
                filters = listOf(
                    ServiceFilter.ALL to "Todos",
                    ServiceFilter.ACTIVE to "Activos",
                    ServiceFilter.INACTIVE to "Inactivos"
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de servicios
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.services.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tienes servicios publicados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onCreateService
                            ) {
                                Text("Crear mi primer servicio")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.services) { service ->
                            ServiceCard(
                                service = service,
                                onServiceClick = { onServiceClick(service.id) },
                                onEditClick = { onEditService(service.id) },
                                onToggleStatus = { viewModel.toggleServiceStatus(service.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Manejo de errores
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar
            viewModel.clearError()
        }
    }

    // Manejo de mensajes de éxito
    LaunchedEffect(uiState.showCreateSuccess, uiState.showUpdateSuccess, uiState.showDeleteSuccess, uiState.showToggleSuccess) {
        if (uiState.showCreateSuccess || uiState.showUpdateSuccess || uiState.showDeleteSuccess || uiState.showToggleSuccess) {
            // Aquí podrías mostrar un Snackbar de éxito
            viewModel.clearSuccessMessages()
        }
    }
}
