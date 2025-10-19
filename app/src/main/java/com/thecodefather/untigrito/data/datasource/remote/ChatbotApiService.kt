package com.thecodefather.untigrito.data.datasource.remote

import retrofit2.http.*

/**
 * API Service para el chatbot de soporte con IA
 * Integra con los endpoints de /api/chatbot
 */
interface ChatbotApiService {
    
    /**
     * Inicia una nueva conversación con el chatbot
     */
    @POST("chatbot/conversations")
    suspend fun startConversation(
        @Body request: StartConversationRequest
    ): ChatbotApiResponse<ConversationResponse>
    
    /**
     * Envía un mensaje al chatbot y recibe respuesta
     */
    @POST("chatbot/message")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): ChatbotApiResponse<MessageResponse>
    
    /**
     * Obtiene el historial de mensajes de una conversación
     */
    @GET("chatbot/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") conversationId: String
    ): ChatbotApiResponse<MessagesResponse>
}

/**
 * Request para iniciar conversación
 */
data class StartConversationRequest(
    val title: String
)

/**
 * Request para enviar mensaje
 */
data class SendMessageRequest(
    val conversationId: String,
    val message: String
)

/**
 * Response base para todas las respuestas del chatbot
 */
data class ChatbotApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: T? = null, // Para respuestas de mensaje
    val error: String? = null,
    val escalated: Boolean = false,
    val ticketId: String? = null,
    val status: String? = null
)

/**
 * Response de conversación creada
 */
data class ConversationResponse(
    val id: String,
    val title: String,
    val createdAt: String,
    val status: String
)

/**
 * Response de mensaje
 */
data class MessageResponse(
    val id: String,
    val content: String,
    val senderId: String,
    val messageType: String,
    val createdAt: String
)

/**
 * Response de lista de mensajes
 */
data class MessagesResponse(
    val conversationId: String,
    val messages: List<ChatbotMessage>,
    val total: Int
)

/**
 * Modelo de mensaje del chatbot
 */
data class ChatbotMessage(
    val id: String,
    val content: String,
    val senderId: String,
    val senderName: String,
    val senderType: String, // USER, BOT, SYSTEM
    val createdAt: String,
    val messageType: String
)
