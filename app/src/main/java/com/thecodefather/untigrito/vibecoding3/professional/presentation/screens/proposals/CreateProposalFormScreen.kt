package com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.CreateProposalEvent
import com.thecodefather.untigrito.vibecoding3.professional.presentation.viewmodel.CreateProposalViewModel

/**
 * Pantalla para crear una nueva propuesta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProposalScreen(
    jobId: String = "",
    viewModel: CreateProposalViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onProposalCreated: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Inicializar con jobId
    LaunchedEffect(jobId) {
        viewModel.initWithJobId(jobId)
    }
    
    // Observar eventos
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is CreateProposalEvent.ProposalCreated -> {
                    onProposalCreated(event.proposalId)
                }
                is CreateProposalEvent.ShowError -> {
                    // Mostrar error
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Propuesta", color = Color.White) },
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFAFAFA))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Título de sección
            item {
                Text(
                    text = "Detalles de tu Propuesta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }
            
            // Campo de monto
            item {
                Column {
                    Text(
                        text = "Monto Ofertado *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = if (uiState.amount == 0.0) "" else uiState.amount.toString(),
                        onValueChange = { value ->
                            val amount = value.toDoubleOrNull() ?: 0.0
                            viewModel.updateAmount(amount)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        ),
                        singleLine = true
                    )
                }
            }
            
            // Campo de descripción
            item {
                Column {
                    Text(
                        text = "Descripción de tu Trabajo *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 150.dp),
                        placeholder = { Text("Describe tu propuesta y cómo harás el trabajo...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )
                }
            }
            
            // Checkbox: Incluye materiales
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Incluye materiales",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    
                    Checkbox(
                        checked = uiState.includesMaterials,
                        onCheckedChange = { viewModel.toggleIncludesMaterials() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }
            
            // Checkbox: Ofrece garantía
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ofrece garantía",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    
                    Checkbox(
                        checked = uiState.offerWarranty,
                        onCheckedChange = { viewModel.toggleOfferWarranty() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }
            
            // Campo de descripción de garantía (si está habilitado)
            if (uiState.offerWarranty) {
                item {
                    Column {
                        Text(
                            text = "Detalles de la Garantía",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = uiState.warrantyDescription,
                            onValueChange = { viewModel.updateWarrantyDescription(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp, max = 120.dp),
                            placeholder = { Text("Ej: 1 año de garantía en piezas...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )
                    }
                }
            }
            
            // Campos de términos y condiciones
            item {
                Column {
                    Text(
                        text = "Términos y Condiciones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = uiState.terms,
                        onValueChange = { viewModel.updateTerms(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp, max = 120.dp),
                        placeholder = { Text("Ej: Depósito del 50% para comenzar...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )
                }
            }
            
            // Mensaje de error
            if (uiState.errorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            
            // Botón de envío
            item {
                Button(
                    onClick = { viewModel.submitProposal() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color(0xFFBBBBBB)
                    ),
                    enabled = !uiState.isSubmitting,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Enviar Propuesta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
