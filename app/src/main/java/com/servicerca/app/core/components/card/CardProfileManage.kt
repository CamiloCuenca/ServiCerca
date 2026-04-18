package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CardProfileManage(
    name: String,
    email: String,
    imageProfile: String,
    onSeeProfile: () -> Unit = {},
    onSuspendProfile: () -> Unit = {},
    onDeleteProfile: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageProfile,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(30.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    CardMenuUserManage(
                        onSeeProfile = {
                            menuExpanded = false
                            onSeeProfile()
                        },
                        onSuspendProfile = {
                            menuExpanded = false
                            onSuspendProfile()
                        },
                        onDeleteProfile = {
                            menuExpanded = false
                            onDeleteProfile()
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = false)
fun CardProfileManagePreview() {
    CardProfileManage(
        name = "Heliana Cuenca",
        email = "juan.camilo@email.com",
        imageProfile = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEga-7mA9kd7EnROYLMEYwURS2xlW1uWK8eWC8F6X3RFuCrJQLnd5eJ8KNOqXeVNuUVM0c4X31Uoz7NlQKJ4QxFfF6EDWAwgT6y1F_HgZ23As74U0wOHy14ClTNC9kP5KJHgPouBaogO5IpYsvxGmDCYlJ9do4tNb9eb6fYBMMSIG3zEcAN-7y2lIrvTwOyb/s320/WhatsApp%20Image%202026-03-04%20at%2023.04.58.jpeg"
    )
}