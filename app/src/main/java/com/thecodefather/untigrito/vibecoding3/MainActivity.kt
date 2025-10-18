package com.example.vibecoding3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.vibecoding3.auth.ui.login.AuthViewModel
import com.example.vibecoding3.auth.ui.verification.VerificationViewModel
import com.example.vibecoding3.navigation.VibecodingNavHost
import com.example.vibecoding3.ui.theme.Vibecoding3Theme
import com.example.vibecoding3.home.ui.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.vibecoding3.navigation.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vibecoding3Theme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val verificationViewModel: VerificationViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    
    VibecodingNavHost(
        navController = navController,
        authViewModel = authViewModel,
        verificationViewModel = verificationViewModel,
        homeViewModel = homeViewModel
    )
}