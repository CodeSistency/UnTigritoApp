package com.thecodefather.untigrito.presentation.screens.professionals.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ProposalStatus
import com.thecodefather.untigrito.presentation.viewmodel.ProposalDetailViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla de detalles de propuesta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDetailScreen(
    proposalId: String = "",
    viewModel: ProposalDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Observar eventos
    LaunchedEffect(Unit) {
//        viewModel.eventFlow.collectLatest { event ->
//            when (event) {
//                is ProposalDetailEvent.NavigateToChat -> onChatClick(event.conversationId)
//                is ProposalDetailEvent.ShowError -> {
//                    // Mostrar error
//                }
//            }
//        }
    }
    
    // Cargar detalles
    LaunchedEffect(proposalId) {
        if (proposalId.isNotEmpty()) {
//            viewModel.loadProposalDetails(proposalId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de Propuesta",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFFAFAFA)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2)
                    )
                }
            }
            
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFFAFAFA)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = {
//                                viewModel.loadProposalDetails(proposalId)
                                      },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            
            uiState.proposal != null -> {
                val proposal = uiState.proposal!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFFAFAFA)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta principal
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Monto
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Monto Ofertado",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "000",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                                
                                // Estado
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Estado",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                    Surface(
                                        modifier = Modifier
                                            .background(
                                                getStatusBackgroundColor(proposal.status),
                                                RoundedCornerShape(6.dp)
                                            ),
                                        color = getStatusBackgroundColor(proposal.status)
                                    ) {
                                        Text(
                                            text = getStatusLabel(proposal.status),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = getStatusTextColor(proposal.status),
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Descripción
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Descripción",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = proposal.description,
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    
                    // Información adicional
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (proposal.includesMaterials) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✓",
                                            fontSize = 16.sp,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = "Incluye Materiales",
                                            fontSize = 13.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }
                                }
                                
                                if (true) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✓",
                                            fontSize = 16.sp,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = "Ofrece Garantía",
                                            fontSize = 13.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Botón de chat
                    if (proposal.status == ProposalStatus.ACCEPTED) {
                        item {
                            Button(
                                onClick = { /* TODO: Abrir chat */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Chat,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 8.dp)
                                )
                                Text("Contactar Cliente")
                            }
                        }
                    }
                }
            }
        }
    }
}

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
