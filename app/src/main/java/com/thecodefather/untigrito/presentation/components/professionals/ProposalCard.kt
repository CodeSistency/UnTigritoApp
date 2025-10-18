package com.thecodefather.untigrito.presentation.components.professionals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalStatus

/**
 * Componente de tarjeta para mostrar una propuesta
 */
@Composable
fun ProposalCard(
    proposal: Proposal,
    onProposalClick: (String) -> Unit = {},
    onCancelClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onProposalClick(proposal.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado con estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Monto destacado
                Column {
                    Text(
                        text = "Monto Ofertado",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("$%.2f", proposal.proposedPrice),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
                
                // Estado con icono
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getStatusBackgroundColor(proposal.status)),
                    color = getStatusBackgroundColor(proposal.status)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = getStatusIcon(proposal.status),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = getStatusTextColor(proposal.status)
                        )
                        Text(
                            text = getStatusLabel(proposal.status),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = getStatusTextColor(proposal.status)
                        )
                    }
                }
            }
            
            // Descripción
            Text(
                text = proposal.description,
                fontSize = 13.sp,
                color = Color(0xFF666666),
                maxLines = 2,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Divider
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // Info del trabajo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Trabajo",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "ID: ${proposal.jobId.take(8)}...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1976D2)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Fecha",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = formatDate(proposal.createdAt.toString()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }
            }
            
            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onProposalClick(proposal.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Ver Detalles",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                
                if (proposal.status == ProposalStatus.PENDING) {
                    IconButton(
                        onClick = { onCancelClick(proposal.id) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color(0xFFFFEBEE),
                                RoundedCornerShape(6.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cancelar",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Funciones auxiliares para el estado
 */
private fun getStatusBackgroundColor(status: ProposalStatus): Color = when (status) {
    ProposalStatus.PENDING -> Color(0xFFFFF3E0)
    ProposalStatus.ACCEPTED -> Color(0xFFE8F5E9)
    ProposalStatus.REJECTED -> Color(0xFFFFEBEE)
    ProposalStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
    ProposalStatus.COMPLETED -> Color(0xFFE8F5E9)
    ProposalStatus.WITHDRAWN -> Color(0xFFF5F5F5)
    ProposalStatus.CANCELLED -> Color(0xFFF5F5F5)
    ProposalStatus.DISPUTED -> Color(0xFFFCE4EC)
}

private fun getStatusTextColor(status: ProposalStatus): Color = when (status) {
    ProposalStatus.PENDING -> Color(0xFFE65100)
    ProposalStatus.ACCEPTED -> Color(0xFF2E7D32)
    ProposalStatus.REJECTED -> Color(0xFFC62828)
    ProposalStatus.IN_PROGRESS -> Color(0xFF1565C0)
    ProposalStatus.COMPLETED -> Color(0xFF2E7D32)
    ProposalStatus.WITHDRAWN -> Color(0xFF616161)
    ProposalStatus.CANCELLED -> Color(0xFF616161)
    ProposalStatus.DISPUTED -> Color(0xFFC2185B)
}

private fun getStatusIcon(status: ProposalStatus) = when (status) {
    ProposalStatus.ACCEPTED, ProposalStatus.COMPLETED -> Icons.Filled.CheckCircle
    ProposalStatus.PENDING, ProposalStatus.IN_PROGRESS -> Icons.Filled.Schedule
    else -> Icons.Filled.Close
}

private fun getStatusLabel(status: ProposalStatus): String = when (status) {
    ProposalStatus.PENDING -> "Pendiente"
    ProposalStatus.ACCEPTED -> "Aceptada"
    ProposalStatus.REJECTED -> "Rechazada"
    ProposalStatus.IN_PROGRESS -> "En Curso"
    ProposalStatus.COMPLETED -> "Completada"
    ProposalStatus.WITHDRAWN -> "Retirada"
    ProposalStatus.CANCELLED -> "Cancelada"
    ProposalStatus.DISPUTED -> "En Disputa"
}

private fun formatDate(dateString: String): String {
    // Formato simple: solo mostrar fecha
    return dateString.take(10)
}
