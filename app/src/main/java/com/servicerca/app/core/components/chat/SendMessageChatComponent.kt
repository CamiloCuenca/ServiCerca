package com.servicerca.app.core.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SendMessageChatComponent(
    message: String = "",
    onMessageChange: (String) -> Unit = {},
    onSendMessage: () -> Unit = {},
    onAttachFile: () -> Unit = {},
    onOpenCamera: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()

    ) {
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)


        Row {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = {
                    Text("Escribe un mensaje...")
                },
                trailingIcon = {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    ) {
                        IconButton(
                            onClick = onAttachFile,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.AttachFile, null)
                        }

                        IconButton(
                            onClick = onOpenCamera,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)
                        ) {
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
                onClick = onSendMessage,
                Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .background(
                        if (message.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
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