package com.thecodefather.untigrito.presentation.screens.professional.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.presentation.viewmodel.JobDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProposalScreen(
    jobId: String,
    onNavigateBack: () -> Unit,
    onProposalCreated: () -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.CreateProposalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var proposedPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedDuration by remember { mutableStateOf("") }
    var includesMaterials by remember { mutableStateOf(false) }
    var offersWarranty by remember { mutableStateOf(false) }
    var termsAndConditions by remember { mutableStateOf("") }

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
    }
    
    // Navegar cuando se crea la propuesta
    LaunchedEffect(uiState.proposalCreated) {
        if (uiState.proposalCreated) {
            onProposalCreated()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Fondo gris claro
        topBar = {
            TopAppBar(
                title = { Text("Crear Propuesta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5),
                    titleContentColor = Color(0xFF212121)
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE67822)
                    )
                }
            }
            uiState.job == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trabajo no encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                val job = uiState.job!!
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp) // Padding consistente con client
                        .verticalScroll(rememberScrollState())
                ) {
                    // Información del trabajo
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Trabajo: ${job.title}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Presupuesto del cliente: $${String.format("%.2f", job.budget)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Precio propuesto
                    OutlinedTextField(
                        value = proposedPrice,
                        onValueChange = { proposedPrice = it },
                        label = { Text("Precio Propuesto ($)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        supportingText = { Text("Ingresa tu precio propuesto") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE67822),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )

                    // Descripción de la propuesta
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción de tu Propuesta") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = { Text("Explica por qué eres la mejor opción para este trabajo") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE67822),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )

                    // Duración estimada
                    OutlinedTextField(
                        value = estimatedDuration,
                        onValueChange = { estimatedDuration = it },
                        label = { Text("Duración Estimada (días)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        supportingText = { Text("¿Cuántos días necesitas para completar el trabajo?") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE67822),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )

                    // Opciones adicionales
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Opciones Adicionales",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = includesMaterials,
                                    onCheckedChange = { includesMaterials = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Incluye materiales")
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = offersWarranty,
                                    onCheckedChange = { offersWarranty = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ofrece garantía")
                            }
                        }
                    }

                    // Términos y condiciones
                    OutlinedTextField(
                        value = termsAndConditions,
                        onValueChange = { termsAndConditions = it },
                        label = { Text("Términos y Condiciones (Opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        minLines = 2,
                        maxLines = 4,
                        supportingText = { Text("Especifica cualquier término especial de tu propuesta") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE67822),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )

                    // Botón de enviar propuesta
                    Button(
                        onClick = {
                            viewModel.createProposal(
                                jobId = jobId,
                                proposedPrice = proposedPrice.toDoubleOrNull() ?: 0.0,
                                description = description,
                                estimatedDuration = estimatedDuration.toIntOrNull() ?: 0,
                                includesMaterials = includesMaterials,
                                offersWarranty = offersWarranty,
                                termsAndConditions = termsAndConditions.ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !uiState.isLoading && proposedPrice.isNotEmpty() && description.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE67822)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (uiState.isLoading) "Enviando..." else "Enviar Propuesta")
                    }
                }
            }
        }
    }
}
