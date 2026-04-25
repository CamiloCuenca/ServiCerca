package com.servicerca.app.ui.reservation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.R
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val sessionDataStore: SessionDataStore,
    @ApplicationContext private val context: Context
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
                        error = context.getString(R.string.error_service_not_found)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = context.getString(R.string.error_loading_data, e.message ?: "")
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

                // Validar que no haya una reserva activa (PENDING o CONFIRMED) para el mismo servicio en la misma fecha
                val userReservations = reservationRepository.getReservationsByUser(currentUserId).first()
                val hasActiveReservation = userReservations.any {
                    it.serviceId == serviceId &&
                    (it.status == ReservationStatus.PENDING || it.status == ReservationStatus.CONFIRMED) &&
                    it.date.time == reservationDate.time
                }

                if (hasActiveReservation) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Ya tienes una reserva activa para este servicio en esta fecha. Selecciona otra fecha u otro servicio."
                    )
                    return@launch
                }

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
                    title = context.getString(R.string.new_service_request_title),
                    message = context.getString(R.string.new_reservation_received_message, serviceTitle),
                    date = context.getString(R.string.now_label),
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
                    error = context.getString(R.string.error_creating_reservation, e.message ?: "")
                )
            }
        }
    }
}
