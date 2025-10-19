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
// import io.github.jan.supabase.realtime.Realtime
// import io.github.jan.supabase.realtime.RealtimeChannel
// import io.github.jan.supabase.realtime.channel.ChannelEvent
// import io.github.jan.supabase.realtime.channel.ChangeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    // private val supabaseRealtime: Realtime,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    // Almacenamiento en memoria
    private val conversationsCache = mutableMapOf<String, Conversation>()
    private val messagesCache = mutableMapOf<String, MutableList<Message>>()
    private val participantsCache = mutableMapOf<String, SupabaseUser>()

    // Límites de memoria
    private val MAX_CONVERSATIONS = 100
    private val MAX_MESSAGES_PER_CONVERSATION = 500
    private val MAX_TOTAL_MESSAGES = 1000

    // Canal de Realtime
    // private var realtimeChannel: RealtimeChannel? = null
    // private var messagesChannel: RealtimeChannel? = null

    // Cargar conversaciones directamente desde Supabase
    fun loadConversations() {
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
                
                // Retornar cache si existe
                if (conversationsCache.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = conversationsCache.values
                            .sortedByDescending { it.lastMessageTime },
                        errorMessage = null
                    )
                }
                
                // Cargar desde Supabase y actualizar cache
                val result = supabaseDatabase.getAll<SupabaseConversation>("Conversation")
                
                result.onSuccess { conversations ->
                    conversations
                        .filter { it.createdById == userId }
                        .forEach { conv ->
                            val domainConv = loadConversationDetails(conv, userId)
                            conversationsCache[conv.id] = domainConv
                        }
                    
                    cleanupMemoryIfNeeded()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = conversationsCache.values
                            .sortedByDescending { it.lastMessageTime },
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
                // Retornar cache si existe
                messagesCache[conversationId]?.let { cachedMessages ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMessages = false,
                        messages = cachedMessages,
                        errorMessage = null
                    )
                }
                
                // Cargar desde Supabase y actualizar cache
                val result = supabaseDatabase.findBy<SupabaseMessage>(
                    "Message", "conversationId", conversationId
                )
                
                result.onSuccess { messages ->
                    val domainMessages = messages
                        .map { message -> mapSupabaseToMessage(message) }
                        .sortedBy { it.timestamp }
                    
                    messagesCache[conversationId] = domainMessages.toMutableList()
                    cleanupMemoryIfNeeded()
                    
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
                val userId = authStateManager.getCurrentUserId()
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                val tempMessage = Message(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    senderId = userId,
                    senderName = participantsCache[userId]?.name ?: "",
                    senderAvatar = null,
                    content = content,
                    timestamp = Date(),
                    isRead = false,
                    messageType = messageType,
                    attachments = emptyList()
                )
                
                // Actualizar cache inmediatamente (optimistic update)
                messagesCache.getOrPut(conversationId) { mutableListOf() }.add(tempMessage)
                _uiState.value = _uiState.value.copy(
                    messages = messagesCache[conversationId] ?: emptyList()
                )
                
                // Enviar a Supabase
                val supabaseMessage = SupabaseMessage(
                    id = tempMessage.id,
                    conversationId = conversationId,
                    senderId = userId,
                    text = content,
                    content = content,
                    messageType = messageType.name,
                    isRead = false,
                    createdAt = System.currentTimeMillis().toString()
                )
                
                val result = supabaseDatabase.insert("Message", supabaseMessage)
                
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        messageSent = true,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    // Revertir mensaje en caso de error
                    messagesCache[conversationId]?.remove(tempMessage)
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        messages = messagesCache[conversationId] ?: emptyList(),
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
                val userId = authStateManager.getCurrentUserId()
                if (userId == null) return@launch
                
                _uiState.value = _uiState.value.copy(
                    connectionStatus = ConnectionStatus.CONNECTING
                )
                
                // Conectar a Realtime
                // supabaseRealtime.connect()
                
                // Suscribirse a mensajes
                // subscribeToMessages()
                
                // Suscribirse a conversaciones
                subscribeToConversations()
                
                _uiState.value = _uiState.value.copy(
                    isConnected = true,
                    connectionStatus = ConnectionStatus.CONNECTED
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isConnected = false,
                    connectionStatus = ConnectionStatus.ERROR,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    private suspend fun subscribeToMessages() {
        // val userId = authStateManager.getCurrentUserId() ?: return
        // 
        // messagesChannel = supabaseRealtime.channel("messages_$userId")
        // 
        // messagesChannel?.on(
        //     event = ChannelEvent.POSTGRES_CHANGES,
        //     filter = ChangeFilter.OnInsert("Message")
        // ) { payload ->
        //     handleNewMessage(payload)
        // }
        // 
        // messagesChannel?.on(
        //     event = ChannelEvent.POSTGRES_CHANGES,
        //     filter = ChangeFilter.OnUpdate("Message")
        // ) { payload ->
        //     handleUpdatedMessage(payload)
        // }
        // 
        // messagesChannel?.subscribe()
    }

    private suspend fun subscribeToConversations() {
        // val userId = authStateManager.getCurrentUserId() ?: return
        // 
        // realtimeChannel = supabaseRealtime.channel("conversations_$userId")
        // 
        // realtimeChannel?.on(
        //     event = ChannelEvent.POSTGRES_CHANGES,
        //     filter = ChangeFilter.OnInsert("Conversation")
        // ) { payload ->
        //     handleNewConversation(payload)
        // }
        // 
        // realtimeChannel?.on(
        //     event = ChannelEvent.POSTGRES_CHANGES,
        //     filter = ChangeFilter.OnUpdate("Conversation")
        // ) { payload ->
        //     handleUpdatedConversation(payload)
        // }
        // 
        // realtimeChannel?.subscribe()
    }

    private fun handleNewMessage(payload: JsonObject) {
        viewModelScope.launch {
            try {
                val messageData = payload["record"]?.jsonObject ?: return@launch
                val supabaseMessage = Json.decodeFromJsonElement(SupabaseMessage.serializer(), messageData)
                val domainMessage = mapSupabaseToMessage(supabaseMessage)
                
                // Agregar a cache
                val conversationId = domainMessage.conversationId
                messagesCache.getOrPut(conversationId) { mutableListOf() }.add(domainMessage)
                
                // Actualizar conversación
                conversationsCache[conversationId]?.let { conv ->
                    conversationsCache[conversationId] = conv.copy(
                        lastMessage = domainMessage.content,
                        lastMessageTime = domainMessage.timestamp,
                        unreadCount = if (domainMessage.senderId != authStateManager.getCurrentUserId()) {
                            conv.unreadCount + 1
                        } else conv.unreadCount
                    )
                }
                
                cleanupMemoryIfNeeded()
                
                // Actualizar UI
                _uiState.value = _uiState.value.copy(
                    messages = messagesCache[conversationId] ?: emptyList(),
                    conversations = conversationsCache.values
                        .sortedByDescending { it.lastMessageTime }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error procesando mensaje: ${e.message}"
                )
            }
        }
    }

    private fun handleUpdatedMessage(payload: JsonObject) {
        viewModelScope.launch {
            try {
                val messageData = payload["record"]?.jsonObject ?: return@launch
                val supabaseMessage = Json.decodeFromJsonElement(SupabaseMessage.serializer(), messageData)
                val domainMessage = mapSupabaseToMessage(supabaseMessage)
                
                // Actualizar en cache
                messagesCache[domainMessage.conversationId]?.let { messages ->
                    val index = messages.indexOfFirst { it.id == domainMessage.id }
                    if (index >= 0) {
                        messages[index] = domainMessage
                    }
                }
                
                // Actualizar UI si es la conversación actual
                _uiState.value = _uiState.value.copy(
                    messages = messagesCache[domainMessage.conversationId] ?: emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error actualizando mensaje: ${e.message}"
                )
            }
        }
    }

    private fun handleNewConversation(payload: JsonObject) {
        viewModelScope.launch {
            try {
                val convData = payload["record"]?.jsonObject ?: return@launch
                val supabaseConv = Json.decodeFromJsonElement(SupabaseConversation.serializer(), convData)
                val userId = authStateManager.getCurrentUserId() ?: return@launch
                
                val domainConv = loadConversationDetails(supabaseConv, userId)
                conversationsCache[supabaseConv.id] = domainConv
                
                cleanupMemoryIfNeeded()
                
                _uiState.value = _uiState.value.copy(
                    conversations = conversationsCache.values
                        .sortedByDescending { it.lastMessageTime }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error procesando conversación: ${e.message}"
                )
            }
        }
    }

    private fun handleUpdatedConversation(payload: JsonObject) {
        viewModelScope.launch {
            try {
                val convData = payload["record"]?.jsonObject ?: return@launch
                val supabaseConv = Json.decodeFromJsonElement(SupabaseConversation.serializer(), convData)
                val userId = authStateManager.getCurrentUserId() ?: return@launch
                
                val domainConv = loadConversationDetails(supabaseConv, userId)
                conversationsCache[supabaseConv.id] = domainConv
                
                _uiState.value = _uiState.value.copy(
                    conversations = conversationsCache.values
                        .sortedByDescending { it.lastMessageTime }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error actualizando conversación: ${e.message}"
                )
            }
        }
    }
    
    // Función de limpieza de memoria
    private fun cleanupMemoryIfNeeded() {
        // Limpiar conversaciones antiguas
        if (conversationsCache.size > MAX_CONVERSATIONS) {
            val toRemove = conversationsCache.values
                .sortedBy { it.lastMessageTime }
                .take(conversationsCache.size - MAX_CONVERSATIONS)
            toRemove.forEach { conv ->
                conversationsCache.remove(conv.id)
                messagesCache.remove(conv.id)
            }
        }
        
        // Limpiar mensajes antiguos por conversación
        messagesCache.forEach { (convId, messages) ->
            if (messages.size > MAX_MESSAGES_PER_CONVERSATION) {
                val toKeep = messages
                    .sortedByDescending { it.timestamp }
                    .take(MAX_MESSAGES_PER_CONVERSATION)
                messagesCache[convId] = toKeep.toMutableList()
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

    fun disconnectRealtime() {
        viewModelScope.launch {
            try {
                // messagesChannel?.unsubscribe()
                // realtimeChannel?.unsubscribe()
                // supabaseRealtime.disconnect()
                
                _uiState.value = _uiState.value.copy(
                    isConnected = false,
                    connectionStatus = ConnectionStatus.DISCONNECTED
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error desconectando: ${e.message}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectRealtime()
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
    val messagesMarkedAsRead: Boolean = false,
    // Nuevos campos para Realtime
    val isConnected: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
)

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED, CONNECTING, ERROR
}
