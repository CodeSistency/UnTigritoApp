package com.thecodefather.untigrito.presentation.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.thecodefather.untigrito.domain.model.PaymentParams

/**
 * Pantalla para seleccionar el monto de recarga
 * Ofrece opciones predefinidas de montos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    navController: NavController,
    currentUserId: String
) {
    val amounts = listOf(50.0, 100.0, 200.0, 500.0)
    var selectedAmount by remember { mutableStateOf<Double?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Recargar saldo") 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            // Título
            Text(
                text = "Selecciona el monto a recargar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Elige una de las opciones disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Opciones de monto
            amounts.forEach { amount ->
                RechargeOptionCard(
                    amount = amount,
                    isSelected = selectedAmount == amount,
                    onClick = { selectedAmount = amount }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Botón continuar
            Button(
                onClick = {
                    selectedAmount?.let { amount ->
                        val params = PaymentParams.Recharge(
                            clientId = currentUserId,
                            amount = amount
                        )
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("paymentParams", params)
                        navController.navigate("payment/recharge")
                    }
                },
                enabled = selectedAmount != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Continuar")
            }
        }
    }
}

/**
 * Card para cada opción de recarga
 */
@Composable
private fun RechargeOptionCard(
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp, 
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bs. ${String.format("%.0f", amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = when (amount) {
                        50.0 -> "Recarga básica"
                        100.0 -> "Recarga estándar"
                        200.0 -> "Recarga premium"
                        500.0 -> "Recarga máxima"
                        else -> "Recarga personalizada"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (isSelected) {
                RadioButton(
                    selected = true,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}