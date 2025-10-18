package com.thecodefather.untigrito.presentation.screens.professional.proposals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ProposalStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDetailScreen(
    proposalId: String,
    onNavigateBack: () -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.ProposalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(proposalId) {
        viewModel.loadProposal(proposalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Propuesta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
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
                    CircularProgressIndicator()
                }
            }
            uiState.proposal == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Propuesta no encontrada",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                val proposal = uiState.proposal!!
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Información del trabajo
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Trabajo: ${proposal.jobTitle}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Cliente: ${proposal.clientName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Estado de la propuesta
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            val statusColor = when (proposal.status) {
                                ProposalStatus.PENDING -> MaterialTheme.colorScheme.primary
                                ProposalStatus.ACCEPTED -> MaterialTheme.colorScheme.tertiary
                                ProposalStatus.REJECTED -> MaterialTheme.colorScheme.error
                                ProposalStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                                ProposalStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                                ProposalStatus.WITHDRAWN -> MaterialTheme.colorScheme.onSurfaceVariant
                                ProposalStatus.DISPUTED -> MaterialTheme.colorScheme.primary
                                ProposalStatus.CANCELLED -> MaterialTheme.colorScheme.primary

                            }
                            
                            val statusText = when (proposal.status) {
                                ProposalStatus.PENDING -> "Pendiente"
                                ProposalStatus.ACCEPTED -> "Aceptada"
                                ProposalStatus.REJECTED -> "Rechazada"
                                ProposalStatus.IN_PROGRESS -> "En Progreso"
                                ProposalStatus.COMPLETED -> "Completada"
                                ProposalStatus.WITHDRAWN -> "Retirada"
                                ProposalStatus.DISPUTED -> "Sin estatus"
                                ProposalStatus.CANCELLED -> "Sin estatus"
                            }
                            
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    // Precio propuesto
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Precio Propuesto",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "$${String.format("%.2f", proposal.proposedPrice)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Descripción de la propuesta
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Descripción de tu Propuesta",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = proposal.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Detalles adicionales
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Detalles Adicionales",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Duración estimada: ${proposal.estimatedDuration} días",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (proposal.includesMaterials) {
                                Text(
                                    text = "✓ Incluye materiales",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            if (proposal.offersWarranty) {
                                Text(
                                    text = "✓ Ofrece garantía",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Términos y condiciones
                    if (!proposal.termsAndConditions.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Términos y Condiciones",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = proposal.termsAndConditions,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Respuesta del cliente
                    if (proposal.responseMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Respuesta del Cliente",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = proposal.responseMessage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                proposal.responseDate?.let { date ->
                                    Text(
                                        text = "Fecha: ${dateFormat.format(date)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Fechas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Fechas",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Creada: ${dateFormat.format(proposal.createdAt)}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Actualizada: ${dateFormat.format(proposal.updatedAt)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Botón de retirar propuesta (solo si está pendiente)
                    if (proposal.status == ProposalStatus.PENDING) {
                        OutlinedButton(
                            onClick = { viewModel.withdrawProposal() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Retirar Propuesta")
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

    // Manejo de éxito al retirar propuesta
    if (uiState.showWithdrawSuccess) {
        LaunchedEffect(uiState.showWithdrawSuccess) {
            // Aquí podrías mostrar un Snackbar de éxito
            viewModel.clearWithdrawSuccess()
        }
    }
}
