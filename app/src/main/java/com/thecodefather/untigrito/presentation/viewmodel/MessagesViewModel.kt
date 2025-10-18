package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.Conversation
import com.thecodefather.untigrito.domain.model.Message
import com.thecodefather.untigrito.domain.model.MessageType
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messagesRepository: MessagesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            messagesRepository.getConversations()
                .collect { conversations ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = conversations,
                        errorMessage = null
                    )
                }
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMessages = true, errorMessage = null)
            
            messagesRepository.getMessages(conversationId)
                .collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMessages = false,
                        messages = messages,
                        errorMessage = null
                    )
                }
        }
    }

    fun sendMessage(conversationId: String, content: String, messageType: MessageType = MessageType.TEXT) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true, errorMessage = null)
            
            messagesRepository.sendMessage(conversationId, content, messageType)
                .collect { result ->
                    result.fold(
                        onSuccess = { message ->
                            _uiState.value = _uiState.value.copy(
                                isSendingMessage = false,
                                errorMessage = null
                            )
                            // Recargar mensajes para mostrar el nuevo
                            loadMessages(conversationId)
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isSendingMessage = false,
                                errorMessage = exception.message ?: "Error al enviar mensaje"
                            )
                        }
                    )
                }
        }
    }

    fun markAsRead(conversationId: String) {
        viewModelScope.launch {
            messagesRepository.markAsRead(conversationId)
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            // Recargar conversaciones para actualizar contadores
                            loadConversations()
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                errorMessage = exception.message ?: "Error al marcar como leído"
                            )
                        }
                    )
                }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            messagesRepository.getUnreadCount()
                .collect { unreadCount ->
                    _uiState.value = _uiState.value.copy(unreadCount = unreadCount)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class MessagesUiState(
    val isLoading: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val isSendingMessage: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val errorMessage: String? = null
)
