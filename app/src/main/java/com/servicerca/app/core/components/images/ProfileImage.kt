package com.servicerca.app.core.components.images

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.servicerca.app.R

@Composable
fun ProfileImage(url: String) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(150.dp)
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .size(150.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                )
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),

                    placeholder = painterResource(R.drawable.logo_profile),
                    error = painterResource(R.drawable.logo_profile),

                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileImagePreview(){
    ProfileImage(
        url = "https://picsum.photos/300"
    )
}