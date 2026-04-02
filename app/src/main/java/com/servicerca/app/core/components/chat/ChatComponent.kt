package com.servicerca.app.core.components.chat

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun ChatComponent(
    @DrawableRes imageRes: Int,
    name: String,
    lastMessage: String,
    time: String?,
    unreadCount : Int,
    onChat: () -> Unit = {}
) {

    Button(
        onClick = { onChat() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Centro: ocupa el espacio restante, pero NO empuja la derecha fuera
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, // o 2 si quieres
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (time != null) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1
                    )
                }

                Box {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }




            }
        }
    }

    Spacer(modifier = Modifier.height(2.dp))
}


@Composable
@Preview(showBackground = true)
fun ChatScreen() {
    ChatComponent(
        imageRes = R.drawable.foto_jcc,
        name = "Juan Camilo Cuenca",
        lastMessage = "Hola mi amor, ¿cómo estás?",
        time = "12:45 PM",
        unreadCount = 20
    )
}

