package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.core.components.chat.HeaderChatComponent
import com.servicerca.app.core.components.chat.MessageBubble
import com.servicerca.app.core.components.chat.SendMessageChatComponent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
        topBar = {
            HeaderChatComponent(
                imageProfile = uiState.participantImage,
                nameProfile = uiState.participantName.ifBlank { "Cargando..." },
                onlineStatus = uiState.isOnline,
                onBack = onBack
            )
        },
        bottomBar = {
            SendMessageChatComponent(
                message = uiState.currentMessage,
                onMessageChange = { viewModel.onMessageChange(it) },
                onSendMessage = { viewModel.sendMessage() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 16.dp),
            state = listState
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(
                items = uiState.messages,
                key = { message -> message.id }
            ) { message ->
                MessageBubble(
                    message = message.message,
                    time = message.time,
                    isMine = message.isMine,
                    isRead = message.isRead,
                    imageProfile = message.imageProfile
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}