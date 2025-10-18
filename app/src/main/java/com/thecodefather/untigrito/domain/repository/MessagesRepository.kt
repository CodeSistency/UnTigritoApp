package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Conversation
import com.thecodefather.untigrito.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    suspend fun getConversations(): Flow<List<Conversation>>
    suspend fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: com.thecodefather.untigrito.domain.model.MessageType = com.thecodefather.untigrito.domain.model.MessageType.TEXT
    ): Flow<Result<Message>>
    suspend fun markAsRead(conversationId: String): Flow<Result<Boolean>>
    suspend fun getUnreadCount(): Flow<Int>
}