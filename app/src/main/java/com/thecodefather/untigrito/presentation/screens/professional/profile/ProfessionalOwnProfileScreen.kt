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
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOwnProfileScreen(
    onNavigateBack: () -> Unit,
    onEditService: (String) -> Unit,
    viewModel: ProfessionalViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val professionalProfile by viewModel.professionalProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var isEditing by remember { mutableStateOf(false) }
    
    Scaffold(
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
                actions = {
                    if (!isEditing) {
                        TextButton(onClick = { isEditing = true }) {
                            Text("Editar")
                        }
                    } else {
                        TextButton(onClick = { isEditing = false }) {
                            Text("Cancelar")
                        }
                        TextButton(onClick = { 
                            // TODO: Guardar cambios
                            isEditing = false 
                        }) {
                            Text("Guardar")
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
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto de perfil
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE67822)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto de perfil",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isEditing) {
                            OutlinedTextField(
                                value = currentUser?.name ?: "",
                                onValueChange = { /* TODO: Actualizar nombre */ },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = currentUser?.email ?: "",
                                onValueChange = { /* TODO: Actualizar email */ },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = professionalProfile?.bio ?: "",
                                onValueChange = { /* TODO: Actualizar bio */ },
                                label = { Text("Biografía") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
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
                                value = professionalProfile?.hourlyRate?.toString() ?: "",
                                onValueChange = { /* TODO: Actualizar tarifa */ },
                                label = { Text("Tarifa por hora (Bs.)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = professionalProfile?.certifications ?: "",
                                onValueChange = { /* TODO: Actualizar certificaciones */ },
                                label = { Text("Certificaciones") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = professionalProfile?.responseTime?.toString() ?: "",
                                onValueChange = { /* TODO: Actualizar tiempo de respuesta */ },
                                label = { Text("Tiempo de respuesta (horas)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            InfoRow("Tarifa por hora", "Bs. ${professionalProfile?.hourlyRate ?: 0.0}")
                            InfoRow("Certificaciones", professionalProfile?.certifications ?: "No especificadas")
                            InfoRow("Tiempo de respuesta", "${professionalProfile?.responseTime ?: 0} horas")
                        }
                        
                        // Especialidades
                        if (!professionalProfile?.specialties.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Especialidades",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val specialties = professionalProfile?.specialties?.split(",") ?: emptyList()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                specialties.forEach { specialty ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(specialty.trim()) },
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
                                value = professionalProfile?.bankAccount ?: "",
                                onValueChange = { /* TODO: Actualizar cuenta bancaria */ },
                                label = { Text("Cuenta Bancaria") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = professionalProfile?.taxId ?: "",
                                onValueChange = { /* TODO: Actualizar RIF */ },
                                label = { Text("RIF/Tax ID") },
                                modifier = Modifier.fillMaxWidth()
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
                            
                            TextButton(onClick = { /* TODO: Navegar a crear servicio */ }) {
                                Text("Agregar")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Lista de servicios (placeholder)
                        Text(
                            text = "No hay servicios publicados",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
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

