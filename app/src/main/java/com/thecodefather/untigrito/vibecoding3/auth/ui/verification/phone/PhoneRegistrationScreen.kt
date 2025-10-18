package com.example.vibecoding3.auth.ui.verification.phone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel
import com.example.vibecoding3.core.validation.PhoneValidator

/**
 * Pantalla de registro de teléfono
 */
@Composable
fun PhoneRegistrationScreen(
    viewModel: VerificationViewModel,
    onNavigateToValidation: () -> Unit
) {
    val verificationData by viewModel.verificationData.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Ingresa tu Teléfono",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // País
        Text(
            text = "País: Venezuela",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )
        
        // Indicativo
        Text(
            text = "Indicativo: +58",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // Phone field
        OutlinedTextField(
            value = verificationData.phoneNumber,
            onValueChange = { viewModel.updatePhone(it) },
            label = { Text("Teléfono (04120386216)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            isError = phoneError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            prefix = { Text("+58 ") }
        )
        if (phoneError != null) {
            Text(
                text = phoneError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )
        } else {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Información
        Text(
            text = "Ingresa tu número sin el indicativo (+58). Ejemplo: 4120386216",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 32.dp)
        )
        
        // Continue button
        Button(
            onClick = { 
                viewModel.verifyPhone()
                onNavigateToValidation()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneError == null && verificationData.phoneNumber.isNotBlank() && !isLoading
        ) {
            Text(if (isLoading) "Verificando..." else "Continuar")
        }
    }
}

