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
    val averageRating: Double = 0.0,
    val totalXp: Int = 0,
    val level: Int = 1,
    val xpInLevel: Int = 0,
    val xpRequiredForNextLevel: Int = 500,
    val levelName: String = "Principiante",
    val progress: Float = 0f
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

                val avg = if (myComments.isNotEmpty()) {
                    myComments.map { it.rating }.average()
                } else {
                    0.0
                }

                // Cálculo de XP basado en comentarios
                // Cada estrella son 50 puntos: 1* -> 50, 2* -> 100, ..., 5* -> 250
                val totalXp = myComments.sumOf { it.rating * 50 }
                calculateLevelState(totalXp, avg)
            }.collect { newState ->
                _uiState.value = _uiState.value.copy(
                    averageRating = newState.averageRating,
                    totalXp = newState.totalXp,
                    level = newState.level,
                    xpInLevel = newState.xpInLevel,
                    xpRequiredForNextLevel = newState.xpRequiredForNextLevel,
                    levelName = newState.levelName,
                    progress = newState.progress
                )
            }
        }
    }

    private fun calculateLevelState(totalXp: Int, avgRating: Double): ProfileUiState {
        // Niveles: 1 -> 500, 2 -> 1300, 3 -> 2500, 4 -> 4300, 5 -> 7000
        val levels = listOf(500, 1300, 2500, 4300, 7000)
        val levelNames = listOf("Principiante", "Colaborador", "Confiable", "Profesional local", "Experto local")
        
        var currentLevel = 1
        var xpRequiredForCurrent = 0
        var xpRequiredForNext = levels[0]
        
        for (i in levels.indices) {
            if (totalXp >= levels[i]) {
                currentLevel = i + 2 // Si supera 500 es Nivel 2, etc.
                xpRequiredForCurrent = levels[i]
                if (i + 1 < levels.size) {
                    xpRequiredForNext = levels[i + 1]
                } else {
                    // Nivel máximo alcanzado
                    xpRequiredForNext = levels.last()
                }
            } else {
                xpRequiredForNext = levels[i]
                break
            }
        }

        val clampedLevel = currentLevel.coerceAtMost(5)
        val levelName = levelNames[(clampedLevel - 1).coerceIn(0, 4)]
        
        val xpInLevel = if (totalXp >= levels.last()) {
            levels.last() // Tope en el máximo
        } else {
            totalXp - xpRequiredForCurrent
        }
        
        val range = xpRequiredForNext - xpRequiredForCurrent
        val progress = if (totalXp >= levels.last()) {
            1.0f
        } else {
            (totalXp - xpRequiredForCurrent).toFloat() / range.toFloat()
        }

        return ProfileUiState(
            averageRating = avgRating,
            totalXp = totalXp,
            level = clampedLevel,
            xpInLevel = totalXp, // Mostramos el XP total o el relativo según prefieras, aquí el total
            xpRequiredForNextLevel = xpRequiredForNext,
            levelName = levelName,
            progress = progress.coerceIn(0f, 1f)
        )
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
