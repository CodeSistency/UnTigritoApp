package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.domain.model.*
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor() : MessagesRepository {

    private val dummyConversations = listOf(
        Conversation(
            id = "conv1",
            participantId = "client1",
            participantName = "María González",
            participantAvatar = null,
            lastMessage = "Perfecto, nos vemos mañana a las 9am",
            lastMessageTime = Date(System.currentTimeMillis() - 30 * 60 * 1000L),
            unreadCount = 0,
            conversationType = ConversationType.CLIENT
        ),
        Conversation(
            id = "conv2",
            participantId = "client2",
            participantName = "Carlos Rodríguez",
            participantAvatar = null,
            lastMessage = "¿Cuándo puedes venir a revisar el aire?",
            lastMessageTime = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
            unreadCount = 2,
            conversationType = ConversationType.CLIENT
        ),
        Conversation(
            id = "conv3",
            participantId = "support",
            participantName = "Soporte UnTigrito",
            participantAvatar = null,
            lastMessage = "Hola, ¿en qué podemos ayudarte?",
            lastMessageTime = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
            unreadCount = 0,
            conversationType = ConversationType.SUPPORT
        ),
        Conversation(
            id = "conv4",
            participantId = "client4",
            participantName = "Roberto Silva",
            participantAvatar = null,
            lastMessage = "La nevera ya está funcionando perfectamente",
            lastMessageTime = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L),
            unreadCount = 0,
            conversationType = ConversationType.CLIENT
        )
    )

    private val dummyMessages = mapOf(
        "conv1" to listOf(
            Message(
                id = "msg1",
                conversationId = "conv1",
                senderId = "client1",
                senderName = "María González",
                senderAvatar = null,
                content = "Hola, vi tu propuesta para la reparación del grifo",
                timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            ),
            Message(
                id = "msg2",
                conversationId = "conv1",
                senderId = "professional",
                senderName = "Tú",
                senderAvatar = null,
                content = "Hola María, perfecto. ¿Cuándo te conviene que vaya?",
                timestamp = Date(System.currentTimeMillis() - 90 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            ),
            Message(
                id = "msg3",
                conversationId = "conv1",
                senderId = "client1",
                senderName = "María González",
                senderAvatar = null,
                content = "Perfecto, nos vemos mañana a las 9am",
                timestamp = Date(System.currentTimeMillis() - 30 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            )
        ),
        "conv2" to listOf(
            Message(
                id = "msg4",
                conversationId = "conv2",
                senderId = "client2",
                senderName = "Carlos Rodríguez",
                senderAvatar = null,
                content = "Hola, aceptamos tu propuesta para el aire acondicionado",
                timestamp = Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            ),
            Message(
                id = "msg5",
                conversationId = "conv2",
                senderId = "professional",
                senderName = "Tú",
                senderAvatar = null,
                content = "Excelente, ¿cuándo te conviene que vaya?",
                timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            ),
            Message(
                id = "msg6",
                conversationId = "conv2",
                senderId = "client2",
                senderName = "Carlos Rodríguez",
                senderAvatar = null,
                content = "¿Cuándo puedes venir a revisar el aire?",
                timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
                isRead = false,
                messageType = MessageType.TEXT
            )
        ),
        "conv3" to listOf(
            Message(
                id = "msg7",
                conversationId = "conv3",
                senderId = "support",
                senderName = "Soporte UnTigrito",
                senderAvatar = null,
                content = "Hola, ¿en qué podemos ayudarte?",
                timestamp = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
                isRead = true,
                messageType = MessageType.TEXT
            )
        )
    )

    override suspend fun getConversations(): Flow<List<Conversation>> = flow {
        delay(800)
        emit(dummyConversations.sortedByDescending { it.lastMessageTime ?: Date(0) })
    }

    override suspend fun getMessages(conversationId: String): Flow<List<Message>> = flow {
        delay(500)
        val messages = dummyMessages[conversationId] ?: emptyList()
        emit(messages.sortedBy { it.timestamp })
    }

    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: MessageType
    ): Flow<Result<Message>> = flow {
        delay(500)
        
        val newMessage = Message(
            id = "msg_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = "professional",
            senderName = "Tú",
            senderAvatar = null,
            content = content,
            timestamp = Date(),
            isRead = false,
            messageType = messageType
        )
        
        emit(Result.success(newMessage))
    }

    override suspend fun markAsRead(conversationId: String): Flow<Result<Boolean>> = flow {
        delay(300)
        emit(Result.success(true))
    }

    override suspend fun getUnreadCount(): Flow<Int> = flow {
        delay(300)
        val totalUnread = dummyConversations.sumOf { it.unreadCount }
        emit(totalUnread)
    }
}
