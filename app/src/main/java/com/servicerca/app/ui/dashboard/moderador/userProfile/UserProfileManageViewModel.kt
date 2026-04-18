package com.servicerca.app.ui.dashboard.moderador.userProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.ui.profile.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileManageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = userRepository.findById(id)
            _uiState.value = _uiState.value.copy(user = user, isLoading = false)

            // Observar estadísticas basadas en servicios y comentarios del usuario
            observeUserStats(id)
        }
    }

    private fun observeUserStats(targetUserId: String) {
        viewModelScope.launch {
            combine(
                serviceRepository.services,
                commentRepository.comments
            ) { services, allComments ->
                val userServices = services.filter { it.ownerId == targetUserId }
                val userServiceIds = userServices.map { it.id }
                val userComments = allComments.filter { it.serviceId in userServiceIds }

                val avg = if (userComments.isNotEmpty()) userComments.map { it.rating }.average() else 0.0
                val totalXp = userComments.sumOf { it.rating * 50 }

                // Aquí podrías reutilizar una lógica común de cálculo de niveles
                calculateLevelState(totalXp, avg)
            }.collect { newState ->
                _uiState.value = _uiState.value.copy(
                    averageRating = newState.averageRating,
                    totalXp = newState.totalXp,
                    level = newState.level,
                    levelName = newState.levelName,
                    progress = newState.progress,
                    xpRequiredForNextLevel = newState.xpRequiredForNextLevel
                )
            }
        }
    }

    // Nota: Esta lógica idealmente debería estar en un UseCase para no duplicarla
    private fun calculateLevelState(totalXp: Int, avgRating: Double): ProfileUiState {
        val levels = listOf(500, 1300, 2500, 4300, 7000)
        val levelNames = listOf("Principiante", "Colaborador", "Confiable", "Profesional local", "Experto local")
        var currentLevel = 1
        var xpRequiredForCurrent = 0
        var xpRequiredForNext = levels[0]

        for (i in levels.indices) {
            if (totalXp >= levels[i]) {
                currentLevel = i + 2
                xpRequiredForCurrent = levels[i]
                xpRequiredForNext = if (i + 1 < levels.size) levels[i + 1] else levels.last()
            } else {
                xpRequiredForNext = levels[i]
                break
            }
        }
        val clampedLevel = currentLevel.coerceAtMost(5)
        val progress = if (totalXp >= levels.last()) 1.0f else (totalXp - xpRequiredForCurrent).toFloat() / (xpRequiredForNext - xpRequiredForCurrent).toFloat()

        return ProfileUiState(
            averageRating = avgRating,
            totalXp = totalXp,
            level = clampedLevel,
            levelName = levelNames[(clampedLevel - 1).coerceIn(0, 4)],
            progress = progress.coerceIn(0f, 1f),
            xpRequiredForNextLevel = xpRequiredForNext
        )
    }
}