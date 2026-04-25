package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.core.components.chat.HeaderChatComponent
import com.servicerca.app.core.components.chat.MessageBubble
import com.servicerca.app.core.components.chat.SendMessageChatComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect


@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Scroll to the bottom whenever a new message is added
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            HeaderChatComponent(
                imageProfile = uiState.participantImage,
                nameProfile = uiState.participantName,
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
                .padding(horizontal = 16.dp),
            state = listState
        ) {
            item {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            }
            items(
                items = uiState.messages,
                key = { message -> message.hashCode() } // Using hashCode as a unique key for the message content
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
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ChatScreenPreview(){
    ChatScreen()
}