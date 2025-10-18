package com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.JobDetailEvent
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.JobDetailViewModel

/**
 * Pantalla de detalles completa de un trabajo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String = "",
    viewModel: JobDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCreateProposalClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Cargar detalles cuando se monta la pantalla
    LaunchedEffect(jobId) {
        viewModel.loadJobDetails(jobId)
    }
    
    // Observar eventos
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is JobDetailEvent.NavigateToCreateProposal -> {
                    onCreateProposalClick(event.jobId)
                }
                is JobDetailEvent.ShowError -> {
                    // Mostrar error
                }
                is JobDetailEvent.FavoriteToggled -> {
                    // Actualizar UI
                }
            }
        }
    }
    
    when {
        uiState.isLoading -> {
            // Pantalla de carga
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF1976D2)
                )
            }
        }
        
        uiState.errorMessage != null -> {
            // Pantalla de error
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Error") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Filled.ArrowBack, "Atrás")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 16.dp),
                            tint = Color(0xFFE53935)
                        )
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        
        uiState.job != null -> {
            // Pantalla con detalles
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Detalles del Trabajo") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Filled.ArrowBack, "Atrás")
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    imageVector = if (uiState.isFavorite)
                                        Icons.Filled.Favorite
                                    else
                                        Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (uiState.isFavorite)
                                        Color(0xFFE53935)
                                    else
                                        Color.Gray
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF1976D2),
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    Button(
                        onClick = { viewModel.createProposal() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Generar Propuesta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFFAFAFA"))
                ) {
                    val job = uiState.job!!
                    
                    // Imagen principal
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.LightGray)
                        ) {
                            if (job.image != null) {
                                AsyncImage(
                                    model = job.image,
                                    contentDescription = job.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    
                    // Contenido principal
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Título
                            Text(
                                text = job.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Presupuesto destacado
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9)),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Presupuesto",
                                        fontSize = 13.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = String.format("$%.2f", job.budget),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Información del cliente
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text(
                                text = "Información del Cliente",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Avatar
                                    Surface(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color = Color(0xFFE0E0E0)
                                    ) {
                                        if (job.clientImage != null) {
                                            AsyncImage(
                                                model = job.clientImage,
                                                contentDescription = job.clientName,
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = job.clientName.first().toString(),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = job.clientName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color(0xFFFFC107)
                                            )
                                            Text(
                                                text = String.format("%.1f", job.clientRating),
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Descripción
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text(
                                text = "Descripción",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = job.description,
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                lineHeight = 20.sp
                            )
                        }
                    }
                    
                    // Ubicación
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text(
                                text = "Ubicación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color(0xFF1976D2)
                                    )
                                    Column {
                                        Text(
                                            text = job.location,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1976D2)
                                        )
                                        Text(
                                            text = String.format("%.1f, %.1f", job.latitude, job.longitude),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Requisitos
                    if (job.requirements.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text(
                                    text = "Requisitos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                        
                        items(job.requirements) { requirement ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = Color.White,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF4CAF50)
                                    )
                                    Text(
                                        text = requirement,
                                        fontSize = 13.sp,
                                        color = Color(0xFF333333)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Espacio al final para el botón
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
