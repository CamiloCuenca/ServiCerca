package com.servicerca.app.core.components.images

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.servicerca.app.core.components.card.CardServiceImage

@Composable
fun ImagesHorizontalScroller() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 7.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ejemplo: 3 imágenes ya existentes
            items(listOf(1, 2, 3, 4, 5, 6, 7, 8)) {
                CardServiceImage()
            }

            // ✅ Card final para "Añadir"
            item {
                CardServiceImage() // o tu CardAddImage()
            }
        }
    }
}