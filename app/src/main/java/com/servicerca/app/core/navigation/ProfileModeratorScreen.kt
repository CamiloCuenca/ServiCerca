package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardProfileModerator
import com.servicerca.app.core.components.images.ProfileImage

@Composable
fun ProfileModerator (){
    var showConfirmDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { showConfirmDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Cerrar sesión"
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(150.dp)
                    ) {
                        ProfileImage(
                            url = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEga-7mA9kd7EnROYLMEYwURS2xlW1uWK8eWC8F6X3RFuCrJQLnd5eJ8KNOqXeVNuUVM0c4X31Uoz7NlQKJ4QxFfF6EDWAwgT6y1F_HgZ23As74U0wOHy14ClTNC9kP5KJHgPouBaogO5IpYsvxGmDCYlJ9do4tNb9eb6fYBMMSIG3zEcAN-7y2lIrvTwOyb/s320/WhatsApp%20Image%202026-03-04%20at%2023.04.58.jpeg"
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.name_user),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,

                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(R.string.lacation_profile),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        CardProfileModerator(
            title = stringResource(R.string.pending_profile_moderator),
            label = stringResource(R.string.pending_number),
            color = Color(0xFFDB9C16)
        )
        CardProfileModerator(
            title = stringResource(R.string.approved_profile_moderator),
            label = stringResource(R.string.approved_number),
            color = Color(0xFF3CA834)
        )
        CardProfileModerator(
            title = stringResource(R.string.rejected_profile_moderator),
            label = stringResource(R.string.rejected_number),
            color = Color(0xFFC72E2E)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileModeratorPreview(){
    ProfileModerator()
}