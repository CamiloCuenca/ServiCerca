package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.model.User
import com.servicerca.app.R
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class DetailsVerificationUiState(
    val service: Service? = null,
    val owner: User? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailsVerificationViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsVerificationUiState())
    val uiState = _uiState.asStateFlow()

    fun loadService(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val service = serviceRepository.findById(id)
            val owner = service?.let { userRepository.findById(it.ownerId) }
            
            _uiState.update { it.copy(
                service = service,
                owner = owner,
                isLoading = false
            ) }
        }
    }

    fun approveService() {
        val currentService = _uiState.value.service ?: return
        viewModelScope.launch {
            val updatedService = currentService.copy(status = ServiceStatus.APPROVED)
            serviceRepository.update(updatedService)
            
            // Enviar notificación al dueño
            notificationRepository.addNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = currentService.ownerId, // Asociar al dueño del servicio
                    title = "Servicio aprobado",
                    message = "¡Tu servicio \"${currentService.title}\" ha sido aprobado y ya es público!",
                    date = "Ahora",
                    imageRes = R.drawable.nueva_publicacion,
                    isRead = false
                )
            )

            _uiState.update { it.copy(isSuccess = true, service = updatedService) }
        }
    }
}
