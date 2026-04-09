package com.servicerca.app.core.components.qr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.utils.generarQR

@Composable
fun QRCodeCard(
    modifier: Modifier = Modifier,
    qrRes: Int? = null // Optional QR resource
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier
            .size(320.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorScheme.primary.copy(alpha = 0.12f),
                        colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(240.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background corners using Primary color from theme
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.dp.toPx()
                    val cornerSize = 40.dp.toPx()
                    val primaryColor = colorScheme.primary

                    // Top Left
                    drawLine(
                        color = primaryColor,
                        start = Offset(0f, cornerSize),
                        end = Offset(0f, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primaryColor,
                        start = Offset(0f, 0f),
                        end = Offset(cornerSize, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Top Right
                    drawLine(
                        color = primaryColor,
                        start = Offset(size.width - cornerSize, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primaryColor,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, cornerSize),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Bottom Left
                    drawLine(
                        color = primaryColor,
                        start = Offset(0f, size.height - cornerSize),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primaryColor,
                        start = Offset(0f, size.height),
                        end = Offset(cornerSize, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Bottom Right
                    drawLine(
                        color = primaryColor,
                        start = Offset(size.width - cornerSize, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primaryColor,
                        start = Offset(size.width, size.height),
                        end = Offset(size.width, size.height - cornerSize),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // QR Placeholder or Icon
                if (qrRes != null) {
                    Image(
                        painter = painterResource(id = qrRes),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(180.dp)
                    )
                } else {
                    PantallaQR("Contenido del QR")
                }
            }
        }
    }
}

@Composable
fun PantallaQR(textoParaQR: String) {
    // Generamos el bitmap y lo recordamos
    val qrBitmap = remember(textoParaQR) {
        generarQR(textoParaQR)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Código QR de $textoParaQR",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Escanea para ver el contenido", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Error al generar el QR")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QRCodeCardPreview() {
    QRCodeCard()
}
