package com.servicerca.app.ui.services.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.servicerca.app.R
import com.mapbox.geojson.Point
import com.servicerca.app.core.components.Map.MapBox
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.ReactionIconButton
import com.servicerca.app.core.components.card.ProviderRow
import com.servicerca.app.core.components.card.ReviewCard
import com.servicerca.app.core.components.card.ServiceDescriptionSection
import com.servicerca.app.core.components.card.ServiceDetailHeader
import com.servicerca.app.core.components.header.SectionHeader
import com.servicerca.app.core.components.input.ReviewInputField
import com.servicerca.app.ui.theme.StarColor
import com.servicerca.app.ui.theme.StarInactiveColor

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

    val context = LocalContext.current
    val service by viewModel.service.collectAsStateWithLifecycle()
    val provider = viewModel.provider.collectAsStateWithLifecycle().value
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val averageRating by viewModel.averageRating.collectAsStateWithLifecycle()
    val providerLevel by viewModel.providerLevel.collectAsStateWithLifecycle()
    val providerAverageRating by viewModel.providerAverageRating.collectAsStateWithLifecycle()
    val providerCommentCount by viewModel.providerCommentCount.collectAsStateWithLifecycle()
    val isLiked by viewModel.isLiked.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()
    val likeCount by viewModel.likeCount.collectAsStateWithLifecycle()
    val canReview by viewModel.canReview.collectAsStateWithLifecycle()
    val isOwner by viewModel.isOwner.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    var reviewText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableIntStateOf(5) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
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
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }

                IconButton(
                    onClick = {
                        service?.let { s ->
                            val deepLink = "https://servicerca-6ee07.web.app/service?serviceId=${s.id}"
                            val texto = buildString {
                                appendLine("🔧 *${s.title}*")
                                appendLine()
                                appendLine("📝 ${s.description}")
                                appendLine("💰 Precio: \$${s.priceMin.toInt()} - \$${s.priceMax.toInt()}")
                                appendLine("📂 Categoría: ${s.type}")
                                appendLine()
                                appendLine("👆 Ver servicio en ServiCerca:")
                                appendLine(deepLink)
                                append("_Si tienes la app instalada, el enlace la abrirá directamente_ 📱")
                            }

                            // Detectar cuál paquete de WhatsApp está instalado
                            val pm = context.packageManager
                            val whatsappPackage = listOf("com.whatsapp", "com.whatsapp.w4b")
                                .firstOrNull { pkg ->
                                    try {
                                        pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)
                                        true
                                    } catch (e: PackageManager.NameNotFoundException) {
                                        false
                                    }
                                }

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, texto)
                                whatsappPackage?.let { setPackage(it) }
                            }

                            if (whatsappPackage != null) {
                                // Abre WhatsApp directo
                                context.startActivity(shareIntent)
                            } else {
                                // WhatsApp no instalado → selector del sistema
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "Compartir servicio")
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = MaterialTheme.colorScheme.surface
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
                        category = service!!.type,
                        rating = String.format("%.1f", averageRating),
                        likeCount = likeCount,
                        isLiked = isLiked
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
                            isSelected = isLiked,
                            onClick = { viewModel.onLikeClick() }
                        )

                        ReactionIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            isSelected = isBookmarked,
                            onClick = { viewModel.onBookmarkClick() }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    ProviderRow(
                        name = if (provider != null) "${provider.name1} ${provider.lastname1}" else "Cargando...",
                        avatarUrl = provider?.profilePictureUrl,
                        level = providerLevel,
                        rating = providerAverageRating,
                        reviewCount = providerCommentCount,
                        onClick = {}
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        showMyLocationButton = false,
                        markerPoint = service?.location?.let { loc ->
                            if (loc.latitude != 0.0 || loc.longitude != 0.0)
                                Point.fromLngLat(loc.longitude, loc.latitude)
                            else null
                        }
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
                        SectionHeader(
                            title = "Reseñas",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${comments.size} comentario${if (comments.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (comments.isEmpty()) {
                        Text(
                            text = "Aún no hay reseñas para este servicio.",
                            style = MaterialTheme.typography.bodyMedium,
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

                    if (canReview) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Selecciona tu calificación",
                                style = MaterialTheme.typography.bodySmall,
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
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_star),
                                            contentDescription = "Calificar $ratingValue estrellas",
                                            tint = if (isSelected) StarColor else StarInactiveColor,
                                            modifier = Modifier.size(24.dp)
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (!isOwner) {
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Este servicio es tuyo",
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
}
