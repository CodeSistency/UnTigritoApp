package com.thecodefather.untigrito.presentation.screens.professional.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditServiceScreen(
    serviceId: String? = null,
    onNavigateBack: () -> Unit,
    onServiceSaved: () -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.ServicesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var serviceArea by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val isEditMode = serviceId != null
    val screenTitle = if (isEditMode) "Editar Servicio" else "Crear Servicio"

    // Cargar datos del servicio si estamos editando
    LaunchedEffect(serviceId) {
        if (isEditMode && serviceId != null) {
            // Aquí cargarías los datos del servicio desde el ViewModel
            // Por ahora usamos datos dummy
            title = "Servicio de ejemplo"
            description = "Descripción del servicio"
            category = "Plomería"
            minPrice = "50"
            maxPrice = "200"
            serviceArea = "Caracas"
            isActive = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título del servicio
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del Servicio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                supportingText = { Text("Un título claro y atractivo para tu servicio") }
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                minLines = 3,
                maxLines = 5,
                supportingText = { Text("Describe detalladamente qué incluye tu servicio") }
            )

            // Categoría
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                supportingText = { Text("Ej: Plomería, Electricidad, Pintura, etc.") }
            )

            // Precios
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    label = { Text("Precio Mínimo ($)") },
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("Precio mínimo") }
                )

                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    label = { Text("Precio Máximo ($)") },
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("Precio máximo") }
                )
            }

            // Área de servicio
            OutlinedTextField(
                value = serviceArea,
                onValueChange = { serviceArea = it },
                label = { Text("Área de Servicio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                supportingText = { Text("Zonas donde ofreces tu servicio") }
            )

            // Estado del servicio
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estado del Servicio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isActive) "Servicio activo" else "Servicio inactivo",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Text(
                        text = if (isActive) 
                            "Tu servicio será visible para los clientes" 
                        else 
                            "Tu servicio no será visible para los clientes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Botón de guardar
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && 
                        minPrice.isNotBlank() && maxPrice.isNotBlank() && serviceArea.isNotBlank()) {
                        
                        isLoading = true
                        
                        if (isEditMode) {
                            // Lógica para actualizar servicio
                            onServiceSaved()
                        } else {
                            // Lógica para crear servicio
                            viewModel.createService(
                                title = title.trim(),
                                description = description.trim(),
                                category = category.trim(),
                                minPrice = minPrice.toDoubleOrNull() ?: 0.0,
                                maxPrice = maxPrice.toDoubleOrNull() ?: 0.0,
                                serviceArea = serviceArea.trim()
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && 
                         category.isNotBlank() && minPrice.isNotBlank() && 
                         maxPrice.isNotBlank() && serviceArea.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) "Guardando..." else if (isEditMode) "Actualizar Servicio" else "Crear Servicio")
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

    // Manejo de éxito
    LaunchedEffect(uiState.showCreateSuccess, uiState.showUpdateSuccess) {
        if (uiState.showCreateSuccess || uiState.showUpdateSuccess) {
            onServiceSaved()
        }
    }
}
