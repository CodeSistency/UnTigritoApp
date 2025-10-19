package com.thecodefather.untigrito.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import timber.log.Timber

/**
 * Componente para seleccionar y mostrar imágenes de perfil
 */
@Composable
fun ImagePicker(
    currentImageUrl: String? = null,
    isUploading: Boolean = false,
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            onImageSelected(it)
            Timber.d("📷 IMAGE PICKER - Image selected: $it")
        }
    }
    
    // Función para abrir selector de imagen
    val openImagePicker = {
        imagePickerLauncher.launch("image/*")
    }
    
    // Función para mostrar diálogo de confirmación de eliminación
    val showRemoveConfirmation = {
        showRemoveDialog = true
    }
    
    // Diálogo de confirmación para eliminar imagen
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Eliminar imagen") },
            text = { Text("¿Estás seguro de que quieres eliminar la imagen de perfil?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog = false
                        selectedImageUri = null
                        onImageRemoved?.invoke()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (isUploading) Color(0xFFE67822) else Color.Gray,
                shape = CircleShape
            )
            .clickable(enabled = !isUploading) { openImagePicker() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isUploading -> {
                // Estado de carga
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFE67822),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Subiendo...",
                        fontSize = 12.sp,
                        color = Color(0xFFE67822),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            selectedImageUri != null -> {
                // Imagen seleccionada localmente
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                // Botón para eliminar imagen
                if (onImageRemoved != null) {
                    IconButton(
                        onClick = showRemoveConfirmation,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(
                                Color.Red.copy(alpha = 0.8f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            currentImageUrl != null -> {
                // Imagen existente desde URL
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                // Botón para eliminar imagen
                if (onImageRemoved != null) {
                    IconButton(
                        onClick = showRemoveConfirmation,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(
                                Color.Red.copy(alpha = 0.8f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            else -> {
                // Estado por defecto - sin imagen
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Agregar foto",
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Agregar foto",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Overlay para indicar que es clickeable
        if (!isUploading && selectedImageUri == null && currentImageUrl == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.1f),
                        CircleShape
                    )
            )
        }
    }
}

/**
 * Componente simplificado para mostrar imagen de perfil sin funcionalidad de edición
 */
@Composable
fun ProfileImageDisplay(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFE67822)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                tint = Color.White,
                modifier = Modifier.size((size * 0.5).dp)
            )
        }
    }
}
