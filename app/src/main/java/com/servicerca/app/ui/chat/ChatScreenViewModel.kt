package com.servicerca.app.ui.chat

import android.icu.util.Calendar
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Message
import com.servicerca.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isOnline: Boolean = true,
    val participantName: String = "",
    val participantImage: String = ""
)

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId: String = checkNotNull(savedStateHandle["chatId"])
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
        loadChatInfo()
        markAsRead()
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

    private fun markAsRead(){
        viewModelScope.launch {
            repository.markAsRead(chatId)
        }
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hour12:$minuteStr $amPm"
    }
}
