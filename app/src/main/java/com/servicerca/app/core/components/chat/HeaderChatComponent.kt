package com.servicerca.app.core.components.chat

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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

@Composable
fun HeaderChatComponent(
    @DrawableRes imageProfile : Int,
    nameProfile : String = "",
    onlineStatus : Boolean = false,
    onBack : () -> Unit = {}
) {

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {

    Row {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.chat_back_content_description)
            )
        }

        Image(
            painter = painterResource(id = imageProfile),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        ) {
            Text(
                text = nameProfile,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (onlineStatus) stringResource(R.string.chat_online_status) else stringResource(R.string.chat_offline_status),
                style = MaterialTheme.typography.bodySmall,
                color = if (onlineStatus) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)

            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null
                )
            }

            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)

                ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

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