package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.R
import com.servicerca.app.core.fcm.FCMSender
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.NotificationType
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class DetailsVerificationUiState(
    val service: Service? = null,
    val owner: User? = null,
    val ownerRating: Float = 0f,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailsVerificationViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    private val fcmSender: FCMSender
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsVerificationUiState())
    val uiState = _uiState.asStateFlow()

    fun loadService(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val service = serviceRepository.findById(id)
            val owner = service?.let { userRepository.findById(it.ownerId) }

            if (owner != null) {
                combine(
                    serviceRepository.services,
                    commentRepository.comments
                ) { allServices, allComments ->
                    val providerServiceIds = allServices.filter { it.ownerId == owner.id }.map { it.id }
                    val providerComments = allComments.filter { it.serviceId in providerServiceIds }
                    if (providerComments.isEmpty()) 0f
                    else providerComments.map { it.rating }.average().toFloat()
                }.collect { rating ->
                    _uiState.update {
                        it.copy(
                            service = service,
                            owner = owner,
                            ownerRating = rating,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        service = service,
                        owner = owner,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun approveService() {
        val currentService = _uiState.value.service ?: return
        viewModelScope.launch {
            val updatedService = currentService.copy(status = ServiceStatus.APPROVED)
            serviceRepository.update(updatedService)

            notificationRepository.deleteNotificationsByTargetId(currentService.id)

            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            val title = "Servicio aprobado"
            val message = "¡Tu servicio \"${currentService.title}\" ha sido aprobado y ya es público!"

            notificationRepository.addNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = currentService.ownerId,
                    title = title,
                    message = message,
                    date = dateStr,
                    imageRes = R.drawable.servicio_verificado,
                    isRead = false,
                    targetId = currentService.id,
                    notificationType = NotificationType.SERVICE
                )
            )

            val ownerToken = _uiState.value.owner?.fcmToken
            if (!ownerToken.isNullOrBlank()) {
                fcmSender.sendGeneralNotification(
                    recipientToken = ownerToken,
                    title = title,
                    body = message,
                    type = "moderation",
                    notificationType = "MODERATION",
                    targetId = currentService.id,
                    userId = currentService.ownerId
                )
            }

            _uiState.update { it.copy(isSuccess = true, service = updatedService) }
        }
    }
}
