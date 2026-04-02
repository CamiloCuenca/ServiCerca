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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.chat.ChatComponent
import com.servicerca.app.core.components.input.SearchTextField

@Composable
fun MessageListScreen(
    onSearch: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {}
){



        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Column{
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

                ChatComponent(
                    imageRes = R.drawable.primo_de_juan_camilo,
                    name = "Deiver Bonano Cuenca",
                    lastMessage = "Uy mano, mandame 50 mil pesos porfa, estoy en la quiebra",
                    time = "3:45 PM",
                    onChat = { onChatClick("chat_deiver") }  // 👈 ID único por chat
                )

                ChatComponent(
                    imageRes = R.drawable.papa_de_juan_camilo,
                    name = "Mauricio Cuenca",
                    lastMessage = "Usted donde anda!",
                    time = "4:28 PM",
                    onChat = { onChatClick("chat_mauricio") }
                )

                ChatComponent(
                    imageRes = R.drawable.tio_de_brandon,
                    name = "Julian Montealegre",
                    lastMessage = "Hola, ¿Donde estas? Te necesito para una cosa",
                    time = "5:28 PM",
                    onChat = { onChatClick("chat_julian") }
                )

                ChatComponent(
                    imageRes = R.drawable.otro_primo,
                    name = "Ferney Alexander",
                    lastMessage = "Mano llegame, estoy embalado con los tombos",
                    time = "11:43 AM",
                    onChat = { onChatClick("chat_ferney") }
                )
            }
        }
}

@Preview(showBackground = true)
@Composable
fun MessageListPreview(){
    MessageListScreen()
}