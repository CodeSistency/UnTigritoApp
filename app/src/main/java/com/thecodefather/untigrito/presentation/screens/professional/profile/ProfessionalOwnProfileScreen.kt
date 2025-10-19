package com.thecodefather.untigrito.presentation.screens.professional.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.json.JsonArray
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalViewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import com.thecodefather.untigrito.presentation.components.ImagePicker
import android.net.Uri
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalServicesViewModel
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOwnProfileScreen(
    onNavigateBack: () -> Unit,
    onEditService: (String) -> Unit,
    onCreateService: () -> Unit,
    onViewAllServices: () -> Unit,
    viewModel: ProfessionalViewModel = hiltViewModel(),
    servicesViewModel: ProfessionalServicesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val professionalProfile by viewModel.professionalProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Estados editables
    val editableName by viewModel.editableName.collectAsState()
    val editableEmail by viewModel.editableEmail.collectAsState()
    val editableBio by viewModel.editableBio.collectAsState()
    val editableHourlyRate by viewModel.editableHourlyRate.collectAsState()
    val editableCertifications by viewModel.editableCertifications.collectAsState()
    val editableResponseTime by viewModel.editableResponseTime.collectAsState()
    val editableBankAccount by viewModel.editableBankAccount.collectAsState()
    val editableTaxId by viewModel.editableTaxId.collectAsState()
    val editableSpecialties by viewModel.editableSpecialties.collectAsState()
    
    // Estados de feedback
    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    
    // Estados para imagen de perfil
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    
    // Estados de servicios
    val services by servicesViewModel.services.collectAsState()
    val isServicesLoading by servicesViewModel.isLoading.collectAsState()
    
    var isEditing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Cargar servicios al inicializar
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            servicesViewModel.loadServices(userId)
        }
    }
    
    // Manejar feedback de actualización
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar(
                message = "Perfil actualizado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetUpdateSuccess()
            isEditing = false
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
                title = { Text("Mi Perfil") },
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
                    if (!isEditing) {
                        TextButton(onClick = { isEditing = true }) {
                            Text("Editar")
                        }
                    } else {
                        TextButton(onClick = { 
                            isEditing = false
                            // Recargar datos originales
                            viewModel.loadEditableData()
                        }) {
                            Text("Cancelar")
                        }
                        TextButton(
                            onClick = { 
                                viewModel.saveProfileChanges()
                            },
                            enabled = !isUpdating
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Guardar")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información Personal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto de perfil
                        ImagePicker(
                            currentImageUrl = profileImageUrl,
                            isUploading = isUploadingImage,
                            onImageSelected = { uri ->
                                viewModel.uploadProfileImage(uri, context)
                            },
                            onImageRemoved = {
                                viewModel.removeProfileImage()
                            },
                            modifier = Modifier.size(100.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isEditing) {
                            OutlinedTextField(
                                value = editableName,
                                onValueChange = { viewModel.updateField("name", it) },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating,
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color(0xFFE67822),
                                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = editableEmail,
                                onValueChange = { viewModel.updateField("email", it) },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating,
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color(0xFFE67822),
                                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = editableBio,
                                onValueChange = { viewModel.updateField("bio", it) },
                                label = { Text("Biografía") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                enabled = !isUpdating,
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color(0xFFE67822),
                                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                )
                            )
                        } else {
                            Text(
                                text = currentUser?.name ?: "Usuario",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Text(
                                text = currentUser?.email ?: "email@ejemplo.com",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            if (!professionalProfile?.bio.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = professionalProfile?.bio ?: "",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
            
            // Estadísticas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Estadísticas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                title = "Rating",
                                value = "${professionalProfile?.ratingAvg ?: 0.0}",
                                icon = Icons.Default.Star,
                                color = Color(0xFFFFC107)
                            )
                            
                            StatCard(
                                title = "Reseñas",
                                value = "${professionalProfile?.ratingCount ?: 0}",
                                icon = Icons.Default.RateReview,
                                color = Color(0xFF2196F3)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                title = "Experiencia",
                                value = "${professionalProfile?.yearsOfExperience ?: 0} años",
                                icon = Icons.Default.Work,
                                color = Color(0xFF4CAF50)
                            )
                            
                            StatCard(
                                title = "Completitud",
                                value = "${professionalProfile?.completionRate ?: 0.0}%",
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF9C27B0)
                            )
                        }
                    }
                }
            }
            
            // Información Profesional
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información Profesional",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (isEditing) {
                            OutlinedTextField(
                                value = editableHourlyRate,
                                onValueChange = { viewModel.updateField("hourlyRate", it) },
                                label = { Text("Tarifa por hora (Bs.)") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = editableCertifications,
                                onValueChange = { viewModel.updateField("certifications", it) },
                                label = { Text("Certificaciones") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                enabled = !isUpdating
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = editableResponseTime,
                                onValueChange = { viewModel.updateField("responseTime", it) },
                                label = { Text("Tiempo de respuesta (horas)") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating
                            )
                        } else {
                            InfoRow("Tarifa por hora", "Bs. ${professionalProfile?.hourlyRate ?: 0.0}")
                            InfoRow("Certificaciones", professionalProfile?.certifications ?: "No especificadas")
                            InfoRow("Tiempo de respuesta", "${professionalProfile?.responseTime ?: 0} horas")
                        }
                        
                        // Especialidades
                        val specialties = professionalProfile?.specialties?.let { jsonElement ->
                            try {
                                if (jsonElement is kotlinx.serialization.json.JsonArray) {
                                    jsonElement.map { it.toString().trim('"') }
                                } else {
                                    jsonElement.toString().trim('"').split(",").map { it.trim() }
                                }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } ?: emptyList()
                        
                        if (specialties.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Especialidades",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                specialties.forEach { specialty ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(specialty) },
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Información Bancaria (solo visible para el dueño)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información Bancaria",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (isEditing) {
                            OutlinedTextField(
                                value = editableBankAccount,
                                onValueChange = { viewModel.updateField("bankAccount", it) },
                                label = { Text("Cuenta Bancaria") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = editableTaxId,
                                onValueChange = { viewModel.updateField("taxId", it) },
                                label = { Text("RIF/Tax ID") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating
                            )
                        } else {
                            InfoRow("Cuenta Bancaria", professionalProfile?.bankAccount ?: "No especificada")
                            InfoRow("RIF/Tax ID", professionalProfile?.taxId ?: "No especificado")
                        }
                    }
                }
            }
            
            // Servicios Publicados
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                text = "Mis Servicios",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(onClick = onCreateService) {
                                    Text("Agregar")
                                }
                                if (services.isNotEmpty()) {
                                    TextButton(onClick = onViewAllServices) {
                                        Text("Ver todos")
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (isServicesLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFFE67822)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cargando servicios...",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else if (services.isEmpty()) {
                            Text(
                                text = "No hay servicios publicados",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // Mostrar los primeros 3 servicios
                            services.take(3).forEach { service ->
                                ServicePreviewCard(
                                    service = service,
                                    onEdit = { onEditService(service.id) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            if (services.size > 3) {
                                Text(
                                    text = "Y ${services.size - 3} servicios más...",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun ServicePreviewCard(
    service: SupabaseService,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = if (service.isActive) Color(0xFFE8F5E8) else Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Bs. ${service.price}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }
            
            AssistChip(
                onClick = { },
                label = { 
                    Text(
                        if (service.isActive) "Activo" else "Inactivo",
                        fontSize = 10.sp,
                        color = if (service.isActive) Color(0xFF4CAF50) else Color.Red
                    ) 
                }
            )
        }
    }
}

