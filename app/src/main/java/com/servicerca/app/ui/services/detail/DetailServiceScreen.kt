package com.servicerca.app.ui.services.detail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.ReactionIconButton
import com.servicerca.app.core.components.card.LocationSection
import com.servicerca.app.core.components.card.ProviderRow
import com.servicerca.app.core.components.card.ReviewCard
import com.servicerca.app.core.components.card.ServiceDescriptionSection
import com.servicerca.app.core.components.card.ServiceDetailHeader
import com.servicerca.app.core.components.input.ReviewInputField

@Composable
fun DetailServiceScreen() {
    var isSelectedLike by remember { mutableStateOf(false) }
    var isSelectedPin by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero image con botones atrás / compartir ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.service),
                    contentDescription = "service",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
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
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = Color.White
                    )
                }
            }

            // ── Tarjeta blanca con esquinas redondeadas ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Header: título, subtítulo, precio, tags
                ServiceDetailHeader(
                    title = "Reparación de Fugas",
                    subtitle = "Plomería Residencial",
                    price = 350.0,
                    isVerified = true,
                    category = "Hogar"
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Botones de reacción (like / pin)
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
                    color = Color(0xFFF0F0F0)
                )

                // Fila del proveedor
                ProviderRow(
                    name = "Juan Pérez",
                    avatarRes = R.drawable.service,
                    level = "MAESTRO",
                    rating = 4.8f,
                    reviewCount = 120,
                    onClick = {}
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Ubicación
                LocationSection(
                    locationName = "Zona Centro",
                    distanceLabel = "A 2.5 km de ti"
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Descripción del servicio
                ServiceDescriptionSection(
                    description = "Servicio profesional de plomería especializado en detección y reparación de fugas de agua y gas. Utilizamos tecnología de ultrasonido para localizar problemas sin romper paredes innecesariamente. Incluye materiales básicos y garantía de 30 días en mano de obra."
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Reseñas
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
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Ver todas",
                        fontSize = 13.sp,
                        color = Color(0xFF00BCD4),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ReviewCard(
                    avatarRes = R.drawable.service,
                    reviewerName = "Maria G.",
                    timeAgo = "Hace 2 días",
                    rating = 5,
                    reviewText = "Excelente servicio, llegó muy rápido y resolvió el problema en menos de una hora. Muy recomendado."
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color(0xFFF0F0F0)
                )

                ReviewCard(
                    avatarRes = R.drawable.service,
                    reviewerName = "Carlos R.",
                    timeAgo = "Hace 1 semana",
                    rating = 4,
                    reviewText = "Buen trabajo, aunque llegó un poco tarde por el tráfico. La reparación quedó perfecta."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo para escribir reseña
                ReviewInputField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    onSend = { reviewText = "" },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ── Botón principal fijo abajo ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            PrimaryButton(
                text = "SOLICITAR SERVICIO →",
                onClick = { }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailServiceScreenPreview() {
    DetailServiceScreen()
}