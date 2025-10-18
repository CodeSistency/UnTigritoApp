package com.thecodefather.untigrito.presentation.screens.professionals.messages

import androidx.compose.runtime.Composable

@Composable
fun MessagesInboxScreen(
    onConversationClick: (String) -> Unit = {},
    onSupportClick: () -> Unit = {}
) {
    // TODO: Listado de conversaciones
}

@Composable
fun ChatScreen(
    conversationId: String = "",
    onBackClick: () -> Unit = {}
) {
    // TODO: Pantalla de chat
}

@Composable
fun SupportChatScreen(
    onBackClick: () -> Unit = {}
) {
    // TODO: Chat de soporte
}
