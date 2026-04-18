package com.servicerca.app.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class MakeReservationUiState(
    val service: Service? = null,
    val provider: User? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MakeReservationViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository,
    private val notificationRepository: com.servicerca.app.domain.repository.NotificationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MakeReservationUiState())
    val uiState: StateFlow<MakeReservationUiState> = _uiState.asStateFlow()

    fun loadServiceDetails(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val service = serviceRepository.findById(serviceId)
                if (service != null) {
                    val provider = userRepository.findById(service.ownerId)
                    _uiState.value = _uiState.value.copy(
                        service = service,
                        provider = provider,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró el servicio"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    fun confirmReservation(
        serviceId: String,
        providerId: String,
        serviceTitle: String,
        serviceImageUrl: String?,
        date: LocalDate,
        time: LocalTime,
        message: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val session = sessionDataStore.sessionFlow.first()
                val currentUserId = session?.userId ?: "unknown_user"
                
                val reservationDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val reservation = Reservation(
                    id = UUID.randomUUID().toString(),
                    serviceId = serviceId,
                    serviceTitle = serviceTitle,
                    serviceImageUrl = serviceImageUrl,
                    userId = currentUserId,
                    providerId = providerId,
                    date = reservationDate,
                    time = time.toString(),
                    status = ReservationStatus.PENDING,
                    message = message
                )
                reservationRepository.createReservation(reservation)

                // Trigger Notification for the Provider
                val notification = com.servicerca.app.domain.model.Notification(
                    id = UUID.randomUUID().toString(),
                    userId = providerId,
                    title = "Nueva solicitud de servicio",
                    message = "Has recibido una reserva para tu servicio: $serviceTitle",
                    date = "Ahora",
                    imageRes = com.servicerca.app.R.drawable.nueva_solicitud_servicio,
                    isRead = false,
                    targetId = reservation.id,
                    notificationType = com.servicerca.app.domain.model.NotificationType.RESERVATION
                )
                notificationRepository.addNotification(notification)

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear la reserva: ${e.message}"
                )
            }
        }
    }
}
