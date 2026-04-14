package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.R
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class RejectReasonUiState(
    val service: Service? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RejectReasonViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RejectReasonUiState())
    val uiState = _uiState.asStateFlow()

    fun loadService(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val service = serviceRepository.findById(id)
            _uiState.update { it.copy(service = service, isLoading = false) }
        }
    }

    fun rejectService(reason: String) {
        val currentService = _uiState.value.service ?: return
        viewModelScope.launch {
            // In a real app, we might want to save the reason somewhere
            val updatedService = currentService.copy(status = ServiceStatus.REJECTED)
            serviceRepository.update(updatedService)

            // Enviar notificación con el motivo del rechazo
            notificationRepository.addNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = currentService.ownerId, // Asociar al dueño del servicio
                    title = "Servicio rechazado",
                    message = "Tu servicio \"${currentService.title}\" ha sido rechazado. Motivo: $reason",
                    date = "Ahora",
                    imageRes = R.drawable.publicacion_rechazada,
                    isRead = false
                )
            )

            _uiState.update { it.copy(isSuccess = true, service = updatedService) }
        }
    }
}
