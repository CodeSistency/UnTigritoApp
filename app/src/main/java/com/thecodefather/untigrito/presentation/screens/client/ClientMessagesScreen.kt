package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.presentation.components.ConversationCard
import com.thecodefather.untigrito.presentation.viewmodel.MessagesViewModel
import com.thecodefather.untigrito.presentation.viewmodel.ConnectionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMessagesScreen(
    onConversationClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSupportClick: () -> Unit,
    navController: NavController,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        viewModel.loadUnreadCount()
        viewModel.subscribeToRealtimeUpdates()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Mensajes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onSupportClick) {
                        Icon(Icons.Default.Support, contentDescription = "Soporte")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Indicador de estado de conexión
            if (uiState.connectionStatus != ConnectionStatus.DISCONNECTED) {
                ConnectionStatusBanner(status = uiState.connectionStatus)
            }
            // Contador de mensajes no leídos
            if (uiState.unreadCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${uiState.unreadCount} mensajes no leídos",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Lista de conversaciones
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.conversations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay conversaciones",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
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

@Composable
fun ConnectionStatusBanner(status: ConnectionStatus) {
    val (color, text) = when (status) {
        ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.primary to "Conectado"
        ConnectionStatus.CONNECTING -> MaterialTheme.colorScheme.secondary to "Conectando..."
        ConnectionStatus.ERROR -> MaterialTheme.colorScheme.error to "Error de conexión"
        ConnectionStatus.DISCONNECTED -> return
    }
    
    Surface(
        color = color,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status == ConnectionStatus.CONNECTING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = when (status) {
                    ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.onPrimary
                    ConnectionStatus.CONNECTING -> MaterialTheme.colorScheme.onSecondary
                    ConnectionStatus.ERROR -> MaterialTheme.colorScheme.onError
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
