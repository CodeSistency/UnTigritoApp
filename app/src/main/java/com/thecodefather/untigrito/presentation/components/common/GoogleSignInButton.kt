package com.thecodefather.untigrito.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * GoogleSignInButton composable
 * 
 * Botón personalizado para autenticación con Google siguiendo Material Design 3.
 * 
 * @param onClick Callback cuando se hace clic en el botón
 * @param modifier Modifier para aplicar al botón
 * @param isLoading Indica si la autenticación está en progreso
 * @param text Texto a mostrar en el botón
 */
@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    text: String = "Iniciar sesión con Google"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.LightGray),
        contentPadding = PaddingValues(vertical = 12.dp),
        enabled = !isLoading
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.DarkGray
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Logo de Google",
                    modifier = Modifier.size(24.dp),
                    tint = Color.DarkGray
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (isLoading) "Conectando..." else text,
                color = Color.DarkGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
