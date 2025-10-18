package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Message
import com.thecodefather.untigrito.domain.model.MessageType
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para enviar un mensaje
 */
class SendMessageUseCase @Inject constructor(
    private val repository: MessagesRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        messageType: String = "TEXT"
    ): Result<Message> {
        return try {
            repository.sendMessage(conversationId, content, MessageType.valueOf(messageType.uppercase())).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
