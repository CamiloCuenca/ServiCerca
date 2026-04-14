package com.servicerca.app.ui.services.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.data.datastore.SessionDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _service = MutableStateFlow<Service?>(null)
    val service: StateFlow<Service?> = _service.asStateFlow()

    private val _provider = MutableStateFlow<User?>(null)
    val provider: StateFlow<User?> = _provider.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()

    fun loadService(serviceId: String) {
        viewModelScope.launch {
            val serviceResult = serviceRepository.findById(serviceId)
            _service.value = serviceResult
            
            if (serviceResult != null) {
                _provider.value = userRepository.findById(serviceResult.ownerId)
                loadComments(serviceId, serviceResult.ownerId)
            }
        }
    }

    private fun loadComments(serviceId: String, ownerId: String) {
        val allComments = commentRepository.comments.value
        val allServices = serviceRepository.services.value

        // Comentarios específicos de este servicio
        val serviceComments = allComments.filter { it.serviceId == serviceId }
        _comments.value = serviceComments

        // Calificación promedio basada en TODOS los servicios del proveedor
        val providerServiceIds = allServices.filter { it.ownerId == ownerId }.map { it.id }
        val providerComments = allComments.filter { it.serviceId in providerServiceIds }

        _averageRating.value = if (providerComments.isEmpty()) 0f
        else providerComments.map { it.rating }.average().toFloat()
    }

    fun addComment(
        rating: Int,
        text: String
    ) {
        val service = _service.value ?: return
        val serviceId = service.id
        val ownerId = service.ownerId
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull()
            if (session != null) {
                val currentUser = userRepository.findById(session.userId)
                if (currentUser != null) {
                    val fallbackAvatar = "https://picsum.photos/200?random=${currentUser.id.hashCode() % 100}"
                    val comment = Comment(
                        id = UUID.randomUUID().toString(),
                        userId = currentUser.id,
                        serviceId = serviceId,
                        userName = "${currentUser.name1} ${currentUser.lastname1}",
                        userAvatar = currentUser.profilePictureUrl.ifEmpty { fallbackAvatar },
                        rating = rating,
                        text = text,
                        date = System.currentTimeMillis(),
                        timeAgo = "Ahora"
                    )
                    commentRepository.save(comment)
                    
                    // Enviar notificación al dueño del servicio
                    val notification = Notification(
                        id = UUID.randomUUID().toString(),
                        userId = ownerId,
                        title = "Nuevo comentario recibido",
                        message = "${currentUser.name1} ha comentado: \"$text\" y te dio $rating estrellas.",
                        date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                        imageRes = com.servicerca.app.R.drawable.insignia_chat, // O un icono adecuado
                        isRead = false
                    )
                    notificationRepository.addNotification(notification)

                    loadComments(serviceId, ownerId)
                }
            }
        }
    }

    fun deleteComment(commentId: String) {
        val service = _service.value ?: return
        val serviceId = service.id
        val ownerId = service.ownerId
        viewModelScope.launch {
            commentRepository.delete(commentId)
            loadComments(serviceId, ownerId)
        }
    }
}
