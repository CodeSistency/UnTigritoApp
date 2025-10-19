package com.thecodefather.untigrito.presentation.screens.professional.proposals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.presentation.components.FilterTabs
import com.thecodefather.untigrito.presentation.components.ProposalCard
import com.thecodefather.untigrito.presentation.components.SearchBar
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
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            placeholder = "Busca servicios o profesionales...",
            query = uiState.searchQuery,
            onQueryChange = viewModel::loadProposal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        // Filtros
        FilterTabs(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = viewModel::updateFilter,
            filters = listOf(
                ProposalFilter.OPEN to "Abiertas",
                ProposalFilter.IN_PROGRESS to "En Curso",
                ProposalFilter.HISTORY to "Historial"
            ),
            modifier = Modifier.padding(bottom = 5.dp)
        )

        // Lista de propuestas
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.proposals.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay propuestas en esta categoría",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

    // Manejo de errores
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar
            viewModel.clearError()
        }
    }
}
