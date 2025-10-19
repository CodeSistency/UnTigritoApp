package com.thecodefather.untigrito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.presentation.navigation.AppNavigation
import com.thecodefather.untigrito.ui.theme.UnTigritoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 *
 * Entry point of the application. Sets up the Compose UI and theme.
 * Uses Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {

            UnTigritoTheme {
                AppNavigation()
            }
        }
    }
}