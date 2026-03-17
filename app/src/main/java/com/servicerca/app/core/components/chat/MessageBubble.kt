package com.servicerca.app.core.components.chat

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R

@Composable
fun MessageBubble(
    message: String,
    time: String,
    isMine: Boolean,
    imageProfile: Int? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {

        if (!isMine && imageProfile != null) {
            Image(
                painter = painterResource(imageProfile),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom)
            )

            Spacer(Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
                    .widthIn(max = 260.dp)
            ) {
                Text(
                    text = message,
                    color = Color.Black,
                    fontWeight = if (isMine) FontWeight.SemiBold else FontWeight.Normal,
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
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
            imageProfile = R.drawable.primo_de_juan_camilo
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