package com.servicerca.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Chat
import com.servicerca.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ChatListUiState(
    val chats: List<Chat> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Usamos combine para filtrar la lista cada vez que cambie el query o lleguen nuevos chats de Firebase
    val uiState: StateFlow<ChatListUiState> = repository.getChats()
        .combine(_searchQuery) { chats, query ->
            val filteredChats = if (query.isBlank()) {
                chats
            } else {
                chats.filter { 
                    it.participantName.contains(query, ignoreCase = true) ||
                    it.lastMessage.contains(query, ignoreCase = true)
                }
            }
            ChatListUiState(chats = filteredChats, searchQuery = query)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChatListUiState()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
