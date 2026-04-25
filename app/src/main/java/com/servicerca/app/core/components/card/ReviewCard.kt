package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import com.servicerca.app.R
import com.servicerca.app.ui.theme.StarColor
import com.servicerca.app.ui.theme.StarInactiveColor

/**
 * Tarjeta que muestra un comentario/reseña de un usuario sobre un servicio.
 *
 * @param avatarUrl URL de la imagen de perfil del usuario. Se carga con Coil (AsyncImage)
 *                  para soportar tanto URLs remotas como la imagen local de fallback.
 * @param reviewerName Nombre del autor de la reseña.
 * @param timeAgo Texto relativo de tiempo (ej. "Hace 2 días").
 * @param rating Calificación de 1 a 5 estrellas.
 * @param reviewText Contenido de la reseña.
 */
@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    avatarUrl: String,
    reviewerName: String,
    timeAgo: String,
    rating: Int,
    reviewText: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar cargado desde URL con Coil; imagen local como fallback
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = reviewerName,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.service),
                    error = painterResource(id = R.drawable.service),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = reviewerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = timeAgo,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Estrellas de calificación
            Row {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        tint = if (index < rating) StarColor else StarInactiveColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = reviewText,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewCardPreview() {
    ReviewCard(
        avatarUrl = "https://picsum.photos/200?random=2",
        reviewerName = "Maria G.",
        timeAgo = "Hace 2 días",
        rating = 5,
        reviewText = "Excelente servicio, llegó muy rápido y resolvió el problema en menos de una hora. Muy recomendado."
    )
}
