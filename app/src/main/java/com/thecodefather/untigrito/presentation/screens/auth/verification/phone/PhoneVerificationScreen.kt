package com.thecodefather.untigrito.presentation.screens.auth.verification.phone

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.auth.domain.model.AuthState
import com.thecodefather.untigrito.presentation.screens.auth.login.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(VerificationStep.PHONE_INPUT) }
    var countdown by remember { mutableStateOf(0) }
    var canResend by remember { mutableStateOf(true) }

    val authState by viewModel.authState.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Countdown timer for resend OTP
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        } else {
            canResend = true
        }
    }

    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.OtpSent -> {
                currentStep = VerificationStep.OTP_INPUT
                countdown = 60 // 60 seconds countdown
                canResend = false
                snackbarHostState.showSnackbar((authState as AuthState.OtpSent).message)
            }
            is AuthState.OtpVerified -> {
                snackbarHostState.showSnackbar("Phone verified successfully!")
                delay(1000) // Show success message briefly
                onVerificationSuccess()
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Verify Phone", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // Header icon
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Phone verification",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE67822)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = when (currentStep) {
                    VerificationStep.PHONE_INPUT -> "Verify Your Phone"
                    VerificationStep.OTP_INPUT -> "Enter Verification Code"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = when (currentStep) {
                    VerificationStep.PHONE_INPUT ->
                        "We'll send a verification code to your phone number to confirm it's yours."
                    VerificationStep.OTP_INPUT ->
                        "We've sent a 5-digit code to ${formatPhoneNumber(phoneNumber)}. Enter it below."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                VerificationStep.PHONE_INPUT -> {
                    // Phone input step
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            viewModel.validatePhone(it)
                        },
                        label = { Text("Phone Number") },
                        placeholder = { Text("04120386216") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError != null,
                        supportingText = {
                            phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        leadingIcon = {
                            Text(
                                text = "+58",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Format: 04120386216 (11 digits, starting with 04)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.sendOtp(phoneNumber)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        enabled = authState != AuthState.Loading && phoneNumber.isNotBlank()
                    ) {
                        if (authState == AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(text = "Send Verification Code", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }

                VerificationStep.OTP_INPUT -> {
                    // OTP input step
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 5) {
                                otpCode = it.filter { char -> char.isDigit() }
                                viewModel.validateOtp(otpCode)
                            }
                        },
                        label = { Text("Verification Code") },
                        placeholder = { Text("12345") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = otpError != null,
                        supportingText = {
                            otpError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown and resend button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (countdown > 0) {
                            Text(
                                text = "Resend code in ${countdown}s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            TextButton(
                                onClick = {
                                    viewModel.sendOtp(phoneNumber)
                                },
                                enabled = canResend && authState != AuthState.Loading
                            ) {
                                Text(
                                    text = "Resend Code",
                                    color = Color(0xFFE67822)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.verifyOtp(phoneNumber, otpCode)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        enabled = authState != AuthState.Loading && otpCode.length == 5
                    ) {
                        if (authState == AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(text = "Verify Code", fontSize = 16.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            currentStep = VerificationStep.PHONE_INPUT
                            otpCode = ""
                            viewModel.clearErrors()
                        }
                    ) {
                        Text(
                            text = "Change Phone Number",
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private enum class VerificationStep {
    PHONE_INPUT,
    OTP_INPUT
}

private fun formatPhoneNumber(phone: String): String {
    return when {
        phone.startsWith("+58") -> phone
        phone.startsWith("58") -> "+$phone"
        else -> "+58$phone"
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPhoneVerificationScreen() {
    MaterialTheme {
        PhoneVerificationScreen(
            onNavigateBack = {},
            onVerificationSuccess = {}
        )
    }
}
