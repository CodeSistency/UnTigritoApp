package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Conversation
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener la lista de conversaciones
 */
class GetConversationsUseCase @Inject constructor(
    private val repository: MessagesRepository
) {
    suspend operator fun invoke(
    ): Flow<List<Conversation>> {
        return repository.getConversations()
    }
}
