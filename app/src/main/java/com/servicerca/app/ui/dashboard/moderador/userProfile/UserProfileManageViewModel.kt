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

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val id = userId ?: return
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val user = userRepository.findById(id)
            if (user != null) {
                _uiState.value = ProfileUiState.Success(user = user)
                observeUserStats(id)
            } else {
                _uiState.value = ProfileUiState.Error("Usuario no encontrado")
            }
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

                calculateLevelInfo(totalXp, avg)
            }.collect { levelInfo ->
                updateSuccessState { it.copy(
                    averageRating = levelInfo.avgRating,
                    totalXp = levelInfo.totalXp,
                    level = levelInfo.level,
                    levelName = levelInfo.levelName,
                    progress = levelInfo.progress,
                    xpRequiredForNextLevel = levelInfo.xpRequiredForNextLevel
                ) }
            }
        }
    }

    private data class LevelInfo(
        val avgRating: Double,
        val totalXp: Int,
        val level: Int,
        val levelName: String,
        val progress: Float,
        val xpRequiredForNextLevel: Int
    )

    private fun calculateLevelInfo(totalXp: Int, avgRating: Double): LevelInfo {
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

        return LevelInfo(
            avgRating = avgRating,
            totalXp = totalXp,
            level = clampedLevel,
            levelName = levelNames[(clampedLevel - 1).coerceIn(0, 4)],
            progress = progress.coerceIn(0f, 1f),
            xpRequiredForNextLevel = xpRequiredForNext
        )
    }

    private fun updateSuccessState(update: (ProfileUiState.Success) -> ProfileUiState.Success) {
        val current = _uiState.value
        if (current is ProfileUiState.Success) {
            _uiState.value = update(current)
        }
    }
}