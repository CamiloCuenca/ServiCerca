package com.servicerca.app.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
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
    val isSuccess: Boolean = false,
    // Controla si se debe mostrar el modal de resultado (éxito o error)
    val showResultModal: Boolean = false
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
                    message = "QR inválido. Vuelve a intentarlo.",
                    showResultModal = true
                )
                return@launch
            }

            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    message = "No se encontró la reserva asociada al QR.",
                    showResultModal = true
                )
                return@launch
            }

            val sessionUserId = sessionDataStore.sessionFlow.first()?.userId
            if (sessionUserId != null && reservation.providerId != sessionUserId) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    message = "Este QR no corresponde a tus reservas.",
                    showResultModal = true
                )
                return@launch
            }

            // Operación única para evitar inconsistencias en estado local al completar/eliminar.
            // completeReservation marca el servicio como completado y elimina la reserva del estado local.
            reservationRepository.completeReservation(reservationId)

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                isSuccess = true,
                message = "El servicio fue completado exitosamente. La reserva ha sido cerrada.",
                showResultModal = true
            )
        }
    }

    // Cierra el modal de resultado; para errores también reinicia el estado para permitir otro escaneo.
    fun dismissModal() {
        val wasError = !_uiState.value.isSuccess
        if (wasError) {
            _uiState.value = ProviderVerificationUiState()
        } else {
            _uiState.value = _uiState.value.copy(showResultModal = false)
        }
    }
}
