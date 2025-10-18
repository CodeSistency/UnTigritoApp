package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Message
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener los mensajes de una conversación
 */
class GetMessagesUseCase @Inject constructor(
    private val repository: MessagesRepository
) {
    suspend operator fun invoke(conversationId: String): Result<List<Message>> {
        return try {
            Result.success(repository.getMessages(conversationId).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
