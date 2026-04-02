package com.servicerca.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.repository.ChatRepositoryImpl
import com.servicerca.app.domain.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatListUiState(
    val chats: List<Chat> = emptyList()
)


class ChatListViewModel : ViewModel() {

    private val repository = ChatRepositoryImpl()

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.getChats().collect { chats ->
                _uiState.value = _uiState.value.copy(chats = chats)
            }
        }
    }
}