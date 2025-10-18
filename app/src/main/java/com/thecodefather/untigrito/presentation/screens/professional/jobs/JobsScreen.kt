package com.thecodefather.untigrito.presentation.screens.professional.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.JobFilter
import com.thecodefather.untigrito.presentation.components.JobCard
import com.thecodefather.untigrito.presentation.components.SearchBar
import com.thecodefather.untigrito.presentation.components.FilterTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    onJobClick: (String) -> Unit,
    onNavigateToProposal: (String) -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.JobsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Trabajos Disponibles",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de búsqueda
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Filtros
        FilterTabs(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = viewModel::updateFilter,
            filters = listOf(
                JobFilter.RECENT to "Recientes",
                JobFilter.RECOMMENDED to "Recomendados",
                JobFilter.FAVORITES to "Favoritos"
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de trabajos
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.jobs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay trabajos disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.jobs) { job ->
                        JobCard(
                            job = job,
                            onJobClick = { onJobClick(job.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(job.id) },
                            onProposalClick = { onNavigateToProposal(job.id) }
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
