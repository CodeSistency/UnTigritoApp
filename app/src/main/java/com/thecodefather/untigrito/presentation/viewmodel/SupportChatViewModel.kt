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
                // Simular respuesta local temporalmente
                val conversationId = UUID.randomUUID().toString()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    conversationId = conversationId,
                    errorMessage = null
                )
                
                // Agregar mensaje de bienvenida del bot
                val welcomeMessage = ChatbotMessage(
                    id = UUID.randomUUID().toString(),
                    content = "¡Hola! Soy tu asistente virtual de UnTigrito. ¿En qué puedo ayudarte hoy?",
                    senderId = "bot",
                    senderName = "Asistente UnTigrito",
                    senderType = "BOT",
                    createdAt = Date().toString(),
                    messageType = "TEXT"
                )
                
                _uiState.value = _uiState.value.copy(
                    messages = listOf(welcomeMessage)
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar conversación: ${e.message}"
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
                
                // Simular respuesta del bot localmente
                val botResponse = generateBotResponse(messageText)
                val botMessage = ChatbotMessage(
                    id = UUID.randomUUID().toString(),
                    content = botResponse,
                    senderId = "bot",
                    senderName = "Asistente UnTigrito",
                    senderType = "BOT",
                    createdAt = Date().toString(),
                    messageType = "TEXT"
                )
                
                val updatedMessages = currentMessages.toMutableList()
                updatedMessages.add(botMessage)
                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages,
                    isSendingMessage = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSendingMessage = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Genera una respuesta del bot basada en el mensaje del usuario
     */
    private fun generateBotResponse(userMessage: String): String {
        val message = userMessage.lowercase()
        
        return when {
            message.contains("hola") || message.contains("buenos") || message.contains("buenas") -> {
                "¡Hola! Me alegra saludarte. ¿En qué puedo ayudarte con UnTigrito?"
            }
            message.contains("ayuda") || message.contains("problema") -> {
                "Estoy aquí para ayudarte. ¿Podrías contarme más detalles sobre tu consulta?"
            }
            message.contains("servicio") || message.contains("tigrito") -> {
                "UnTigrito conecta clientes con profesionales de confianza para todo tipo de servicios. ¿Necesitas ayuda con algún servicio específico?"
            }
            message.contains("precio") || message.contains("costo") -> {
                "Los precios varían según el servicio y el profesional. Puedes ver los precios en cada perfil de profesional o solicitar un presupuesto personalizado."
            }
            message.contains("contacto") || message.contains("telefono") -> {
                "Puedes contactar con nosotros a través de esta aplicación o visitar nuestro sitio web. ¿Hay algo específico en lo que pueda ayudarte?"
            }
            message.contains("gracias") || message.contains("muchas gracias") -> {
                "¡De nada! Me alegra poder ayudarte. ¿Hay algo más en lo que pueda asistirte?"
            }
            message.contains("adios") || message.contains("hasta luego") -> {
                "¡Hasta luego! Que tengas un excelente día. Recuerda que estoy aquí cuando necesites ayuda."
            }
            else -> {
                "Entiendo tu consulta. Te recomiendo revisar nuestros servicios disponibles o contactar directamente con un profesional. ¿Hay algo más específico en lo que pueda ayudarte?"
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
