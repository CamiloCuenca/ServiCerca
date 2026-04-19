package com.servicerca.app.ui.reservation.details

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.Map.MapBox
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardDetailsReservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.ui.reservation.ConfirmActionModal

@Composable
fun DetailsReservationScreen(
    reservationId: String,
    viewModel: ReservationDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onQr: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteModal by remember { mutableStateOf(false) }

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.reservation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró la reserva")
        }
    } else {
        val reservation = uiState.reservation!!
        val service = uiState.service
        val provider = uiState.provider
        val customer = uiState.customer

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Detalles de la Reserva",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val statusInfo = getStatusInfo(reservation.status)

            CardDetailsReservation(
                serviceImageUrl = reservation.serviceImageUrl ?: service?.photoUrl,
                profileImageUrl = provider?.profilePictureUrl,
                serviceRequestedLabel = stringResource(R.string.reservation_servicio_solicitado),
                statusText = statusInfo.text,
                statusContainerColor = statusInfo.containerColor,
                statusContentColor = statusInfo.contentColor,
                serviceTitle = reservation.serviceTitle,
                professionalName = if (provider != null) "${provider.name1} ${provider.lastname1}" else "Cargando...",
                professionalBadgeText = stringResource(R.string.reservation_profesional_certificado),
                rating = "4.9"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 🔹 SECTION TITLE
            Text(
                text = "INFORMACIÓN",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    InfoItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Fecha y hora",
                        value = reservation.time
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    InfoItem(
                        icon = Icons.Default.LocationOn,
                        label = "Ubicación",
                        value = customer?.city ?: "Ubicación no disponible"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    EstimatedCostRow(
                        label = "Costo Estimado",
                        value = if (service != null) "$${service.priceMin} - $${service.priceMax}" else "$0.00"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Chatear con el Profesional",
                onClick = {
                    viewModel.onContactProfessional { chatId ->
                        onNavigateToChat(chatId)
                    }
                }
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (reservation.status != ReservationStatus.CANCELLED) {
                PrimaryButton(
                    text = "Terminar Servicio",
                    onClick = { onQr() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DeleteButton(
                    text = "Cancelar Reserva",
                    onClick = { showDeleteModal = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // MODAL DE CONFIRMACIÓN
        if (showDeleteModal) {
            ConfirmActionModal(
                onDismiss = { showDeleteModal = false },
                onConfirm = {
                    viewModel.cancelReservation(reservation.id) {
                        showDeleteModal = false
                    }
                },
                title = "¿Cancelar Reserva?",
                textPrimary = "Mantener mi Reserva",
                textSecondary = "Cancelar Reserva"
            )
        }
    }
}

data class StatusUIInfo(
    val text: String,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun getStatusInfo(status: ReservationStatus): StatusUIInfo {
    return when (status) {
        ReservationStatus.CONFIRMED -> StatusUIInfo(
            stringResource(R.string.reservation_status_confirmed),
            Color(0xFFD1FADF),
            Color(0xFF027A48)
        )

        ReservationStatus.PENDING -> StatusUIInfo(
            "Pendiente",
            Color(0xFFFEF0C7),
            Color(0xFFB54708)
        )

        ReservationStatus.CANCELLED -> StatusUIInfo(
            "Cancelada",
            Color(0xFFFEE4E2),
            Color(0xFFB42318)
        )

        ReservationStatus.COMPLETED -> StatusUIInfo(
            "Completada",
            Color(0xFFD1E9FF),
            Color(0xFF175CD3)
        )
    }
}


@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EstimatedCostRow(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
