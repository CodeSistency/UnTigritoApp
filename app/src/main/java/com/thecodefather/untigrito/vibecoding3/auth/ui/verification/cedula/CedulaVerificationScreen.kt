package com.example.vibecoding3.auth.ui.verification.cedula

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * CedulaVerificationScreen composable
 * 
 * Pantalla para verificación de cédula de identidad.
 * Permite ingreso de número de cédula y upload de imagen.
 * 
 * @param onNavigateToPhone Callback cuando se completa la verificación de cédula
 * @param onNavigateBack Callback para navegar atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CedulaVerificationScreen(
    onNavigateToPhone: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var cedula by remember { mutableStateOf("") }
    var imageSelected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var cedulaError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Verificación de Cédula", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título del paso
            Text(
                text = "Paso 1: Número de Cédula",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tu número de cédula",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de cédula
            OutlinedTextField(
                value = cedula,
                onValueChange = {
                    cedula = it
                    cedulaError = if (it.length in 7..8 && it.all { c -> c.isDigit() }) {
                        null
                    } else if (it.isNotEmpty()) {
                        "Cédula debe tener 7-8 dígitos"
                    } else {
                        null
                    }
                },
                label = { Text("Número de Cédula") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cedulaError != null,
                placeholder = { Text("27483383") }
            )

            if (cedulaError != null) {
                Text(
                    text = cedulaError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Paso 2: Upload de imagen
            Text(
                text = "Paso 2: Foto de tu Cédula",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sube una foto clara de tu cédula",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        width = 2.dp,
                        color = if (imageSelected) Color(0xFFE67822) else Color.LightGray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .background(
                        color = if (imageSelected) Color(0xFFE67822).copy(alpha = 0.1f) else Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { imageSelected = !imageSelected },
                contentAlignment = Alignment.Center
            ) {
                if (imageSelected) {
                    // Preview placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE67822).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFE67822)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Imagen cargada",
                                color = Color(0xFFE67822),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Haz clic para subir imagen",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Formatos: JPG, PNG",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de verificación
            Button(
                onClick = {
                    if (cedula.length in 7..8 && imageSelected && !isLoading) {
                        isLoading = true
                        GlobalScope.launch {
                            delay(1500)
                            isLoading = false
                            onNavigateToPhone()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = cedula.length in 7..8 && imageSelected && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text(
                    text = if (isLoading) "Verificando..." else "Verificar y Continuar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCedulaVerificationScreen() {
    MaterialTheme {
        CedulaVerificationScreen(
            onNavigateToPhone = {},
            onNavigateBack = {}
        )
    }
}

