package com.example.vibecoding3.auth.ui.verification.phone

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibecoding3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * PhoneRegistrationScreen composable
 * 
 * Pantalla para registro y validación de número de teléfono.
 * Soporta formato venezolano (04120386216).
 * 
 * @param onNavigateToValidation Callback cuando se completa el registro de teléfono
 * @param onNavigateBack Callback para navegar atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneRegistrationScreen(
    onNavigateToValidation: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registrar Teléfono", color = Color.Black) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Teléfono",
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ingresa tu Teléfono",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Usaremos este número para verificar tu identidad",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = if (isValidVenezuelanPhone(it)) {
                        null
                    } else if (it.isNotEmpty()) {
                        "Formato: 04120386216"
                    } else {
                        null
                    }
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError != null,
                placeholder = { Text("04120386216") },
                supportingText = {
                    Text(
                        text = "Formato venezolano",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            )

            if (phoneError != null) {
                Text(
                    text = phoneError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de continuar
            Button(
                onClick = {
                    if (isValidVenezuelanPhone(phone) && !isLoading) {
                        isLoading = true
                        GlobalScope.launch {
                            kotlinx.coroutines.delay(1000)
                            isLoading = false
                            onNavigateToValidation()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = isValidVenezuelanPhone(phone) && !isLoading
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
                    text = if (isLoading) "Enviando Código..." else "Continuar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Valida número de teléfono en formato venezolano
 * Formato: 04120386216 (11 dígitos)
 */
private fun isValidVenezuelanPhone(phone: String): Boolean {
    return phone.matches(Regex("^0412\\d{7}$"))
}

@Preview(showBackground = true)
@Composable
fun PreviewPhoneRegistrationScreen() {
    MaterialTheme {
        PhoneRegistrationScreen(
            onNavigateToValidation = {},
            onNavigateBack = {}
        )
    }
}

