package com.thecodefather.untigrito.presentation.screens.professional.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thecodefather.untigrito.data.datasource.remote.ChatbotMessage
import com.thecodefather.untigrito.presentation.components.ChatBubble
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: com.thecodefather.untigrito.presentation.viewmodel.SupportChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Soporte UnTigrito",
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
                    IconButton(
                        onClick = { viewModel.resetConversation() }
                    ) {
                        Icon(
                            Icons.Default.Refresh, 
                            contentDescription = "Reiniciar chat"
                        )
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
            // Lista de mensajes
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.messages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "¡Hola! Soy tu asistente virtual",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "¿En qué puedo ayudarte hoy?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.messages) { message ->
                            SupportChatBubble(
                                message = message,
                                isFromCurrentUser = message.senderType == "USER"
                            )
                        }
                    }
                }
            }

            // Input de mensaje
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { 
                            Text(
                                if (uiState.isEscalated) 
                                    "Conversación transferida a agente humano" 
                                else 
                                    "Escribe tu mensaje..."
                            )
                        },
                        maxLines = 3,
                        singleLine = false,
                        enabled = !uiState.isEscalated && !uiState.isSendingMessage
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && !uiState.isEscalated) {
                                viewModel.sendMessage(messageText.trim())
                                messageText = ""
                            }
                        },
                        enabled = !uiState.isSendingMessage && messageText.isNotBlank() && !uiState.isEscalated
                    ) {
                        if (uiState.isSendingMessage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Enviar mensaje"
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

/**
 * Componente personalizado para mostrar mensajes del chatbot
 */
@Composable
fun SupportChatBubble(
    message: ChatbotMessage,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    when (message.senderType) {
        "SYSTEM" -> {
            // Mensaje del sistema (centrado, estilo diferente)
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        else -> {
            // Usar ChatBubble existente para mensajes normales
            ChatBubble(
                message = com.thecodefather.untigrito.domain.model.Message(
                    id = message.id,
                    conversationId = "",
                    senderId = message.senderId,
                    senderName = message.senderName,
                    senderAvatar = null,
                    content = message.content,
                    timestamp = java.util.Date(),
                    isRead = true,
                    messageType = com.thecodefather.untigrito.domain.model.MessageType.valueOf(message.messageType),
                    attachments = emptyList()
                ),
                isFromCurrentUser = isFromCurrentUser,
                modifier = modifier
            )
        }
    }
}
