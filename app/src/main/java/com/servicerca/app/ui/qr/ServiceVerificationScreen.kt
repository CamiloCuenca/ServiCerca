package com.servicerca.app.ui.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.AlternativeCodeField
import com.servicerca.app.core.components.navigation.AppTopAppBarBack
import com.servicerca.app.core.components.qr.QRCodeCard
import com.servicerca.app.core.utils.generarCodigoAlternativoReserva
import com.servicerca.app.core.utils.generarContenidoReservaQR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceVerificationScreen(
    reservationId: String,
    onBackClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val qrContent = remember(reservationId) { generarContenidoReservaQR(reservationId) }
    val alternativeCode = remember(reservationId) { generarCodigoAlternativoReserva(reservationId) }

    Scaffold(
        topBar = {
            AppTopAppBarBack(
                title = "Verificación de servicio",
                onBackClick = onBackClick
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Service Summary Card
            ServiceSummaryCard()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "CÓDIGO DE VERIFICACIÓN",
                style = typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code Section
            QRCodeCard(qrContent = qrContent)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pide al proveedor de servicio escanear este código para confirmar que el servicio fue realizado correctamente",
                style = typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Código alternativo al QR",
                style = typography.labelMedium,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alternative Code
            AlternativeCodeField(code = alternativeCode)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ServiceSummaryCard() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service Image
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.service),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Status Tag
                Row(
                    modifier = Modifier
                        .background(colorScheme.primary.copy(alpha = 0.12f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(colorScheme.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EN PROGRESO",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Plomería Residencial",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "Carlos Ruiz",
                    style = typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hoy, 10:00",
                        style = typography.bodySmall,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceVerificationScreenPreview() {
    ServiceVerificationScreen(reservationId = "1")
}
