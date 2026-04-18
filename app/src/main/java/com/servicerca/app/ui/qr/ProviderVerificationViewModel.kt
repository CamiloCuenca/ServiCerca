package com.servicerca.app.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.core.utils.leerReservaIdDesdeQR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderVerificationUiState(
    val scannedValue: String = "Escaneando...",
    val message: String = "",
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ProviderVerificationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProviderVerificationUiState())
    val uiState: StateFlow<ProviderVerificationUiState> = _uiState.asStateFlow()

    fun onQrDetected(rawValue: String) {
        if (_uiState.value.isProcessing || _uiState.value.isSuccess) return

        _uiState.value = _uiState.value.copy(
            scannedValue = rawValue,
            isProcessing = true,
            message = ""
        )

        viewModelScope.launch {
            val reservationId = leerReservaIdDesdeQR(rawValue)
            if (reservationId == null) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    message = "QR inválido. Vuelve a intentarlo."
                )
                return@launch
            }

            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    message = "No se encontró la reserva asociada al QR."
                )
                return@launch
            }

            val sessionUserId = sessionDataStore.sessionFlow.first()?.userId
            if (sessionUserId != null && reservation.providerId != sessionUserId) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    message = "Este QR no corresponde a tus reservas."
                )
                return@launch
            }

            // Marcamos como completado y luego eliminamos la reserva para ambos roles (estado local).
            reservationRepository.updateReservationStatus(reservationId, ReservationStatus.COMPLETED.name)
            reservationRepository.deleteReservation(reservationId)

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                isSuccess = true,
                message = "Servicio confirmado y reserva eliminada correctamente."
            )
        }
    }
}
