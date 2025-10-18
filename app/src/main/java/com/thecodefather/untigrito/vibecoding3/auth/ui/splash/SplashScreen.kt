package com.example.vibecoding3.auth.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vibecoding3.R
import kotlinx.coroutines.delay

/**
 * SplashScreen composable
 * 
 * Pantalla de bienvenida que muestra el logo de UnTigrito® durante 2-3 segundos
 * y luego navega automáticamente a la pantalla de Login.
 * 
 * @param onNavigateToLogin Callback cuando debe navegar a la pantalla de Login
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    // Efecto que navega automáticamente después del delay
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 segundos de delay
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo de UnTigrito®",
            modifier = Modifier.size(120.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    MaterialTheme {
        SplashScreen(
            onNavigateToLogin = {}
        )
    }
}
