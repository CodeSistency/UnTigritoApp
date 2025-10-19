package com.thecodefather.untigrito.presentation.screens.professional.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.domain.model.ConversationType
import com.thecodefather.untigrito.presentation.components.ConversationCard
import com.thecodefather.untigrito.presentation.screens.professional.components.ProfessionalHeader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    onConversationClick: (String) -> Unit,
    onSupportClick: () -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.MessagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        viewModel.loadUnreadCount()
        viewModel.subscribeToRealtimeUpdates()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris claro como en client
    ) {
        // Header profesional
        ProfessionalHeader(
            userName = "María García", // TODO: Obtener del ViewModel
            onMessageClick = { /* TODO: Navegar a mensajes */ },
            onNotificationClick = { /* TODO: Navegar a notificaciones */ }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp) // Padding consistente con client
        ) {
            // Título
            Text(
                text = "Mensajes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botón de soporte
            Card(
                onClick = onSupportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Support,
                    contentDescription = null,
                    tint = Color(0xFFE67822) // Color naranja
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Soporte UnTigrito",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121) // Texto oscuro
                    )
                    Text(
                        text = "¿Necesitas ayuda? Contacta con nuestro equipo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF616161) // Texto gris
                    )
                }
            }
        }

            // Contador de mensajes no leídos
            if (uiState.unreadCount > 0) {
                Surface(
                    color = Color(0xFFE67822), // Color naranja
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${uiState.unreadCount} mensajes no leídos",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de conversaciones
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE67822)
                        )
                    }
                }
                uiState.conversations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFF616161)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay conversaciones",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF616161)
                                )
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.conversations) { conversation ->
                            ConversationCard(
                                conversation = conversation,
                                onConversationClick = { onConversationClick(conversation.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Manejo de errores
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar
            viewModel.clearError()
        }
    }
}
