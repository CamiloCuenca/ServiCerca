package com.servicerca.app.core.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.ReactionIconButton
import com.servicerca.app.core.components.tag.CategoryTag
import com.servicerca.app.core.components.tag.LevelTag
import com.servicerca.app.core.components.tag.VerificationTag
import com.servicerca.app.ui.theme.ServiCercaTheme

@Composable
fun CardService( // TODO @CAMILOCUENCA luego de tener el firestorage ponerle el parametro de imagn del servicio.
    modifier: Modifier = Modifier,
    title: String = "Reparación de Fugas Urgente",
    category: String = "Hogar",
    distance: String = "A 2.4 km de ti",
    priceRange: String = "$20 - $50 USD",
    rating: String = "4.9",
    isVerified: Boolean = true,
    level: String = "EXPERTO",
    photoUrl: String? = null,
    onRequestClick: () -> Unit = {}
) {

    var isSelectedLike by remember { mutableStateOf(false) }
    var isSelectedPin by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {

            // Imagen
            val decodedBase64Bitmap = remember(photoUrl) {
                photoUrl?.takeIf { it.startsWith("data:image") }?.let { dataUri ->
                    runCatching {
                        val base64Part = dataUri.substringAfter(",")
                        val bytes = try {
                            Base64.decode(base64Part, Base64.NO_WRAP)
                        } catch (e1: Exception) {
                            Base64.decode(base64Part, Base64.DEFAULT)
                        }
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }.onFailure {
                        android.util.Log.w("CardService", "failed to decode base64 image: ${it.message}")
                    }.getOrNull()
                }
            }

            val decodedFileBitmap = remember(photoUrl) {
                photoUrl?.takeIf { it.startsWith("file://") }?.let { fileUri ->
                    runCatching {
                        val path = fileUri.removePrefix("file://")
                        val fileBytes = java.io.File(path).readBytes()
                        BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.size)
                    }.onFailure {
                        android.util.Log.w("CardService", "failed to read local file image: ${it.message}")
                    }.getOrNull()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                when {
                    decodedBase64Bitmap != null -> {
                        Log.d("CardService", "rendering decodedBase64Bitmap for title=$title")
                        Image(
                            bitmap = decodedBase64Bitmap.asImageBitmap(),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    decodedFileBitmap != null -> {
                        Log.d("CardService", "rendering decodedFileBitmap for title=$title")
                        Image(
                            bitmap = decodedFileBitmap.asImageBitmap(),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    !photoUrl.isNullOrBlank() && !photoUrl.startsWith("data:image") && !photoUrl.startsWith("file://") -> {
                        Log.d("CardService", "rendering remote/async image for title=$title model=$photoUrl")
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.service),
                            error = painterResource(id = R.drawable.service),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Log.d("CardService", "rendering fallback image for title=$title")
                        Image(
                            painter = painterResource(id = R.drawable.plumber),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Overlays dentro del mismo Box para que Modifier.align funcione correctamente
                if (isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        VerificationTag()
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "Compartir",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                // Categoría y rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    CategoryTag(
                        text = category,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Distancia
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint =  MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = distance,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Precio y nivel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "PRECIO ESTIMADO",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = priceRange,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    LevelTag(
                        text = level,
                        backgroundColor = Color(0xFFF3E5F5),
                        contentColor = Color(0xFF9C27B0)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))



                Spacer(modifier = Modifier.height(16.dp))

                // Reacciones y compartir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
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

                        PrimaryButton(
                            text = "Solicitar servicio",
                            onClick = onRequestClick,

                        )




                    }


                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun CardServicePreview() {
    ServiCercaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {
            CardService(
                onRequestClick = {}
            )
        }
    }
}