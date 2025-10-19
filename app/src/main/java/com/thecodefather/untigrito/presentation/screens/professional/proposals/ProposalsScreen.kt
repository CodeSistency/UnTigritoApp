package com.thecodefather.untigrito.presentation.screens.professional.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.presentation.components.FilterTabs
import com.thecodefather.untigrito.presentation.components.ProposalCard
import com.thecodefather.untigrito.presentation.components.SearchBar
import com.thecodefather.untigrito.presentation.screens.professional.components.ProfessionalHeader
import com.thecodefather.untigrito.presentation.viewmodel.ProposalsViewModel

@Composable
fun ProposalsScreen(
    onProposalClick: (String) -> Unit,
    viewModel: ProposalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProposals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris claro como en client
    ) {
        // Header profesional
        ProfessionalHeader(
            userName = "María García", // TODO: Obtener del ViewModel
            onMessageClick = { /* TODO: Navegar a mensajes */ },
            onNotificationClick = { /* TODO: Navegar a notificaciones */ }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp) // Padding consistente con client
        ) {
            // Barra de búsqueda con estilo client
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::loadProposal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar propuestas...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFF616161)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFE67822),
                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                )
            )
            // Filtros con chips estilo client
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    ProposalFilter.OPEN to "Abiertas",
                    ProposalFilter.IN_PROGRESS to "En Curso",
                    ProposalFilter.HISTORY to "Historial"
                ).forEach { (filter, label) ->
                    FilterChip(
                        onClick = { viewModel.updateFilter(filter) },
                        label = { Text(label) },
                        selected = uiState.selectedFilter == filter,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE67822),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Lista de propuestas
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE67822)
                        )
                    }
                }
                uiState.proposals.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "No hay propuestas en esta categoría",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.proposals) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onProposalClick = { onProposalClick(proposal.id) }
                            )
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
}
