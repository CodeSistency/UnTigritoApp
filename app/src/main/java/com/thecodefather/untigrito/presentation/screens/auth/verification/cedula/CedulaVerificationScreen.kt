package com.thecodefather.untigrito.presentation.screens.auth.verification.cedula

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun CedulaVerificationScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val context = LocalContext.current

    var cedula by remember { mutableStateOf("") }
    var cedulaImageUri by remember { mutableStateOf<Uri?>(null) }
    var cedulaImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var faceImageUri by remember { mutableStateOf<Uri?>(null) }
    var faceImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val authState by viewModel.authState.collectAsState()
    val cedulaError by viewModel.cedulaError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image pickers
    val cedulaImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            cedulaImageUri = it
            // Convert URI to Bitmap (simplified - in real app you'd handle this properly)
            context.contentResolver.openInputStream(it)?.use { stream ->
                cedulaImageBitmap = BitmapFactory.decodeStream(stream)
            }
        }
    }

    val faceImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            faceImageUri = it
            // Convert URI to Bitmap
            context.contentResolver.openInputStream(it)?.use { stream ->
                faceImageBitmap = BitmapFactory.decodeStream(stream)
            }
        }
    }

    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.IdVerified -> {
                snackbarHostState.showSnackbar("ID verified successfully!")
                delay(1000)
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
                title = { Text("Verify Identity", color = Color.Black) },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Header icon
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "ID verification",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE67822)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Verify Your Identity",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "We need to verify your identity to ensure account security. Please provide your ID information.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cedula input
            OutlinedTextField(
                value = cedula,
                onValueChange = {
                    cedula = it.filter { char -> char.isDigit() }
                    viewModel.validateCedula(cedula)
                },
                label = { Text("Cédula Number") },
                placeholder = { Text("12345678") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cedulaError != null,
                supportingText = {
                    cedulaError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cedula photo section
            Text(
                text = "Photo of your Cédula",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clickable { cedulaImagePicker.launch("image/*") },
                border = BorderStroke(
                    width = 2.dp,
                    color = if (cedulaImageBitmap != null) Color(0xFFE67822) else Color.LightGray
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (cedulaImageBitmap != null) {
                        Image(
                            bitmap = cedulaImageBitmap!!.asImageBitmap(),
                            contentDescription = "Cedula photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "Add photo",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to add photo of your Cédula",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Face photo section
            Text(
                text = "Selfie Photo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Take a clear photo of your face for verification",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { faceImagePicker.launch("image/*") },
                shape = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = if (faceImageBitmap != null) Color(0xFFE67822) else Color.LightGray
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (faceImageBitmap != null) {
                        Image(
                            bitmap = faceImageBitmap!!.asImageBitmap(),
                            contentDescription = "Face photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Take selfie",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📋 Requirements:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Cédula must be valid (7-8 digits)\n• Photo must clearly show the entire ID\n• Selfie must show your face clearly\n• Good lighting and no filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val cedulaImageBase64 = cedulaImageBitmap?.let { bitmapToBase64(it) } ?: ""
                    val faceImageBase64 = faceImageBitmap?.let { bitmapToBase64(it) } ?: ""
                    viewModel.verifyId(cedula, cedulaImageBase64, faceImageBase64)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = authState != AuthState.Loading &&
                         cedula.length in 7..8 &&
                         cedulaImageBitmap != null &&
                         faceImageBitmap != null
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Submit for Verification", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "* Your information is encrypted and secure",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Helper function to convert Bitmap to Base64
private fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
}

@Preview(showBackground = true)
@Composable
fun PreviewCedulaVerificationScreen() {
    MaterialTheme {
        CedulaVerificationScreen(
            onNavigateBack = {},
            onVerificationSuccess = {}
        )
    }
}
