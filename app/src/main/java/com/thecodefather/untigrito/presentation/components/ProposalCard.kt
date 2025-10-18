package com.thecodefather.untigrito.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProposalCard(
    proposal: Proposal,
    onProposalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onProposalClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con trabajo y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = proposal.jobTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Estado
                val statusColor = when (proposal.status) {
                    ProposalStatus.PENDING -> MaterialTheme.colorScheme.primary
                    ProposalStatus.ACCEPTED -> MaterialTheme.colorScheme.tertiary
                    ProposalStatus.REJECTED -> MaterialTheme.colorScheme.error
                    ProposalStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                    ProposalStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    ProposalStatus.WITHDRAWN -> MaterialTheme.colorScheme.onSurfaceVariant
                    ProposalStatus.CANCELLED -> MaterialTheme.colorScheme.onSurfaceVariant
                    ProposalStatus.DISPUTED -> MaterialTheme.colorScheme.error
                }
                
                val statusText = when (proposal.status) {
                    ProposalStatus.PENDING -> "Pendiente"
                    ProposalStatus.ACCEPTED -> "Aceptada"
                    ProposalStatus.REJECTED -> "Rechazada"
                    ProposalStatus.IN_PROGRESS -> "En Progreso"
                    ProposalStatus.COMPLETED -> "Completada"
                    ProposalStatus.WITHDRAWN -> "Retirada"
                    ProposalStatus.CANCELLED -> "Cancelada"
                    ProposalStatus.DISPUTED -> "En Disputa"
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cliente
            Text(
                text = "Cliente: ${proposal.clientName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = proposal.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Precio y duración
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio
                Text(
                    text = "$${String.format("%.2f", proposal.proposedPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Duración
                Text(
                    text = "${proposal.estimatedDuration} días",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Características adicionales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (proposal.includesMaterials) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Incluye materiales",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                if (proposal.offersWarranty) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Garantía",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Respuesta del cliente (si existe)
            proposal.responseMessage?.let { response ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Respuesta del Cliente:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = response,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha de creación
            Text(
                text = "Enviada: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(proposal.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}