package com.servicerca.app.ui.dashboard.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.LevelUtils
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceWithRating(
    val service: Service,
    val averageRating: Double,
    val ownerLevel: String,
    val isBookmarked: Boolean
)

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // Obtenemos los servicios APROBADOS con su calificación específica y estado de bookmark del usuario actual
    val services: StateFlow<List<ServiceWithRating>> = combine(
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
                    isBookmarked = service.id in interestingIds
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onBookmarkClick(serviceId: String) {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            userRepository.toggleInterestingService(
                userId = session.userId,
                serviceId = serviceId
            )
        }
    }
}