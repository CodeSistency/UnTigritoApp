package com.example.vibecoding3.auth.ui.verification.validatephone

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
 * PhoneValidationScreen composable
 * 
 * Pantalla para validación de teléfono mediante código OTP.
 * Permite ingreso de 6 dígitos con auto-focus entre campos.
 * 
 * @param phoneNumber Número de teléfono formateado a mostrar
 * @param onValidationComplete Callback cuando se verifica el OTP correctamente
 * @param onNavigateBack Callback para navegar atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneValidationScreen(
    phoneNumber: String = "+58 412 0386216",
    onValidationComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var otp by remember { mutableStateOf(arrayOf("", "", "", "", "", "")) }
    var isLoading by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Verificar Teléfono", color = Color.Black) },
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

            Text(
                text = "Verifica tu Teléfono",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa el código enviado a",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Text(
                text = phoneNumber,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFE67822),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campos OTP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    OTPField(
                        value = otp[index],
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                otp[index] = newValue
                                otpError = null
                                // Auto-focus al siguiente campo
                                if (newValue.isNotEmpty() && index < 5) {
                                    // El siguiente campo recibirá el enfoque automáticamente
                                }
                            }
                        },
                        modifier = Modifier.size(50.dp)
                    )
                    
                    if (index < 5) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            if (otpError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = otpError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de verificación
            Button(
                onClick = {
                    val otpValue = otp.joinToString("")
                    if (otpValue.length == 6 && !isLoading) {
                        isLoading = true
                        GlobalScope.launch {
                            delay(1500)
                            isLoading = false
                            onValidationComplete()
                        }
                    } else {
                        otpError = "Ingresa los 6 dígitos del código"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = otp.all { it.isNotEmpty() } && !isLoading
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
                    text = if (isLoading) "Verificando..." else "Verificar Código",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reenviar código
            TextButton(onClick = { /* Reenviar código */ }) {
                Text(
                    text = "¿No recibiste el código? Reenviar",
                    color = Color(0xFFE67822),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Componente para campo individual de OTP
 */
@Composable
private fun OTPField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (value.isNotEmpty()) Color(0xFFE67822) else Color.LightGray,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPhoneValidationScreen() {
    MaterialTheme {
        PhoneValidationScreen(
            phoneNumber = "+58 412 0386216",
            onValidationComplete = {},
            onNavigateBack = {}
        )
    }
}

