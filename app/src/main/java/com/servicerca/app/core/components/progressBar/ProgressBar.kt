package com.servicerca.app.core.components.progressBar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.card.CardLevel

@Composable
fun XpBar(
    progress: Float, // Valor entre 0.0f y 1.0f
    modifier: Modifier = Modifier,
    colorXp: Color = Color.Cyan, // Verde XP
    colorBackground: Color = Color.LightGray
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val barWidth = size.width
        val barHeight = size.height

        // Fondo de la barra
        drawRoundRect(
            color = colorBackground,
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(10.dp.toPx())
        )

        // Barra de progreso (XP actual)
        drawRoundRect(
            color = colorXp,
            size = Size(barWidth * progress.coerceIn(0f, 1f), barHeight),
            cornerRadius = CornerRadius(10.dp.toPx())
        )
    }
}