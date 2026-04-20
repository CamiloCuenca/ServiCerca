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
import androidx.compose.foundation.shape.CircleShape
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
    var showRejectModal by remember { mutableStateOf(false) }

    LaunchedEffect(reservationId) {
        viewModel.loadReservation(reservationId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.reservation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.reservation_not_found))
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

            // 🔹 Top Bar (Modern minimalist style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.reservation_details_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val statusInfo = getStatusInfo(reservation.status)

            // 🔹 Lógica de Rol: ¿Quién es el protagonista de la tarjeta?
            val targetUser = if (uiState.isProvider) customer else provider
            val roleLabel = if (uiState.isProvider) stringResource(R.string.role_client) else stringResource(R.string.reservation_profesional_certificado)

            CardDetailsReservation(
                serviceImageUrl = reservation.serviceImageUrl ?: service?.photoUrl,
                profileImageUrl = targetUser?.profilePictureUrl,
                serviceRequestedLabel = stringResource(R.string.reservation_servicio_solicitado),
                statusText = statusInfo.text,
                statusContainerColor = statusInfo.containerColor,
                statusContentColor = statusInfo.contentColor,
                serviceTitle = reservation.serviceTitle,
                professionalName = if (targetUser != null) "${targetUser.name1} ${targetUser.lastname1}" else stringResource(R.string.loading_text),
                professionalBadgeText = roleLabel,
                rating = if (uiState.isProvider) null else "4.9", // No mostramos el rating del propio cliente si somos proveedores
                modifier = Modifier.padding(horizontal = 0.dp) // Reset padding inside Column
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 🔹 SECTION: INFORMACIÓN DETALLADA
            Text(
                text = stringResource(R.string.service_summary_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    InfoItem(
                        icon = Icons.Default.CalendarMonth,
                        label = stringResource(R.string.agreed_date_time_label),
                        value = reservation.time
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    InfoItem(
                        icon = Icons.Default.LocationOn,
                        label = stringResource(R.string.meeting_address_label),
                        value = customer?.city ?: stringResource(R.string.location_not_available)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 🔹 Mapa con estilo premium
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    ) {
                        MapBox(modifier = Modifier.fillMaxSize())
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    EstimatedCostRow(
                        label = stringResource(R.string.estimated_cost_label),
                        value = if (service != null) "$${service.priceMin} - $${service.priceMax}" else "$0.00"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 BOTÓN DE CHAT (Ahora más integrado)
            PrimaryButton(
                text = if (uiState.isProvider) stringResource(R.string.chat_with_client) else stringResource(R.string.chat_with_professional),
                onClick = {
                    viewModel.onContactProfessional { chatId ->
                        onNavigateToChat(chatId)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 ACCIONES DEPENDIENDO DEL ESTADO Y ROL
            if (uiState.isProvider && reservation.status == ReservationStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DeleteButton(
                        text = stringResource(R.string.reject_action),
                        onClick = { showRejectModal = true },
                        modifier = Modifier.weight(1f),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )

                    PrimaryButton(
                        text = stringResource(R.string.accept_action),
                        onClick = { viewModel.acceptReservation(reservation.id) },
                        modifier = Modifier.weight(1.2f) // Un poco más de peso al botón principal
                    )
                }
            } else if (reservation.status != ReservationStatus.CANCELLED && reservation.status != ReservationStatus.REJECTED) {

                // Si es confirmada pero no terminada, mostramos botón de terminar si es necesario
                if (reservation.status == ReservationStatus.CONFIRMED) {
                    PrimaryButton(
                        text = stringResource(R.string.finish_service_action),
                        onClick = { onQr() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                DeleteButton(
                    text = stringResource(R.string.cancel_reservation_action),
                    onClick = { showDeleteModal = true },
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // MODAL DE CONFIRMACIÓN - CANCELAR (CLIENTE)
        if (showDeleteModal) {
            ConfirmActionModal(
                onDismiss = { showDeleteModal = false },
                onConfirm = {
                    viewModel.cancelReservation(reservation.id) {
                        showDeleteModal = false
                    }
                },
                title = stringResource(R.string.cancel_reservation_confirm_title),
                textPrimary = stringResource(R.string.keep_my_reservation_action),
                textSecondary = stringResource(R.string.cancel_reservation_action)
            )
        }

        // MODAL DE CONFIRMACIÓN - RECHAZAR (PROVEEDOR)
        if (showRejectModal) {
            ConfirmActionModal(
                onDismiss = { showRejectModal = false },
                onConfirm = {
                    viewModel.rejectReservation(reservation.id)
                    showRejectModal = false
                },
                title = stringResource(R.string.reject_request_confirm_title),
                textPrimary = stringResource(R.string.action_back),
                textSecondary = stringResource(R.string.reject_request_action)
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
            stringResource(R.string.status_pending_label),
            Color(0xFFFEF0C7),
            Color(0xFFB54708)
        )

        ReservationStatus.CANCELLED -> StatusUIInfo(
            stringResource(R.string.status_cancelled_label),
            Color(0xFFFEE4E2),
            Color(0xFFB42318)
        )
        ReservationStatus.REJECTED -> StatusUIInfo(
            stringResource(R.string.status_rejected_label),
            Color(0xFFFEE4E2),
            Color(0xFFD92D20)
        )
        ReservationStatus.COMPLETED -> StatusUIInfo(
            stringResource(R.string.status_completed_label),
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
