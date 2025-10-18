package com.thecodefather.untigrito.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * ErrorSnackbar composable
 * 
 * Componente para mostrar mensajes de error estandarizados
 * con diferentes tipos de error (validación, red, servidor).
 * 
 * @param message Mensaje de error a mostrar
 * @param errorType Tipo de error (validation, network, server)
 * @param onDismiss Callback cuando se cierra el snackbar
 * @param modifier Modifier para aplicar al componente
 */
@Composable
fun ErrorSnackbar(
    message: String,
    errorType: ErrorType = ErrorType.VALIDATION,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (errorType) {
        ErrorType.VALIDATION -> Color(0xFFE53935) // Rojo fuerte
        ErrorType.NETWORK -> Color(0xFFF57C00)    // Naranja
        ErrorType.SERVER -> Color(0xFF6A1B9A)     // Púrpura
    }

    Row(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (onDismiss != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Tipos de errores soportados
 */
enum class ErrorType {
    VALIDATION,  // Errores de validación de formularios
    NETWORK,     // Errores de conexión de red
    SERVER       // Errores del servidor
}

@Preview
@Composable
fun PreviewErrorSnackbarValidation() {
    ErrorSnackbar(
        message = "Por favor ingresa un email válido",
        errorType = ErrorType.VALIDATION
    )
}

@Preview
@Composable
fun PreviewErrorSnackbarNetwork() {
    ErrorSnackbar(
        message = "Sin conexión a internet. Intenta nuevamente.",
        errorType = ErrorType.NETWORK,
        onDismiss = {}
    )
}
