package com.thecodefather.untigrito.presentation.screens.professional.services

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalServicesViewModel
import com.thecodefather.untigrito.presentation.viewmodel.CreateServiceRequest
import com.thecodefather.untigrito.presentation.viewmodel.UpdateServiceRequest
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfession

// Colores de la app
val PrimaryOrange = Color(0xFFE67822)
val PrimaryOrangeDark = Color(0xFFD2691E)
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFF9E9E9E)
val DarkGray = Color(0xFF424242)
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFF44336)
val WarningAmber = Color(0xFFFF9800)

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

    // Estados para UX mejorada
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val snackbarHostState = remember { SnackbarHostState() }

    // Calcular progreso del formulario
    val formProgress = remember {
        derivedStateOf {
            val fields = listOf(title, description, price, selectedCategoryId)
            val completedFields = fields.count { it.isNotBlank() }
            completedFields.toFloat() / fields.size
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = if (serviceId != null) "Editar Servicio" else "Crear Servicio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Completa la información de tu servicio",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Verificar si hay cambios sin guardar
                            val hasChanges = if (serviceId != null) {
                                // Para edición, comparar con el servicio original
                                val original = existingService
                                original?.let { orig ->
                                    orig.title != title || 
                                    orig.description != description || 
                                    orig.price.toString() != price || 
                                    orig.categoryId != selectedCategoryId || 
                                    orig.isActive != isActive
                                } ?: false
                            } else {
                                // Para creación, verificar si hay algún campo lleno
                                title.isNotBlank() || description.isNotBlank() || price.isNotBlank() || selectedCategoryId.isNotBlank()
                            }
                            
                            if (hasChanges) {
                                showUnsavedChangesDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (serviceId != null) {
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showDeleteDialog = true
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar servicio",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryOrange
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicador de progreso del formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = LightGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progreso del formulario",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkGray
                        )
                        Text(
                            text = "${(formProgress.value * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = formProgress.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryOrange,
                        trackColor = MediumGray.copy(alpha = 0.3f)
                    )
                }
            }
            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título del servicio") },
                            placeholder = { Text("Ej: Reparación de grifos") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Título",
                                    tint = if (title.isNotBlank()) PrimaryOrange else MediumGray
                                )
                            },
                            trailingIcon = {
                                if (title.isNotBlank()) {
                                    Icon(
                                        imageVector = if (title.length <= 60) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = if (title.length <= 60) "Válido" else "Muy largo",
                                        tint = if (title.length <= 60) SuccessGreen else WarningAmber
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            isError = title.isNotBlank() && title.length > 60,
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (title.isBlank()) "Mínimo 5 caracteres" 
                                               else if (title.length > 60) "Máximo 60 caracteres" 
                                               else "Título válido",
                                        color = if (title.isBlank()) MediumGray 
                                               else if (title.length > 60) ErrorRed 
                                               else SuccessGreen,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${title.length}/60",
                                        color = if (title.length > 60) ErrorRed else MediumGray,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                focusedLabelColor = PrimaryOrange,
                                focusedLeadingIconColor = PrimaryOrange
                            )
                        )
                    }

                    // Descripción
                    Column {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción del servicio") },
                            placeholder = { Text("Describe detalladamente qué incluye tu servicio, materiales, tiempo estimado, etc.") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Descripción",
                                    tint = if (description.isNotBlank()) PrimaryOrange else MediumGray
                                )
                            },
                            trailingIcon = {
                                if (description.isNotBlank()) {
                                    Icon(
                                        imageVector = if (description.length >= 20 && description.length <= 500) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = if (description.length >= 20 && description.length <= 500) "Válido" else "Revisar",
                                        tint = if (description.length >= 20 && description.length <= 500) SuccessGreen else WarningAmber
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            enabled = !isLoading,
                            isError = description.isNotBlank() && (description.length < 20 || description.length > 500),
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (description.isBlank()) "Mínimo 20 caracteres" 
                                               else if (description.length < 20) "Mínimo 20 caracteres" 
                                               else if (description.length > 500) "Máximo 500 caracteres" 
                                               else "Descripción válida",
                                        color = if (description.isBlank()) MediumGray 
                                               else if (description.length < 20 || description.length > 500) ErrorRed 
                                               else SuccessGreen,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${description.length}/500",
                                        color = if (description.length > 500) ErrorRed else MediumGray,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                focusedLabelColor = PrimaryOrange,
                                focusedLeadingIconColor = PrimaryOrange
                            )
                        )
                    }

                    // Precio
                    Column {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { newValue ->
                                // Solo permitir números y un punto decimal
                                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    price = newValue
                                }
                            },
                            label = { Text("Precio del servicio") },
                            placeholder = { Text("Ej: 150.00") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Precio",
                                    tint = if (price.isNotBlank() && price.toDoubleOrNull() != null && price.toDoubleOrNull()!! > 0) PrimaryOrange else MediumGray
                                )
                            },
                            trailingIcon = {
                                if (price.isNotBlank()) {
                                    val priceValue = price.toDoubleOrNull()
                                    Icon(
                                        imageVector = if (priceValue != null && priceValue > 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = if (priceValue != null && priceValue > 0) "Precio válido" else "Precio inválido",
                                        tint = if (priceValue != null && priceValue > 0) SuccessGreen else ErrorRed
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            isError = price.isNotBlank() && (price.toDoubleOrNull() == null || price.toDoubleOrNull()!! <= 0),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (price.isBlank()) "Ingresa el precio en bolivianos" 
                                               else if (price.toDoubleOrNull() == null) "Formato inválido" 
                                               else if (price.toDoubleOrNull()!! <= 0) "Precio debe ser mayor a 0" 
                                               else "Precio válido",
                                        color = if (price.isBlank()) MediumGray 
                                               else if (price.toDoubleOrNull() == null || price.toDoubleOrNull()!! <= 0) ErrorRed 
                                               else SuccessGreen,
                                        fontSize = 12.sp
                                    )
                                    if (price.isNotBlank() && price.toDoubleOrNull() != null && price.toDoubleOrNull()!! > 0) {
                                        Text(
                                            text = "Bs. ${String.format("%.2f", price.toDoubleOrNull()!!)}",
                                            color = PrimaryOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                focusedLabelColor = PrimaryOrange,
                                focusedLeadingIconColor = PrimaryOrange
                            )
                        )
                        
                        // Sugerencias de precios comunes
                        if (price.isBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Precios sugeridos:",
                                fontSize = 12.sp,
                                color = MediumGray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("50", "100", "150", "200", "300").forEach { suggestedPrice ->
                                    AssistChip(
                                        onClick = { price = suggestedPrice },
                                        label = { 
                                            Text(
                                                text = "Bs. $suggestedPrice",
                                                fontSize = 11.sp
                                            ) 
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Categoría
                    Column {
                        if (isLoadingProfessions) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PrimaryOrange
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cargando categorías...",
                                    color = MediumGray
                                )
                            }
                        } else {
                            var expanded by remember { mutableStateOf(false) }
                            val selectedProfession = professions.find { it.id == selectedCategoryId }
                            
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedProfession?.name ?: "",
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Categoría del servicio") },
                                    placeholder = { Text("Selecciona una categoría") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = "Categoría",
                                            tint = if (selectedCategoryId.isNotBlank()) PrimaryOrange else MediumGray
                                        )
                                    },
                                    trailingIcon = { 
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        ) 
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    enabled = !isLoading,
                                    isError = selectedCategoryId.isBlank() && expanded,
                                    supportingText = {
                                        Text(
                                            text = if (selectedCategoryId.isBlank()) "Selecciona la categoría que mejor describe tu servicio" 
                                                   else "Categoría seleccionada",
                                            color = if (selectedCategoryId.isBlank()) MediumGray else SuccessGreen,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryOrange,
                                        focusedLabelColor = PrimaryOrange,
                                        focusedLeadingIconColor = PrimaryOrange
                                    )
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    professions.forEach { profession ->
                                        DropdownMenuItem(
                                            text = { 
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Category,
                                                        contentDescription = null,
                                                        tint = PrimaryOrange,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = profession.name,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        if (profession.description != null) {
                                                            Text(
                                                                text = profession.description,
                                                                fontSize = 12.sp,
                                                                color = MediumGray
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            onClick = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                selectedCategoryId = profession.id
                                                expanded = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
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
            AnimatedVisibility(
                visible = title.isNotBlank() || description.isNotBlank() || price.isNotBlank(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Vista previa",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGray
                            )
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Vista previa",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Título con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = "Servicio",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = title.ifBlank { "Título del servicio" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (title.isBlank()) MediumGray else DarkGray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Descripción
                        Text(
                            text = description.ifBlank { "Descripción del servicio aparecerá aquí..." },
                            fontSize = 14.sp,
                            color = if (description.isBlank()) MediumGray else DarkGray,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Precio y estado
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Precio",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (price.isNotBlank()) "Bs. ${String.format("%.2f", price.toDoubleOrNull() ?: 0.0)}" else "Bs. 0.00",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (price.isBlank()) MediumGray else SuccessGreen
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isActive) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f),
                                modifier = Modifier.border(
                                    width = 1.dp,
                                    color = if (isActive) SuccessGreen else ErrorRed,
                                    shape = RoundedCornerShape(20.dp)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = "Estado",
                                        tint = if (isActive) SuccessGreen else ErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isActive) "Activo" else "Inactivo",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isActive) SuccessGreen else ErrorRed
                                    )
                                }
                            }
                        }
                        
                        // Información adicional
                        if (selectedCategoryId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = "Categoría",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = professions.find { it.id == selectedCategoryId }?.name ?: "Categoría seleccionada",
                                    fontSize = 12.sp,
                                    color = PrimaryOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        // Verificar si hay cambios sin guardar
                        val hasChanges = if (serviceId != null) {
                            // Para edición, comparar con el servicio original
                            val original = existingService
                            original?.let { orig ->
                                orig.title != title || 
                                orig.description != description || 
                                orig.price.toString() != price || 
                                orig.categoryId != selectedCategoryId || 
                                orig.isActive != isActive
                            } ?: false
                        } else {
                            // Para creación, verificar si hay algún campo lleno
                            title.isNotBlank() || description.isNotBlank() || price.isNotBlank() || selectedCategoryId.isNotBlank()
                        }
                        
                        if (hasChanges) {
                            showUnsavedChangesDialog = true
                        } else {
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkGray
                    ),
                    border = BorderStroke(1.dp, MediumGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancelar",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Botón Principal
                val isFormValid = title.isNotBlank() && description.isNotBlank() && 
                                 price.toDoubleOrNull()?.let { it > 0 } == true && selectedCategoryId.isNotBlank()
                
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isLoading && isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = MediumGray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (isFormValid) Brush.linearGradient(
                                    colors = listOf(PrimaryOrange, PrimaryOrangeDark)
                                ) else Brush.linearGradient(
                                    colors = listOf(MediumGray, MediumGray)
                                )
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (serviceId != null) "Actualizando..." else "Creando...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = if (serviceId != null) Icons.Default.Update else Icons.Default.Add,
                                    contentDescription = if (serviceId != null) "Actualizar" else "Crear",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (serviceId != null) "Actualizar" else "Crear",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar servicio
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Eliminar Servicio",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres eliminar este servicio? Esta acción no se puede deshacer.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        existingService?.let { service ->
                            viewModel.deleteService(service.id)
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkGray
                    ),
                    border = BorderStroke(1.dp, MediumGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para salir sin guardar
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = WarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Cambios sin guardar",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "Tienes cambios sin guardar. ¿Estás seguro de que quieres salir sin guardar?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showUnsavedChangesDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarningAmber
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Salir sin guardar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showUnsavedChangesDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkGray
                    ),
                    border = BorderStroke(1.dp, MediumGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continuar editando")
                }
            }
        )
    }
}