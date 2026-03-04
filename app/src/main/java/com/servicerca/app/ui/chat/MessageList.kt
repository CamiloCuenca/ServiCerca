package com.servicerca.app.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.chat.ChatComponent

@Composable
fun MessageListScreen(
){

    Scaffold(
        modifier = Modifier.fillMaxWidth()
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Column{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {

                    Text(
                        text  = stringResource(R.string.title_message_list),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .align(Alignment.CenterStart)


                    )

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {  },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)

                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }

                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                )

                ChatComponent(
                    imageRes = R.drawable.primo_de_juan_camilo,
                    name = "Deiver Bonano Cuenca",
                    lastMessage = "Uy mano, mandame 50 mil pesos porfa, estoy en la quiebra",
                    time = "3:45 PM"
                )

                ChatComponent(
                    imageRes = R.drawable.papa_de_juan_camilo,
                    name = "Mauricio Cuenca",
                    lastMessage = "Usted donde anda!",
                    time = "4:28 PM"
                )

                ChatComponent(
                    imageRes = R.drawable.tio_de_brandon,
                    name = "Julian Montealegre",
                    lastMessage = "Hola, ¿Donde estas? Te necesito para una cosa",
                    time = "5:28 PM"
                )

                ChatComponent(
                    imageRes = R.drawable.otro_primo,
                    name = "Ferney Alexander",
                    lastMessage = "Mano llegame, estoy embalado con los tombos",
                    time = "11:43 AM"
                )
            }
        }
}}

@Preview
@Composable
fun MessageListPreview(){
    MessageListScreen()
}