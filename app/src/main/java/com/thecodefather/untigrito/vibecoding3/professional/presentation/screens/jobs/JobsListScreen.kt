package com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.vibecoding3.professional.presentation.components.AdvancedFilters
import com.thecodefather.untigrito.vibecoding3.professional.presentation.components.JobCard
import com.thecodefather.untigrito.vibecoding3.professional.presentation.components.JobFilterTabs
import com.thecodefather.untigrito.vibecoding3.professional.presentation.components.JobSearchBar
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.JobsEvent
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.JobsViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal de listado de trabajos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsListScreen(
    viewModel: JobsViewModel = hiltViewModel(),
    onJobClick: (String) -> Unit = {},
    onNavigateToProposals: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    
    // Observar eventos
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is JobsEvent.NavigateToJobDetail -> onJobClick(event.jobId)
                is JobsEvent.ShowError -> {
                    // Mostrar error (snackbar, etc.)
                }
                is JobsEvent.FavoriteToggled -> {
                    // Actualizar UI
                }
            }
        }
    }
    
    // Detectar cuando scroll llega al final para paginación
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && 
                    lastVisibleItemIndex >= uiState.jobs.size - 3 &&
                    uiState.hasMorePages &&
                    !uiState.isLoading) {
                    viewModel.loadMoreJobs()
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trabajos Disponibles",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
        ) {
            // Buscador
            JobSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { query ->
                    // Actualizar búsqueda mientras escribes
                },
                onSearch = { query ->
                    viewModel.searchJobs(query)
                }
            )
            
            // Tabs de filtros
            JobFilterTabs(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { filter ->
                    viewModel.filterJobs(filter)
                }
            )
            
            // Filtros avanzados
            AdvancedFilters(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.filterByCategory(category)
                }
            )
            
            // Lista de trabajos
            when {
                uiState.isLoading && uiState.jobs.isEmpty() -> {
                    // Estado de carga inicial
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFAFAFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1976D2)
                        )
                    }
                }
                
                uiState.jobs.isEmpty() && !uiState.isLoading -> {
                    // Sin resultados
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFAFAFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(bottom = 16.dp),
                                tint = Color(0xFFBBBBBB)
                            )
                            Text(
                                text = "No hay trabajos disponibles",
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "Intenta cambiar los filtros",
                                fontSize = 13.sp,
                                color = Color(0xFFAAAAAA),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                else -> {
                    // Lista de trabajos
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.jobs) { job ->
                            JobCard(
                                job = job,
                                onJobClick = { jobId ->
                                    viewModel.onJobClick(jobId)
                                },
                                onFavoriteClick = { jobId, isFavorite ->
                                    viewModel.toggleFavorite(jobId)
                                },
                                isFavorite = uiState.favorites.contains(job.id),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        
                        // Indicador de carga al final
                        if (uiState.isLoading && uiState.jobs.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color(0xFF1976D2)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Mensaje de error
            if (uiState.errorMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Pantalla de detalles de un trabajo
 */
@Composable
fun JobDetailScreen(
    jobId: String = "",
    onBackClick: () -> Unit = {},
    onCreateProposalClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Trabajo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // TODO: Implementar detalles del trabajo
        Box(modifier = Modifier.padding(padding)) {
            Text("Detalles del Trabajo: $jobId")
        }
    }
}

/**
 * Pantalla para crear una propuesta
 */
@Composable
fun CreateProposalScreen(
    jobId: String = "",
    onBackClick: () -> Unit = {},
    onProposalCreated: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Propuesta") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // TODO: Implementar formulario de propuesta
        Box(modifier = Modifier.padding(padding)) {
            Text("Crear Propuesta para: $jobId")
        }
    }
}
