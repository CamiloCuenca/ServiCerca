package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.ai.ToxicityRepository
import com.servicerca.app.core.fcm.FCMSender
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val userRepository: UserRepository,
    private val fcmSender: FCMSender
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
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "El motivo no puede estar vacío") }
            return
        }

        val currentService = _uiState.value.service ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                if (ToxicityRepository.isToxic(reason)) {
                    _uiState.update {
                        it.copy(
                            error = "Contenido ofensivo detectado en el motivo",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val updatedService = currentService.copy(status = ServiceStatus.REJECTED)
                serviceRepository.update(updatedService)

                val title = "Servicio rechazado"
                val message = "Tu servicio \"${currentService.title}\" ha sido rechazado. Motivo: $reason"
                val ownerToken = userRepository.findById(currentService.ownerId)?.fcmToken
                if (!ownerToken.isNullOrBlank()) {
                    fcmSender.sendGeneralNotification(
                        recipientToken = ownerToken,
                        title = title,
                        body = message,
                        type = "rejection",
                        notificationType = "MODERATION",
                        targetId = currentService.id,
                        userId = currentService.ownerId,
                        alreadySavedInFirestore = false
                    )
                }

                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        service = updatedService,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
