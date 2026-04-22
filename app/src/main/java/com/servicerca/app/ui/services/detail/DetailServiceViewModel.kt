package com.servicerca.app.ui.services.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.LevelUtils
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import com.servicerca.app.R

@HiltViewModel
class DetailServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _serviceId = MutableStateFlow<String?>(null)

    val service: StateFlow<Service?> = combine(
        _serviceId,
        serviceRepository.services
    ) { id, allServices ->
        allServices.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val provider: StateFlow<User?> = combine(
        service,
        userRepository.users
    ) { s, allUsers ->
        allUsers.find { it.id == s?.ownerId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val comments: StateFlow<List<Comment>> = combine(
        _serviceId,
        commentRepository.comments
    ) { id, allComments ->
        allComments.filter { it.serviceId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val averageRating: StateFlow<Float> = combine(
        service,
        commentRepository.comments
    ) { s, allComments ->
        if (s == null) return@combine 0f
        val serviceComments = allComments.filter { it.serviceId == s.id }
        if (serviceComments.isEmpty()) 0f
        else serviceComments.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val providerAverageRating: StateFlow<Float> = combine(
        service,
        commentRepository.comments,
        serviceRepository.services
    ) { s, allComments, allServices ->
        if (s == null) return@combine 0f
        val providerServiceIds = allServices.filter { it.ownerId == s.ownerId }.map { it.id }
        val providerComments = allComments.filter { it.serviceId in providerServiceIds }
        if (providerComments.isEmpty()) 0f
        else providerComments.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val providerCommentCount: StateFlow<Int> = combine(
        service,
        commentRepository.comments,
        serviceRepository.services
    ) { s, allComments, allServices ->
        if (s == null) return@combine 0
        val providerServiceIds = allServices.filter { it.ownerId == s.ownerId }.map { it.id }
        allComments.count { it.serviceId in providerServiceIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val providerLevel: StateFlow<String> = combine(
        service,
        commentRepository.comments,
        serviceRepository.services
    ) { s, allComments, allServices ->
        if (s == null) return@combine "Principiante"
        val providerServiceIds = allServices.filter { it.ownerId == s.ownerId }.map { it.id }
        val providerComments = allComments.filter { it.serviceId in providerServiceIds }
        val totalXp = providerComments.sumOf { (it.rating * 50).toInt() }
        LevelUtils.getLevelName(totalXp)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Principiante")

    val isBookmarked: StateFlow<Boolean> = combine(
        _serviceId,
        userRepository.users,
        sessionDataStore.sessionFlow
    ) { id, users, session ->
        val currentUser = users.find { it.id == session?.userId }
        id != null && id in (currentUser?.listInteresting ?: emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isLiked: StateFlow<Boolean> = combine(
        service,
        sessionDataStore.sessionFlow
    ) { s, session ->
        s?.likedBy?.contains(session?.userId) ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val likeCount: StateFlow<Int> = service.map {
        it?.likedBy?.size ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun loadService(serviceId: String) {
        _serviceId.value = serviceId
    }

    fun addComment(
        rating: Int,
        text: String
    ) {
        val s = service.value ?: return
        val serviceId = s.id
        val ownerId = s.ownerId
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
                    
                    // Actualizar puntos y rating del proveedor
                    val providerId = ownerId
                    val providerUser = userRepository.findById(providerId)
                    if (providerUser != null) {
                        val allComments = commentRepository.comments.value
                        val allServices = serviceRepository.services.value
                        
                        val providerServiceIds = allServices.filter { it.ownerId == providerId }.map { it.id }
                        val providerComments = allComments.filter { it.serviceId in providerServiceIds }
                        
                        val newAvg = if (providerComments.isEmpty()) rating.toDouble() 
                                    else providerComments.map { it.rating }.average()
                        
                        val xpGained = rating * 50
                        
                        val updatedProvider = providerUser.copy(
                            rating = newAvg,
                            totalPoints = providerUser.totalPoints + xpGained,
                            completedServices = providerUser.completedServices + 1
                        )
                        userRepository.save(updatedProvider)
                    }
                    
                    // Enviar notificación al dueño del servicio
                    val context = sessionDataStore.context
                    val title = context.getString(com.servicerca.app.R.string.new_comment_title)
                    val message = context.getString(
                        com.servicerca.app.R.string.new_comment_message_format,
                        currentUser.name1,
                        text,
                        rating
                    )

                    val notification = Notification(
                        id = UUID.randomUUID().toString(),
                        userId = ownerId,
                        title = title,
                        message = message,
                        date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                        imageRes = com.servicerca.app.R.drawable.insignia_chat,
                        isRead = false
                    )
                    notificationRepository.addNotification(notification)
                }
            }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            commentRepository.delete(commentId)
        }
    }

    fun onBookmarkClick() {
        val serviceId = _serviceId.value ?: return
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            val currentUser = userRepository.findById(session.userId) ?: return@launch
            val s = service.value ?: return@launch

            val isAddingBookmark = !currentUser.listInteresting.contains(serviceId)

            userRepository.toggleInterestingService(
                userId = session.userId,
                serviceId = serviceId
            )

            if (isAddingBookmark) {
                notificationRepository.addNotification(
                    Notification(
                        id = UUID.randomUUID().toString(),
                        userId = s.ownerId,
                        title = "¡Interés en tu servicio!",
                        message = "${currentUser.name1} guardó tu servicio \"${s.title}\" como interesante",
                        date = "Ahora",
                        imageRes = R.drawable.insignia_favorita,
                        isRead = false
                    )
                )
            }
        }
    }

    fun onLikeClick() {
        val serviceId = _serviceId.value ?: return
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            val s = service.value ?: return@launch
            val currentUser = userRepository.findById(session.userId) ?: return@launch

            val isAddingLike = !s.likedBy.contains(session.userId)

            serviceRepository.toggleLike(
                serviceId = serviceId,
                userId = session.userId
            )

            if (isAddingLike) {
                notificationRepository.addNotification(
                    Notification(
                        id = UUID.randomUUID().toString(),
                        userId = s.ownerId,
                        title = "¡Nuevo like!",
                        message = "${currentUser.name1} le dio like a tu servicio \"${s.title}\"",
                        date = "Ahora",
                        imageRes = R.drawable.insignia_favorita,
                        isRead = false
                    )
                )
            }
        }
    }
}
