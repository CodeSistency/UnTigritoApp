package com.thecodefather.untigrito.presentation.screens.professionals.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.presentation.components.professionals.ProposalCard
import com.thecodefather.untigrito.presentation.viewmodel.ProposalsListEvent
import com.thecodefather.untigrito.presentation.viewmodel.ProposalsListViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla de listado de propuestas enviadas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsListScreen(
    viewModel: ProposalsListViewModel = hiltViewModel(),
    onProposalClick: (String) -> Unit = {},
    onNavigateToJobs: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    
    // Observar eventos
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProposalsListEvent.NavigateToDetail -> onProposalClick(event.proposalId)
                is ProposalsListEvent.ShowError -> {
                    // Mostrar error
                }
                is ProposalsListEvent.ProposalCancelled -> {
                    // Actualizar UI
                }
            }
        }
    }
    
    // Detectar scroll al final para paginación
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && 
                    lastVisibleItemIndex >= uiState.proposals.size - 3 &&
                    uiState.hasMorePages &&
                    !uiState.isLoading) {
                    viewModel.loadMoreProposals()
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Propuestas",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Abrir filtros avanzados */ }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filtros",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
        ) {
            // Tabs de filtros
            TabRow(
                selectedTabIndex = getFilterTabIndex(uiState.selectedFilter),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                containerColor = Color.White,
                contentColor = Color(0xFF1976D2)
            ) {
                val filters = listOf(
                    ProposalFilter.OPEN to "Abiertas",
                    ProposalFilter.IN_PROGRESS to "En Curso",
                    ProposalFilter.COMPLETED to "Completadas",
                    ProposalFilter.REJECTED to "Rechazadas"
                )
                
                filters.forEachIndexed { index, (filter, label) ->
                    Tab(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.filterProposals(filter) },
                        text = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (uiState.selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Contenido
            when {
                uiState.isLoading && uiState.proposals.isEmpty() -> {
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
                
                uiState.proposals.isEmpty() && !uiState.isLoading -> {
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
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(bottom = 16.dp),
                                tint = Color(0xFFBBBBBB)
                            )
                            Text(
                                text = "Sin propuestas",
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "Envía propuestas para comenzar",
                                fontSize = 13.sp,
                                color = Color(0xFFAAAAAA),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.proposals) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onProposalClick = { proposalId ->
                                    viewModel.onProposalClick(proposalId)
                                },
                                onCancelClick = { proposalId ->
                                    viewModel.cancelProposal(proposalId)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        
                        if (uiState.isLoading && uiState.proposals.isNotEmpty()) {
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
 * Obtener índice del tab según el filtro
 */
private fun getFilterTabIndex(filter: ProposalFilter): Int = when (filter) {
    ProposalFilter.OPEN -> 0
    ProposalFilter.IN_PROGRESS -> 1
    ProposalFilter.COMPLETED -> 2
    ProposalFilter.REJECTED -> 3
    ProposalFilter.HISTORY -> 3
}
