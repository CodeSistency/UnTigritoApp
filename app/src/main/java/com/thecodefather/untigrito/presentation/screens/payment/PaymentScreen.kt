package com.thecodefather.untigrito.presentation.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thecodefather.untigrito.domain.model.PaymentParams
import com.thecodefather.untigrito.presentation.viewmodel.PaymentViewModel

/**
 * Pantalla unificada de pago
 * Soporta los 3 escenarios: recarga, pago por problema resuelto, pago por servicio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    paymentParams: PaymentParams,
    onPaymentCompleted: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Inicializar pago cuando se reciben los parámetros
    LaunchedEffect(paymentParams) {
        viewModel.initializePayment(paymentParams)
    }
    
    // Navegar cuando el pago se complete
    LaunchedEffect(uiState.paymentCompleted) {
        if (uiState.paymentCompleted) {
            onPaymentCompleted()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Realizar pago") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás"
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Mostrar error si existe
            uiState.errorMessage?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Card con detalles del pago
            PaymentDetailsCard(
                amount = uiState.amount,
                concept = uiState.concept
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Datos bancarios del receptor
            uiState.bankDetails?.let { bankDetails ->
                BankDetailsCard(
                    bankDetails = bankDetails,
                    onCopy = { text -> viewModel.copyToClipboard(text) },
                    onCopyAll = { viewModel.copyAllBankDetails() }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card informativa
            InfoCard(message = "Asegúrate de pagar correctamente.")
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Botón de confirmación
            Button(
                onClick = { viewModel.showConfirmationDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading && uiState.bankDetails != null
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Ya pagué")
                }
            }
        }
    }
    
    // Diálogo de confirmación
    if (uiState.showConfirmationDialog) {
        PaymentConfirmationDialog(
            onConfirm = { externalRef ->
                viewModel.confirmPayment(externalRef)
            },
            onDismiss = { viewModel.hideConfirmationDialog() }
        )
    }
}
