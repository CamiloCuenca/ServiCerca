package com.servicerca.app.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val reservationRepository: ReservationRepository
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
        date: LocalDate,
        time: LocalTime,
        message: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val reservationDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val reservation = Reservation(
                    id = UUID.randomUUID().toString(),
                    serviceId = serviceId,
                    serviceTitle = serviceTitle,
                    userId = "current_user",
                    providerId = providerId,
                    date = reservationDate,
                    time = time.toString(),
                    status = ReservationStatus.PENDING,
                    message = message
                )
                reservationRepository.createReservation(reservation)
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
