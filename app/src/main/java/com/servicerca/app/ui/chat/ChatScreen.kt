package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.chat.HeaderChatComponent
import com.servicerca.app.core.components.chat.MessageBubble
import com.servicerca.app.core.components.chat.SendMessageChatComponent

@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = viewModel(),
    onBack: () -> Unit = {}
){

    val uiState by viewModel.uiState.collectAsState()

    Column {
        HeaderChatComponent(
            imageProfile = R.drawable.foto_perfil,
            nameProfile = stringResource(R.string.name_profile),
            onlineStatus = uiState.isOnline,
            onBack = onBack
        )

        Column (
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ){
            uiState.messages.forEach { message ->
                MessageBubble(
                    message = message.message,
                    time = message.time,
                    isMine = message.isMine,
                    imageProfile = message.imageProfile
                )
            }
        }

        SendMessageChatComponent(
            message = uiState.currentMessage,
            onMessageChange = { viewModel.onMessageChange(it) },
            onSendMessage = { viewModel.sendMessage() }
        )

    }
}


@Composable
@Preview(showBackground = true)
fun ChatScreenPreview(){
    ChatScreen()
}