package com.servicerca.app.ui.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.ConfirmServiceSummaryCard
import com.servicerca.app.core.components.navigation.AppTopAppBarBack
import com.servicerca.app.core.components.qr.QRScannerPlaceholder
import com.servicerca.app.ui.theme.ServiCercaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderVerificationScreen(
    onBackClick: () -> Unit = {},
    onScanClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
        topBar = {
            AppTopAppBarBack(
                title = "Confirmar servicio",
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

            // Provider Service Summary Card
            ConfirmServiceSummaryCard()

            Spacer(modifier = Modifier.height(40.dp))

            // QR Scanner Area Placeholder
            QRScannerPlaceholder()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Escanear código QR",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Escanea el código QR que te mostrará el cliente para confirmar que el servicio fue completado satisfactoriamente.",
                style = typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Confirmation Code Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Codigo de confirmación",
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Reusing AppTextField or similar structure if exists, falling back to OutlinedTextField for exact visual match
                OutlinedTextField(
                    value = "12345678",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    textStyle = typography.bodyLarge.copy(color = colorScheme.onSurface)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Scan Button
            PrimaryButton(
                text = "Escanear QR",
                onClick = onScanClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Success pill
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE8F5E9), // Light green background
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50), // Green
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Servicio confirmado correctamente",
                    style = typography.labelSmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProviderVerificationScreenPreview() {
    ServiCercaTheme {
        ProviderVerificationScreen()
    }
}
