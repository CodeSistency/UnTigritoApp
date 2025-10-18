package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.presentation.viewmodel.CreateRequestViewModel

/**
 * Create Request Screen
 * Form to create new service requests
 */
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

    val categories = listOf("PLOMERIA", "ELECTRICIDAD", "ALBANILERIA", "LIMPIEZA", "MUDANZA")

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Crear Nueva Solicitud",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Title Field
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.updateTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Título del servicio") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
            }

            // Description Field
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    label = { Text("Descripción detallada") },
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
            }

            // Category Dropdown
            item {
                OutlinedTextField(
                    value = category,
                    onValueChange = { viewModel.updateCategory(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoría") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
            }

            // Budget Field
            item {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { viewModel.updateBudget(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Presupuesto") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
            }

            // Error Message
            if (error != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = error ?: "",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = { viewModel.submitRequest("current_user_id") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE67822)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !loading
                ) {
                    Text(
                        text = if (loading) "Enviando..." else "Crear Solicitud",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Success Message
            if (success) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Green.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "¡Solicitud creada exitosamente!",
                            fontSize = 12.sp,
                            color = Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
