package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.presentation.viewmodel.CreateRequestViewModel
import kotlinx.coroutines.delay

/**
 * Create Request Screen
 * Enhanced form to create new service requests with improved UX
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    navController: NavController,
    viewModel: CreateRequestViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    
    // Field-specific errors
    val titleError by viewModel.titleError.collectAsState()
    val descriptionError by viewModel.descriptionError.collectAsState()
    val categoryError by viewModel.categoryError.collectAsState()
    val budgetError by viewModel.budgetError.collectAsState()

    val categories = listOf("PLOMERIA", "ELECTRICIDAD", "ALBANILERIA", "LIMPIEZA", "MUDANZA")
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Auto-navigate back on success
    LaunchedEffect(success) {
        if (success) {
            delay(2000) // Show success message for 2 seconds
            viewModel.onSuccessHandled()
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Text(
                    text = "Crear Nueva Solicitud",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Describe el servicio que necesitas",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Title Field
            item {
                InputFieldCard(
                    title = "Título del servicio",
                    icon = Icons.Default.Title,
                    error = titleError,
                    characterCount = title.length,
                    maxCharacters = 100
                ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.updateTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ej: Reparar grifo de la cocina") },
                        placeholder = { Text("Describe brevemente tu solicitud") },
                        isError = titleError != null,
                    shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                            errorContainerColor = Color.Red.copy(alpha = 0.05f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Title, contentDescription = null, tint = Color(0xFFE67822))
                        }
                    )
                }
            }

            // Description Field
            item {
                InputFieldCard(
                    title = "Descripción detallada",
                    icon = Icons.Default.Description,
                    error = descriptionError,
                    characterCount = description.length,
                    maxCharacters = 500
                ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Detalla lo que necesitas") },
                        placeholder = { Text("Incluye detalles específicos, ubicación, urgencia, etc.") },
                    maxLines = 5,
                        isError = descriptionError != null,
                    shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                            errorContainerColor = Color.Red.copy(alpha = 0.05f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFE67822))
                        }
                    )
                }
            }

            // Category Dropdown
            item {
                InputFieldCard(
                    title = "Categoría",
                    icon = Icons.Default.Work,
                    error = categoryError
                ) {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                OutlinedTextField(
                    value = category,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("Selecciona una categoría") },
                            placeholder = { Text("Elige el tipo de servicio") },
                            isError = categoryError != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                errorContainerColor = Color.Red.copy(alpha = 0.05f)
                            ),
                            leadingIcon = {
                                Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFFE67822))
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.forEach { categoryOption ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(categoryOption) },
                                    onClick = {
                                        viewModel.updateCategory(categoryOption)
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Budget Field
            item {
                InputFieldCard(
                    title = "Presupuesto",
                    icon = Icons.Default.Money,
                    error = budgetError
                ) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { viewModel.updateBudget(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Presupuesto en Bolivianos") },
                    placeholder = { Text("Ej: 150.00") },
                    isError = budgetError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.Red.copy(alpha = 0.05f)
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Money, contentDescription = null, tint = Color(0xFFE67822))
                    },
                    trailingIcon = {
                        Text(
                            text = "Bs",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
                }
            }

            // Error Message
            if (error != null) {
                item {
                    ErrorCard(message = error ?: "")
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Clear Button
                    Button(
                        onClick = { viewModel.clearForm() },
                        modifier = Modifier.weight(1f),
                        enabled = !loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Limpiar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    // Submit Button
                Button(
                    onClick = { viewModel.submitRequest() },
                        modifier = Modifier.weight(2f),
                        enabled = isFormValid && !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE67822)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (loading) "Enviando..." else "Crear Solicitud",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    }
                }
            }

            // Success Message
            if (success) {
                item {
                    SuccessCard(
                        message = "¡Solicitud creada exitosamente!",
                        subMessage = "Redirigiendo al inicio..."
                    )
                }
            }
        }
    }
}

@Composable
private fun InputFieldCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    characterCount: Int = 0,
    maxCharacters: Int = 0,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE67822),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (maxCharacters > 0) {
                    Spacer(modifier = Modifier.weight(1f))
                        Text(
                        text = "$characterCount/$maxCharacters",
                            fontSize = 12.sp,
                        color = if (characterCount > maxCharacters) Color.Red else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SuccessCard(message: String, subMessage: String = "") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
            if (subMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subMessage,
                    fontSize = 12.sp,
                    color = Color.Green.copy(alpha = 0.8f)
                )
            }
        }
    }
}