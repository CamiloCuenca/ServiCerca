package com.servicerca.app.core.components.qr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun QRScannerPlaceholder(
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft cyan background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorScheme.primary.copy(alpha = 0.05f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )
        )

        // Dashed border & scanning line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            val dashPattern = floatArrayOf(20f, 20f)
            val pathEffect = PathEffect.dashPathEffect(dashPattern, 0f)

            // Dashed rounded rectangle border
            drawRoundRect(
                color = colorScheme.primary.copy(alpha = 0.5f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                style = Stroke(width = strokeWidth, pathEffect = pathEffect)
            )

            // Scanning horizontal line
            drawLine(
                color = colorScheme.primary.copy(alpha = 0.4f),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Center icon
        Icon(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = "Scan QR",
            modifier = Modifier.size(64.dp),
            tint = colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QRScannerPlaceholderPreview() {
    Box(modifier = Modifier.padding(32.dp)) {
        QRScannerPlaceholder()
    }
}
