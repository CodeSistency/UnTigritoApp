package com.thecodefather.untigrito.domain.model

import java.util.Date

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String?,
    val content: String,
    val timestamp: Date,
    val isRead: Boolean,
    val messageType: MessageType,
    val attachments: List<String> = emptyList()
)

data class Conversation(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatar: String?,
    val lastMessage: String?,
    val lastMessageTime: Date?,
    val unreadCount: Int,
    val conversationType: ConversationType,
    val isActive: Boolean = true
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    SYSTEM
}

enum class ConversationType {
    CLIENT,
    SUPPORT
}