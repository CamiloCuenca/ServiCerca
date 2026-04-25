package com.servicerca.app.core.components.chat

import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

import androidx.compose.material3.Surface
import androidx.compose.foundation.clickable

@Composable
fun HeaderChatComponent(
    imageProfile: Any,
    nameProfile: String = "",
    onlineStatus: Boolean = false,
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.chat_back_content_description),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AsyncImage(
                model = imageProfile,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.primo_de_juan_camilo),
                error = painterResource(R.drawable.primo_de_juan_camilo)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = nameProfile,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (onlineStatus) stringResource(R.string.chat_online_status) else stringResource(R.string.chat_offline_status),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (onlineStatus) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (onlineStatus) FontWeight.SemiBold else FontWeight.Normal
                )
            }

        }
    }
}


@Composable
@Preview(showBackground = true)
fun HeaderChatComponentPreview() {
    HeaderChatComponent(
        imageProfile = R.drawable.primo_de_juan_camilo,
        nameProfile = "Deiver Bonano Cuenca",
        onlineStatus = true
    )
}