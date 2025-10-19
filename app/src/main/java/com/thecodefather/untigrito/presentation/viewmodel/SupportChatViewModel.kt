package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.datasource.remote.ChatbotApiService
import com.thecodefather.untigrito.data.datasource.remote.ChatbotMessage
import com.thecodefather.untigrito.data.datasource.remote.SendMessageRequest
import com.thecodefather.untigrito.data.datasource.remote.StartConversationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    private val chatbotApiService: ChatbotApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportChatUiState())
    val uiState: StateFlow<SupportChatUiState> = _uiState.asStateFlow()

    /**
     * Inicia una nueva conversación con el chatbot
     */
    fun startNewConversation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val request = StartConversationRequest(
                    title = "Consulta de Soporte"
                )
                
                val response = chatbotApiService.startConversation(request)
                
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversationId = response.data.id,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = response.error ?: "Error al iniciar conversación"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    /**
     * Envía un mensaje al chatbot
     */
    fun sendMessage(messageText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true, errorMessage = null)
            
            try {
                // Si no hay conversación activa, crear una nueva
                if (_uiState.value.conversationId == null) {
                    startNewConversation()
                    // Esperar a que se cree la conversación
                    return@launch
                }
                
                // Agregar mensaje del usuario inmediatamente
                val userMessage = ChatbotMessage(
                    id = UUID.randomUUID().toString(),
                    content = messageText,
                    senderId = "user",
                    senderName = "Tú",
                    senderType = "USER",
                    createdAt = Date().toString(),
                    messageType = "TEXT"
                )
                
                val currentMessages = _uiState.value.messages.toMutableList()
                currentMessages.add(userMessage)
                _uiState.value = _uiState.value.copy(messages = currentMessages)
                
                // Enviar al chatbot
                val request = SendMessageRequest(
                    conversationId = _uiState.value.conversationId!!,
                    message = messageText
                )
                
                val response = chatbotApiService.sendMessage(request)
                
                if (response.success && response.message != null) {
                    // Agregar respuesta del bot
                    val botMessage = ChatbotMessage(
                        id = response.message.id,
                        content = response.message.content,
                        senderId = response.message.senderId,
                        senderName = "Asistente IA",
                        senderType = "BOT",
                        createdAt = response.message.createdAt,
                        messageType = response.message.messageType
                    )
                    
                    val updatedMessages = _uiState.value.messages.toMutableList()
                    updatedMessages.add(botMessage)
                    
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        messages = updatedMessages,
                        errorMessage = null
                    )
                } else {
                    // Manejar escalación a agente humano
                    if (response.escalated) {
                        val systemMessage = ChatbotMessage(
                            id = UUID.randomUUID().toString(),
                            content = response.error ?: "Tu consulta ha sido transferida a un agente humano. Un especialista se pondrá en contacto contigo pronto.",
                            senderId = "system",
                            senderName = "Sistema",
                            senderType = "SYSTEM",
                            createdAt = Date().toString(),
                            messageType = "SYSTEM"
                        )
                        
                        val updatedMessages = _uiState.value.messages.toMutableList()
                        updatedMessages.add(systemMessage)
                        
                        _uiState.value = _uiState.value.copy(
                            isSendingMessage = false,
                            messages = updatedMessages,
                            isEscalated = true,
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSendingMessage = false,
                            errorMessage = response.error ?: "Error al enviar mensaje"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSendingMessage = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    /**
     * Carga el historial de mensajes de la conversación actual
     */
    fun loadMessages() {
        viewModelScope.launch {
            val conversationId = _uiState.value.conversationId ?: return@launch
            
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val response = chatbotApiService.getMessages(conversationId)
                
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        messages = response.data.messages,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = response.error ?: "Error al cargar mensajes"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    /**
     * Reinicia la conversación (limpia estado)
     */
    fun resetConversation() {
        _uiState.value = SupportChatUiState()
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Estado de la UI del chat de soporte
 */
data class SupportChatUiState(
    val isLoading: Boolean = false,
    val isSendingMessage: Boolean = false,
    val messages: List<ChatbotMessage> = emptyList(),
    val conversationId: String? = null,
    val isEscalated: Boolean = false,
    val errorMessage: String? = null
)
