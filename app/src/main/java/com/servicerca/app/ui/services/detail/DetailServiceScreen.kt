package com.servicerca.app.ui.services.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.servicerca.app.R
import com.servicerca.app.core.components.Map.MapBox
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.ReactionIconButton
import com.servicerca.app.core.components.card.ProviderRow
import com.servicerca.app.core.components.card.ReviewCard
import com.servicerca.app.core.components.card.ServiceDescriptionSection
import com.servicerca.app.core.components.card.ServiceDetailHeader
import com.servicerca.app.core.components.input.ReviewInputField

@Composable
fun DetailServiceScreen(
    serviceId: String,
    onBack: () -> Unit,
    onMakeReservation: (String) -> Unit = {},
    viewModel: DetailServiceViewModel = hiltViewModel()
) {
    LaunchedEffect(serviceId) {
        viewModel.loadService(serviceId)
    }

    val service by viewModel.service.collectAsState()
    val provider = viewModel.provider.collectAsState().value
    val comments by viewModel.comments.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()

    var isSelectedLike by remember { mutableStateOf(false) }
    var isSelectedPin by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableIntStateOf(5) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                val s = service

                // Decodificar base64 fuera de las llamadas composable
                val decodedBitmap = remember(s?.photoUrl) {
                    s?.photoUrl?.takeIf { it.startsWith("data:image") }?.let { dataUri ->
                        runCatching {
                            val base64Part = dataUri.substringAfter(",")
                            val bytes = try {
                                Base64.decode(base64Part, Base64.NO_WRAP)
                            } catch (e1: Exception) {
                                Base64.decode(base64Part, Base64.DEFAULT)
                            }
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }.getOrNull()
                    }
                }

                if (decodedBitmap != null) {
                    Image(
                        bitmap = decodedBitmap.asImageBitmap(),
                        contentDescription = s?.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = s?.photoUrl,
                        contentDescription = s?.title,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.service),
                        error = painterResource(id = R.drawable.service),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (service == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ServiceDetailHeader(
                        title = service!!.title,
                        subtitle = service!!.type,
                        price = service!!.priceMin,
                        isVerified = true,
                        category = service!!.type
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReactionIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                )
                            },
                            isSelected = isSelectedLike,
                            onClick = { isSelectedLike = !isSelectedLike }
                        )
                        ReactionIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            isSelected = isSelectedPin,
                            onClick = { isSelectedPin = !isSelectedPin }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    ProviderRow(
                        name = if (provider != null) "${provider.name1} ${provider.lastname1}" else "Cargando...",
                        avatarUrl = provider?.profilePictureUrl,
                        level = "MAESTRO",
                        rating = averageRating.takeIf { it > 0f } ?: 0f,
                        reviewCount = comments.size,
                        onClick = {}
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    ServiceDescriptionSection(description = service!!.description)

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reseñas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${comments.size} comentario${if (comments.size != 1) "s" else ""}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (comments.isEmpty()) {
                        Text(
                            text = "Aún no hay reseñas para este servicio.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    } else {
                        comments.forEachIndexed { index, comment ->
                            ReviewCard(
                                avatarUrl = comment.userAvatar,
                                reviewerName = comment.userName,
                                timeAgo = comment.timeAgo,
                                rating = comment.rating,
                                reviewText = comment.text
                            )
                            if (index < comments.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Selecciona tu califiación",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                val ratingValue = index + 1
                                val isSelected = ratingValue <= selectedRating
                                IconButton(
                                    onClick = { selectedRating = ratingValue },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_star),
                                        contentDescription = "Calificar $ratingValue estrellas",
                                        tint = if (isSelected) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    ReviewInputField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        onSend = {
                            if (reviewText.isNotBlank()) {
                                viewModel.addComment(
                                    rating = selectedRating,
                                    text = reviewText
                                )
                                reviewText = ""
                                selectedRating = 5
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            PrimaryButton(
                text = "SOLICITAR SERVICIO →",
                onClick = { onMakeReservation(serviceId) }
            )
        }
    }
}
