package com.servicerca.app.ui.chat

import android.icu.util.Calendar
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.repository.ChatRepositoryImpl
import com.servicerca.app.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isOnline: Boolean = true,
    val participantName: String = "",
    val participantImage: Int = 0
)

class ChatScreenViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId: String = checkNotNull(savedStateHandle["chatId"])
    private val repository = ChatRepositoryImpl()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
        loadChatInfo()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.getMessages(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    private fun loadChatInfo() {
        viewModelScope.launch {
            repository.getChats().collect { chats ->
                val chat = chats.find { it.chatId == chatId }
                chat?.let {
                    _uiState.value = _uiState.value.copy(
                        participantName = it.participantName,
                        participantImage = it.participantImage
                    )
                }
            }
        }
    }

    fun onMessageChange(newMessage: String) {
        _uiState.value = _uiState.value.copy(currentMessage = newMessage)
    }

    fun sendMessage() {
        val text = _uiState.value.currentMessage.trim()
        if (text.isEmpty()) return

        val newMessage = Message(
            senderId = "me",
            message = text,
            time = getCurrentTime(),
            isMine = true
        )

        viewModelScope.launch {
            repository.sendMessage(chatId, newMessage)
            _uiState.value = _uiState.value.copy(currentMessage = "")
        }
    }

    private fun getCurrentTime(): String {
        val hour = Calendar.HOUR_OF_DAY
        val minute = Calendar.MINUTE
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        return "$hour12:${minute.toString().padStart(2, '0')} $amPm"
    }
}