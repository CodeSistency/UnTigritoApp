package com.example.vibecoding3.auth.ui.verification.validatephone

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel

/**
 * Pantalla de validación de teléfono con OTP
 */
@Composable
fun PhoneValidationScreen(
    viewModel: VerificationViewModel,
    onNavigateToHome: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    
    val verificationData by viewModel.verificationData.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val verificationSuccess by viewModel.verificationSuccess.collectAsState()
    
    if (verificationSuccess) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            onNavigateToHome()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Verifica tu Teléfono",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // Número a verificar
        Text(
            text = "Número a verificar: +58 ${verificationData.phoneNumber.take(3)} ${verificationData.phoneNumber.substring(3, 6)}-${verificationData.phoneNumber.substring(6)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // Descripción
        Text(
            text = "Ingresa el código de 5 dígitos que recibiste",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // OTP fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (index < otp.length) otp[index].toString() else "",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // OTP input field (invisible, for typing)
        OutlinedTextField(
            value = otp,
            onValueChange = { newValue ->
                if (newValue.length <= 5 && newValue.all { it.isDigit() }) {
                    otp = newValue
                    if (newValue.length == 5) {
                        viewModel.updateOTP(newValue)
                    }
                }
            },
            label = { Text("Código OTP") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            isError = otpError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (otpError != null) {
            Text(
                text = otpError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )
        } else {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(24.dp))
        }
        
        // Verify button
        Button(
            onClick = { viewModel.validateOTP(otp) },
            modifier = Modifier.fillMaxWidth(),
            enabled = otp.length == 5 && !isLoading
        ) {
            Text(if (isLoading) "Verificando..." else "Verificar Código")
        }
        
        // Resend link
        TextButton(
            onClick = { /* Simular reenvío */ }
        ) {
            Text(
                text = "¿No recibiste el código? Reenviar",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

