package com.example.vibecoding3.auth.ui.verification.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibecoding3.R

/**
 * VerificationIntroductionScreen composable
 * 
 * Pantalla de introducción a la verificación de identidad.
 * Muestra los beneficios de la verificación y opciones para continuar u omitir.
 * 
 * @param onContinue Callback cuando el usuario selecciona continuar
 * @param onSkip Callback cuando el usuario selecciona omitir
 */
@Composable
fun VerificationIntroductionScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Ilustración (usando logo como placeholder)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Verificación de identidad",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Título
            Text(
                text = "Verifica tu Identidad",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Desbloquea funcionalidades premium verificando tu identidad",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Beneficios
            VerificationBenefitItem(
                icon = Icons.Default.CheckCircle,
                title = "Cédula de Identidad",
                description = "Sube una foto clara de tu cédula"
            )

            Spacer(modifier = Modifier.height(16.dp))

            VerificationBenefitItem(
                icon = Icons.Default.CheckCircle,
                title = "Número de Teléfono",
                description = "Verifica tu número de teléfono"
            )

            Spacer(modifier = Modifier.height(16.dp))

            VerificationBenefitItem(
                icon = Icons.Default.CheckCircle,
                title = "Profesional Verificado",
                description = "Accede a funcionalidades de profesional"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Omitir por ahora",
                    fontSize = 16.sp,
                    color = Color(0xFFE67822)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Componente para mostrar un beneficio de verificación
 */
@Composable
private fun VerificationBenefitItem(
    icon: androidx.compose.material.icons.Icons,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFE67822),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerificationIntroductionScreen() {
    MaterialTheme {
        VerificationIntroductionScreen(
            onContinue = {},
            onSkip = {}
        )
    }
}
