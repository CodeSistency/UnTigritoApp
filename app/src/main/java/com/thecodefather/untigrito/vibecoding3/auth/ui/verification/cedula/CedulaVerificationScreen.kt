package com.example.vibecoding3.auth.ui.verification.cedula

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel

/**
 * Pantalla de verificación de cédula
 */
@Composable
fun CedulaVerificationScreen(
    viewModel: VerificationViewModel,
    onNavigateToPhone: () -> Unit
) {
    var imageSelected by remember { mutableStateOf(false) }
    var faceScanned by remember { mutableStateOf(false) }
    
    val verificationData by viewModel.verificationData.collectAsState()
    val cedulaError by viewModel.cedulaError.collectAsState()
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
            text = "Verificar Cédula",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )
        
        // Cedula field
        OutlinedTextField(
            value = verificationData.cedula,
            onValueChange = { viewModel.updateCedula(it) },
            label = { Text("Número de Cédula") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            isError = cedulaError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (cedulaError != null) {
            Text(
                text = cedulaError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )
        } else {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Paso 1: Subir imagen
        Text(
            text = "Paso 1: Sube la imagen de tu cédula",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageSelected) {
                Text("✓ Imagen seleccionada", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Haz click para subir una imagen", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Button(
            onClick = { imageSelected = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(if (imageSelected) "✓ Imagen Cargada" else "Seleccionar Imagen")
        }
        
        // Paso 2: Escaneo facial
        Text(
            text = "Paso 2: Escanea tu rostro",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (faceScanned) {
                Text("✓ Rostro escaneado", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Haz click para escanear", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Button(
            onClick = { faceScanned = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(if (faceScanned) "✓ Escaneo Completado" else "Escanear Rostro")
        }
        
        // Verify button
        Button(
            onClick = { viewModel.verifyCedula() },
            modifier = Modifier.fillMaxWidth(),
            enabled = imageSelected && faceScanned && !isLoading && verificationData.cedula.isNotBlank()
        ) {
            Text(if (isLoading) "Verificando..." else "Verificar y Continuar")
        }
    }
}

