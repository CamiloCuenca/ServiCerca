package com.servicerca.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val averageRating: Double = 0.0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        observeServiceCounts()
        observeAverageRating()
    }

    private fun observeAverageRating() {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            val userId = session.userId

            combine(
                serviceRepository.services,
                commentRepository.comments
            ) { services, allComments ->
                val myServiceIds = services.filter { it.ownerId == userId }.map { it.id }
                val myComments = allComments.filter { it.serviceId in myServiceIds }

                if (myComments.isNotEmpty()) {
                    myComments.map { it.rating }.average()
                } else {
                    0.0
                }
            }.collect { avg ->
                _uiState.value = _uiState.value.copy(averageRating = avg)
            }
        }
    }

    private fun observeServiceCounts() {
        viewModelScope.launch {
            serviceRepository.services.collectLatest { services ->
                _uiState.value = _uiState.value.copy(
                    pendingCount = services.count { it.status == ServiceStatus.PENDING },
                    approvedCount = services.count { it.status == ServiceStatus.APPROVED },
                    rejectedCount = services.count { it.status == ServiceStatus.REJECTED }
                )
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                if (session != null) {
                    val user = userRepository.findById(session.userId)
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Sesión no encontrada"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }
}
