package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.chat.HeaderChatComponent
import com.servicerca.app.core.components.chat.MessageBubble
import com.servicerca.app.core.components.chat.SendMessageChatComponent

@Composable
fun ChatScreen(){

    Column {
        HeaderChatComponent(
            imageProfile = R.drawable.foto_perfil,
            nameProfile = stringResource(R.string.name_profile),
            onlineStatus = true
        )

        Column (
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ){
            MessageBubble(
                message = "que se dice gran hijueputa",
                time = "7:15 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "entonces que gonorrea, por que me insulta?",
                time = "7:18 PM",
                isMine = true
            )

            MessageBubble(
                message = "perdon, es que me confundi de chat",
                time = "7:19 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "ah bueno, no hay problema, pero se mas respetuoso con las personas",
                time = "7:20 PM",
                isMine = true
            )

            MessageBubble(
                message = "si, lo siento, te quiero mucho",
                time = "7:21 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )
            MessageBubble(
                message = "que se dice gran hijueputa",
                time = "7:15 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "entonces que gonorrea, por que me insulta?",
                time = "7:18 PM",
                isMine = true
            )

            MessageBubble(
                message = "perdon, es que me confundi de chat",
                time = "7:19 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "ah bueno, no hay problema, pero se mas respetuoso con las personas",
                time = "7:20 PM",
                isMine = true
            )

            MessageBubble(
                message = "si, lo siento, te quiero mucho",
                time = "7:21 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )
            MessageBubble(
                message = "que se dice gran hijueputa",
                time = "7:15 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "entonces que gonorrea, por que me insulta?",
                time = "7:18 PM",
                isMine = true
            )

            MessageBubble(
                message = "perdon, es que me confundi de chat",
                time = "7:19 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )

            MessageBubble(
                message = "ah bueno, no hay problema, pero se mas respetuoso con las personas",
                time = "7:20 PM",
                isMine = true
            )

            MessageBubble(
                message = "si, lo siento, te quiero mucho",
                time = "7:21 PM",
                isMine = false,
                imageProfile = R.drawable.foto_perfil
            )
        }

        SendMessageChatComponent()

    }
}


@Composable
@Preview(showBackground = true)
fun ChatScreenPreview(){
    ChatScreen()
}