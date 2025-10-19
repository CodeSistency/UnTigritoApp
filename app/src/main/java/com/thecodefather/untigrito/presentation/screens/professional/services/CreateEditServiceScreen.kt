package com.thecodefather.untigrito.presentation.screens.professional.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalServicesViewModel
import com.thecodefather.untigrito.presentation.viewmodel.CreateServiceRequest
import com.thecodefather.untigrito.presentation.viewmodel.UpdateServiceRequest
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditServiceScreen(
    serviceId: String? = null, // null para crear, no null para editar
    onNavigateBack: () -> Unit,
    viewModel: ProfessionalServicesViewModel = hiltViewModel()
) {
    val services by viewModel.services.collectAsState()
    val professions by viewModel.professions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingProfessions by viewModel.isLoadingProfessions.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    // Encontrar el servicio a editar si existe
    val existingService = serviceId?.let { id ->
        services.find { it.id == id }
    }

    // Estados del formulario
    var title by remember { mutableStateOf(existingService?.title ?: "") }
    var description by remember { mutableStateOf(existingService?.description ?: "") }
    var price by remember { mutableStateOf(existingService?.price?.toString() ?: "") }
    var selectedCategoryId by remember { mutableStateOf(existingService?.categoryId ?: "") }
    var isActive by remember { mutableStateOf(existingService?.isActive ?: true) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar profesiones al inicializar
    LaunchedEffect(Unit) {
        viewModel.loadProfessions()
    }

    // Manejar éxito de operación
    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            snackbarHostState.showSnackbar(
                message = if (serviceId != null) "Servicio actualizado exitosamente" else "Servicio creado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetOperationSuccess()
            onNavigateBack()
        }
    }

    // Manejar errores
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
        containerColor = Color(0xFFF5F5F5), // Fondo gris claro
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (serviceId != null) "Editar Servicio" else "Crear Servicio"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5),
                    titleContentColor = Color(0xFF212121)
                ),
                actions = {
                    if (serviceId != null) {
                        IconButton(
                            onClick = {
                                existingService?.let { service ->
                                    viewModel.deleteService(service.id)
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp) // Padding consistente con client
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Información del Servicio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del servicio") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = title.isBlank() && title.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFE67822),
                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                )
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                enabled = !isLoading,
                isError = description.isBlank() && description.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFE67822),
                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                )
            )

                    // Precio
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio (Bs.)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        isError = price.toDoubleOrNull()?.let { it <= 0 } == true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE67822),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )

                    // Categoría
                    if (isLoadingProfessions) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargando categorías...")
                        }
                    } else {
                        var expanded by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
            OutlinedTextField(
                                value = professions.find { it.id == selectedCategoryId }?.name ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                                    .menuAnchor(),
                                enabled = !isLoading
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                professions.forEach { profession ->
                                    DropdownMenuItem(
                                        text = { Text(profession.name) },
                                        onClick = {
                                            selectedCategoryId = profession.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Estado activo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicio activo",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                            enabled = !isLoading
                        )
                    }
                }
            }

            // Preview del servicio
            if (title.isNotBlank() && description.isNotBlank() && price.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Vista previa",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bs. $price",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE67822) // Color naranja
                            )
                            
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Text(
                                        if (isActive) "Activo" else "Inactivo",
                                        color = if (isActive) Color(0xFF4CAF50) else Color.Red
                                    ) 
                                }
                            )
                        }
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        if (serviceId != null) {
                            // Actualizar servicio
                            viewModel.updateService(
                                serviceId,
                                UpdateServiceRequest(
                                    title = title,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    categoryId = selectedCategoryId,
                                    isActive = isActive
                                )
                            )
                        } else {
                            // Crear servicio
                            viewModel.createService(
                                CreateServiceRequest(
                                    title = title,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    categoryId = selectedCategoryId,
                                    isActive = isActive
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && 
                             price.toDoubleOrNull()?.let { it > 0 } == true && selectedCategoryId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE67822)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                if (isLoading) {
                    CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            if (serviceId != null) "Actualizar" else "Crear"
                        )
                    }
                }
            }
        }
    }
}