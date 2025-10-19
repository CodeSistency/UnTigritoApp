package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseConversation
import com.thecodefather.untigrito.data.datasource.remote.SupabaseConversationParticipant
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseMessage
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.Conversation
import com.thecodefather.untigrito.domain.model.ConversationType
import com.thecodefather.untigrito.domain.model.Message
import com.thecodefather.untigrito.domain.model.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val supabaseRealtime: Realtime,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    // Cargar conversaciones directamente desde Supabase
    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener ID del usuario actual
                val userId = authStateManager.getCurrentUserId()
                
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Consulta directa a Conversation
                val result = supabaseDatabase.getAll<SupabaseConversation>("Conversation")
                
                result.onSuccess { conversations ->
                    val domainConversations = conversations
                        .filter { it.createdById == userId } // Filtrar por usuario actual
                        .map { conversation -> mapSupabaseToConversation(conversation) }
                        .sortedByDescending { it.lastMessageTime }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = domainConversations,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar conversaciones"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Cargar conversaciones del usuario actual usando ConversationParticipant
    fun loadUserConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val userId = authStateManager.getCurrentUserId()
                
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Obtener participantes de conversaciones del usuario
                val participantsResult = supabaseDatabase.findBy<SupabaseConversationParticipant>(
                    "ConversationParticipant",
                    "userId",
                    userId
                )
                
                participantsResult.onSuccess { participants ->
                    val conversationIds = participants.map { it.conversationId }
                    
                    if (conversationIds.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            conversations = emptyList(),
                            errorMessage = null
                        )
                        return@launch
                    }
                    
                    // Obtener conversaciones
                    val conversationsResult = supabaseDatabase.getAll<SupabaseConversation>("Conversation")
                    
                    conversationsResult.onSuccess { allConversations ->
                        val userConversations = allConversations.filter { conversation ->
                            conversationIds.contains(conversation.id)
                        }
                        
                        // Para cada conversación, obtener último mensaje y otro participante
                        val conversationsWithDetails = userConversations.map { conversation ->
                            loadConversationDetails(conversation, userId)
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            conversations = conversationsWithDetails,
                            errorMessage = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar conversaciones"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Obtener detalles de una conversación (último mensaje y otro participante)
    private suspend fun loadConversationDetails(
        conversation: SupabaseConversation,
        currentUserId: String
    ): Conversation {
        // Obtener último mensaje
        val lastMessageResult = supabaseDatabase.findBy<SupabaseMessage>(
            "Message",
            "conversationId",
            conversation.id
        )
        
        val lastMessage = lastMessageResult.getOrNull()?.maxByOrNull { 
            it.createdAt ?: "0" 
        }
        
        // Obtener otro participante
        val participantsResult = supabaseDatabase.findBy<SupabaseConversationParticipant>(
            "ConversationParticipant",
            "conversationId",
            conversation.id
        )
        
        val otherParticipant = participantsResult.getOrNull()?.find { 
            it.userId != currentUserId 
        }
        
        // Obtener información del otro participante
        val otherUser = if (otherParticipant != null) {
            val userResult = supabaseDatabase.getById<SupabaseUser>("User", otherParticipant.userId)
            userResult.getOrNull()
        } else null
        
        // Contar mensajes no leídos
        val unreadCount = if (lastMessage != null) {
            val messagesResult = supabaseDatabase.findBy<SupabaseMessage>(
                "Message",
                "conversationId",
                conversation.id
            )
            messagesResult.getOrNull()?.count { 
                it.senderId != currentUserId && !it.isRead 
            } ?: 0
        } else 0
        
        return Conversation(
            id = conversation.id,
            participantId = otherParticipant?.userId ?: "",
            participantName = otherUser?.name ?: "Usuario",
            participantAvatar = null,
            lastMessage = lastMessage?.text ?: lastMessage?.content,
            lastMessageTime = lastMessage?.createdAt?.let { 
                try {
                    Date(it)
                } catch (e: Exception) {
                    null
                }
            },
            unreadCount = unreadCount,
            conversationType = ConversationType.CLIENT,
            isActive = true
        )
    }

    // Cargar mensajes de una conversación
    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMessages = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.findBy<SupabaseMessage>("Message", "conversationId", conversationId)
                
                result.onSuccess { messages ->
                    val domainMessages = messages
                        .map { message -> mapSupabaseToMessage(message) }
                        .sortedBy { it.timestamp }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoadingMessages = false,
                        messages = domainMessages,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMessages = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMessages = false,
                    errorMessage = e.message
                )
            }
        }
    }

    // Enviar mensaje
    fun sendMessage(conversationId: String, content: String, messageType: MessageType = MessageType.TEXT) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true, errorMessage = null)
            
            try {
                // Obtener ID del usuario actual
                val userId = authStateManager.getCurrentUserId()
                
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                val message = SupabaseMessage(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    senderId = userId,
                    text = content,
                    content = content,
                    messageType = messageType.name,
                    isRead = false,
                    createdAt = System.currentTimeMillis().toString()
                )
                
                val result = supabaseDatabase.insert("Message", message)
                
                result.onSuccess { insertedMessage ->
                    if (insertedMessage != null) {
                        _uiState.value = _uiState.value.copy(
                            isSendingMessage = false,
                            messageSent = true,
                            errorMessage = null
                        )
                        // Recargar mensajes
                        loadMessages(conversationId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSendingMessage = false,
                            errorMessage = "Error al enviar mensaje"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        errorMessage = exception.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSendingMessage = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun markAsRead(conversationId: String) {
        viewModelScope.launch {
            try {
                // Obtener ID del usuario actual
                val userId = authStateManager.getCurrentUserId()
                
                if (userId == null) {
                    return@launch
                }
                
                // Obtener mensajes de la conversación
                val result = supabaseDatabase.findBy<SupabaseMessage>("Message", "conversationId", conversationId)
                
                result.onSuccess { messages ->
                    // Marcar como leídos los mensajes que no son del usuario actual
                    messages
                        .filter { it.senderId != userId && !it.isRead }
                        .forEach { message ->
                            val updatedMessage = message.copy(isRead = true)
                            supabaseDatabase.update("Message", message.id, updatedMessage)
                        }
                    
                    _uiState.value = _uiState.value.copy(messagesMarkedAsRead = true)
                    // Recargar conversaciones para actualizar contadores
                    loadConversations()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al marcar como leído"
                )
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                // Obtener ID del usuario actual
                val userId = authStateManager.getCurrentUserId()
                
                if (userId == null) {
                    return@launch
                }
                
                val result = supabaseDatabase.getAll<SupabaseMessage>("Message")
                
                result.onSuccess { messages ->
                    val unreadCount = messages.count { 
                        it.senderId != userId && !it.isRead 
                    }
                    
                    _uiState.value = _uiState.value.copy(unreadCount = unreadCount)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }
    
    // Suscribirse a actualizaciones en tiempo real
    fun subscribeToRealtimeUpdates() {
        viewModelScope.launch {
            try {
                // TODO: Implementar suscripción a cambios en tiempo real con Realtime
                // Esto requeriría configuración adicional del canal de Realtime
                // Por ahora solo recargamos periódicamente
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }
    
    // Mapper helpers
    private fun mapSupabaseToConversation(conversation: SupabaseConversation): Conversation {
        return Conversation(
            id = conversation.id,
            participantId = "", // Se obtiene por separado si es necesario
            participantName = conversation.title ?: "Conversación",
            participantAvatar = null,
            lastMessage = null,
            lastMessageTime = null,
            unreadCount = 0,
            conversationType = ConversationType.CLIENT,
            isActive = true
        )
    }
    
    private fun mapSupabaseToMessage(message: SupabaseMessage): Message {
        return Message(
            id = message.id,
            conversationId = message.conversationId,
            senderId = message.senderId,
            senderName = "", // Se obtiene por separado si es necesario
            senderAvatar = null,
            content = message.text ?: message.content ?: "",
            timestamp = try {
                Date(message.createdAt ?: System.currentTimeMillis().toString())
            } catch (e: Exception) {
                Date()
            },
            isRead = message.isRead,
            messageType = when (message.messageType) {
                "TEXT" -> MessageType.TEXT
                "IMAGE" -> MessageType.IMAGE
                "FILE" -> MessageType.FILE
                "SYSTEM" -> MessageType.SYSTEM
                else -> MessageType.TEXT
            },
            attachments = emptyList()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class MessagesUiState(
    val isLoading: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val isSendingMessage: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val errorMessage: String? = null,
    val messageSent: Boolean = false,
    val messagesMarkedAsRead: Boolean = false
)
