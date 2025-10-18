package com.thecodefather.untigrito.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * LoadingIndicator composable
 * 
 * Componente reutilizable que muestra un indicador de carga centrado
 * con un mensaje opcional, siguiendo Material Design 3.
 * 
 * @param isVisible Controla la visibilidad del indicador
 * @param message Mensaje opcional a mostrar debajo del indicador
 * @param modifier Modifier para aplicar al componente
 */
@Composable
fun LoadingIndicator(
    isVisible: Boolean = true,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFFE67822),
                strokeWidth = 4.dp
            )

            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoadingIndicator() {
    LoadingIndicator(
        isVisible = true,
        message = "Cargando..."
    )
}
