package com.servicerca.app.core.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SendMessageChatComponent() {
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row{
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text("Escribe un mensaje...")
                },
                trailingIcon = {
                    Row (
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    ){
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)
                            ) {
                            Icon(Icons.Default.AttachFile, null)
                        }

                        IconButton(onClick = { },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)) {
                            Icon(Icons.Default.PhotoCamera, null)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(50)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                maxLines = 1
            )

            IconButton(
                onClick = {},
                Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.AutoMirrored.Default.Send,
                    null
                )
            }

        }
    }


}

@Composable
@Preview(showBackground = true)
fun SendMessageChatComponentPreview() {
    Column {
        SendMessageChatComponent()
    }
}