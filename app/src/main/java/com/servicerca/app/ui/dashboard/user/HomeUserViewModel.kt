package com.servicerca.app.ui.dashboard.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.LevelUtils
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Categories
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.core.fcm.FCMSender
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class HomeSort(val label: String) {
    RECENT("Recientes"),
    CHEAPEST("Más económicos"),
    BEST_RATED("Mejor valorados")
}

data class HomeFilters(
    val sort: HomeSort = HomeSort.RECENT,
    val maxPrice: Float = 0f  // 0 = sin límite
)

data class ServiceWithRating(
    val service: Service,
    val averageRating: Double,
    val ownerLevel: String,
    val isBookmarked: Boolean,
    val likeCount: Int,
    val isLiked: Boolean,
    val isOwner: Boolean
)

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore,
    private val fcmSender: FCMSender
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Categories?>(null)
    val selectedCategory: StateFlow<Categories?> = _selectedCategory.asStateFlow()

    private val _homeFilters = MutableStateFlow(HomeFilters())
    val homeFilters: StateFlow<HomeFilters> = _homeFilters.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Todos los servicios aprobados con calificación calculada
    private val allServices: StateFlow<List<ServiceWithRating>> = combine(
        serviceRepository.services,
        commentRepository.comments,
        userRepository.users,
        sessionDataStore.sessionFlow
    ) { allServices, allComments, allUsers, session ->
        val currentUser = allUsers.firstOrNull { it.id == session?.userId }
        val interestingIds = currentUser?.listInteresting?.toSet().orEmpty()

        allServices
            .filter { it.status == ServiceStatus.APPROVED }
            .map { service ->
                val serviceComments = allComments.filter { it.serviceId == service.id }
                val avg = if (serviceComments.isNotEmpty()) {
                    serviceComments.map { it.rating }.average()
                } else {
                    0.0
                }
                val ownerServicesIds = allServices.filter { it.ownerId == service.ownerId }.map { it.id }
                val ownerComments = allComments.filter { it.serviceId in ownerServicesIds }
                val totalXp = ownerComments.sumOf { (it.rating * 50).toInt() }
                val ownerLevel = LevelUtils.getLevelName(totalXp)

                ServiceWithRating(
                    service = service,
                    averageRating = avg,
                    ownerLevel = ownerLevel,
                    isBookmarked = service.id in interestingIds,
                    likeCount = service.likedBy.size,
                    isLiked = service.likedBy.contains(session?.userId),
                    isOwner = service.ownerId == session?.userId
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Feed final con filtros aplicados
    val services: StateFlow<List<ServiceWithRating>> = combine(
        allServices,
        _selectedCategory,
        _homeFilters
    ) { list, category, filters ->
        var result = list
            .filter { if (category != null) category.matchesType(it.service.type) else true }
            .filter { if (filters.maxPrice > 0f) it.service.priceMin <= filters.maxPrice else true }

        result = when (filters.sort) {
            HomeSort.RECENT -> result
            HomeSort.CHEAPEST -> result.sortedBy { it.service.priceMin }
            HomeSort.BEST_RATED -> result.sortedByDescending { it.averageRating }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: Categories) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun updateFilters(filters: HomeFilters) {
        _homeFilters.value = filters
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(700)
            _isRefreshing.value = false
        }
    }

    fun onBookmarkClick(serviceId: String) {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            userRepository.toggleInterestingService(
                userId = session.userId,
                serviceId = serviceId
            )
        }
    }

    fun onLikeClick(serviceId: String) {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            val service = serviceRepository.findById(serviceId) ?: return@launch
            val currentUser = userRepository.findById(session.userId) ?: return@launch

            val isAddingLike = !service.likedBy.contains(session.userId)

            serviceRepository.toggleLike(
                serviceId = serviceId,
                userId = session.userId
            )

            if (isAddingLike) {
                val title = "¡Nuevo like!"
                val message = "${currentUser.name1} le dio like a tu servicio \"${service.title}\""
                notificationRepository.addNotification(
                    Notification(
                        id = UUID.randomUUID().toString(),
                        userId = service.ownerId,
                        title = title,
                        message = message,
                        date = "Ahora",
                        imageRes = R.drawable.insignia_favorita,
                        isRead = false
                    )
                )
                val owner = userRepository.findById(service.ownerId)
                if (!owner?.fcmToken.isNullOrBlank()) {
                    fcmSender.sendGeneralNotification(
                        recipientToken = owner!!.fcmToken,
                        title = title,
                        body = message,
                        type = "like"
                    )
                }
            }
        }
    }
}
