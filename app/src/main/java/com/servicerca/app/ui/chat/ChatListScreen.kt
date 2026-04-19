package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.core.components.chat.ChatComponent
import com.servicerca.app.core.components.input.SearchTextField

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onSearch: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {

                SearchTextField(
                    query = "",
                    onQueryChange = onSearch,
                    placeholder = "Buscar chats"
                )

            }

            uiState.chats.forEach { chat ->
                ChatComponent(
                    imageUrl = chat.participantImage,
                    name = chat.participantName,
                    lastMessage = chat.lastMessage,
                    time = chat.lastMessageTime,
                    unreadCount = chat.unreadCount,
                    onChat = { onChatClick(chat.chatId) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListPreview() {
    ChatListScreen()
}