package com.servicerca.app.ui.reservation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ReservationDetailsUiState(
    val reservation: Reservation? = null,
    val service: Service? = null,
    val provider: User? = null,
    val customer: User? = null,
    val isProvider: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ReservationDetailsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailsUiState())
    val uiState: StateFlow<ReservationDetailsUiState> = _uiState.asStateFlow()

    fun loadReservation(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val session = sessionDataStore.sessionFlow
                .filterNotNull()
                .first()
            val currentUserId = session.userId
            
            val reservation = reservationRepository.getReservationById(id)
            if (reservation != null) {
                val service = serviceRepository.findById(reservation.serviceId)
                val provider = userRepository.findById(reservation.providerId)
                val customer = userRepository.findById(reservation.userId)
                
                _uiState.value = _uiState.value.copy(
                    reservation = reservation,
                    service = service,
                    provider = provider,
                    customer = customer,
                    isProvider = reservation.providerId == currentUserId,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun acceptReservation(id: String) {
        viewModelScope.launch {
            val currentReservation = _uiState.value.reservation ?: return@launch
            reservationRepository.updateReservationStatus(id, ReservationStatus.CONFIRMED.name)
            
            // Notificar al cliente
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = currentReservation.userId,
                title = "Reserva Aceptada",
                message = "¡Buenas noticias! Tu reserva para '${currentReservation.serviceTitle}' ha sido aceptada.",
                date = "Ahora",
                imageRes = com.servicerca.app.R.drawable.nueva_solicitud_servicio,
                isRead = false,
                targetId = id,
                notificationType = com.servicerca.app.domain.model.NotificationType.RESERVATION
            )
            notificationRepository.addNotification(notification)
            
            loadReservation(id)
        }
    }

    fun rejectReservation(id: String) {
        viewModelScope.launch {
            val currentReservation = _uiState.value.reservation ?: return@launch
            reservationRepository.updateReservationStatus(id, ReservationStatus.REJECTED.name)
            
            // Notificar al cliente
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = currentReservation.userId,
                title = "Reserva Rechazada",
                message = "Lo sentimos, tu reserva para '${currentReservation.serviceTitle}' no ha podido ser aceptada.",
                date = "Ahora",
                imageRes = com.servicerca.app.R.drawable.publicacion_rechazada,
                isRead = false,
                targetId = id,
                notificationType = com.servicerca.app.domain.model.NotificationType.RESERVATION
            )
            notificationRepository.addNotification(notification)
            
            loadReservation(id)
        }
    }

    fun cancelReservation(id: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            reservationRepository.updateReservationStatus(id, "CANCELLED")
            loadReservation(id)
            onComplete()
        }
    }
}
