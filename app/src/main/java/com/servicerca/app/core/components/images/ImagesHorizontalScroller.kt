package com.servicerca.app.core.components.images

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import com.servicerca.app.core.components.card.CardServiceImage

@Composable
fun ImagesHorizontalScroller(
    images: List<ByteArray>,
    onAddImage: () -> Unit,
    onRemoveAt: (index: Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mostrar las imágenes actuales
            itemsIndexed(images) { index, bytes ->
                CardServiceImage(
                    imageBytes = bytes,
                    onRemove = { onRemoveAt(index) }
                )
            }

            // Card final para "Añadir" si aún no llegamos a 5
            if (images.size < 5) {
                item {
                    CardServiceImage(onClick = onAddImage)
                }
            }
        }
    }
}