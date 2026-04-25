package com.servicerca.app.core.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.servicerca.app.R

@Composable
fun MessageBubble(
    message: String,
    time: String,
    isMine: Boolean,
    isRead: Boolean = false,
    imageProfile: String? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {

        if (!isMine && imageProfile != null) {
            AsyncImage(
                model = imageProfile,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.primo_de_juan_camilo),
                error = painterResource(R.drawable.primo_de_juan_camilo)
            )

            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            val bubbleShape = if (isMine) {
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
            } else {
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = bubbleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = message,
                    color = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = if (isMine && !isRead) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MessageBubblePreview() {

    Column {
        MessageBubble(
            message = "Hola, ¿cómo estás? Este es un mensaje de prueba para mostrar cómo se ve el bubble de mensaje en la interfaz de chat.",
            time = "3:45 PM",
            isMine = false,
            imageProfile = ""
        )

        Spacer(Modifier.height(16.dp))

        MessageBubble(
            message = "¡Hola! Estoy bien, gracias por preguntar. Este es otro mensaje de prueba para mostrar cómo se ve el bubble de mensaje en la interfaz de chat cuando es un mensaje mío.",
            time = "3:46 PM",
            isMine = true,
            imageProfile = null
        )
    }
}
