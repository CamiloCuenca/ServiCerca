package com.servicerca.app.ui.chat

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    val time: String,
    val isMine: Boolean,
    val imageProfile: Int? = null
)

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isOnline: Boolean = true
)

class ChatScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onMessageChange(newMessage: String) {
        _uiState.value = _uiState.value.copy(currentMessage = newMessage)
    }

    fun sendMessage() {
        val text = _uiState.value.currentMessage.trim()
        if (text.isEmpty()) return

        val newMessage = Message(
            message = text,
            time = getCurrentTime(),
            isMine = true
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + newMessage,
            currentMessage = ""
        )
    }

    private fun getCurrentTime(): String {
        val hour = Calendar.HOUR_OF_DAY
        val minute = Calendar.MINUTE
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        return "$hour12:${minute.toString().padStart(2, '0')} $amPm"
    }
}