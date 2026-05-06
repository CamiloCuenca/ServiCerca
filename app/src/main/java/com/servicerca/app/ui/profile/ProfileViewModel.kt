package com.servicerca.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.i18n.LanguageManager
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.data.datastore.SettingsDataStore
import com.servicerca.app.data.datastore.AppThemeMode
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.domain.usecase.GetEarnedInsigniasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Success(
        val user: User,
        val insignias: List<InsigniaUiModel> = emptyList(),
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
    ) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository,
    private val sessionDataStore: SessionDataStore,
    private val settingsDataStore: SettingsDataStore,
    private val getEarnedInsigniasUseCase: GetEarnedInsigniasUseCase,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _selectedLanguageTag = MutableStateFlow(SettingsDataStore.SYSTEM_LANGUAGE_TAG)
    val selectedLanguageTag: StateFlow<String> = _selectedLanguageTag.asStateFlow()

    private val _selectedThemeMode = MutableStateFlow(AppThemeMode.SYSTEM_DEFAULT)
    val selectedThemeMode: StateFlow<AppThemeMode> = _selectedThemeMode.asStateFlow()

    init {
        observeLanguageTag()
        observeThemeMode()
        loadUserProfile()
        observeServiceCounts()
        observeAverageRating()
    }

    private fun observeLanguageTag() {
        viewModelScope.launch {
            languageManager.selectedLanguageTag.collectLatest { tag ->
                _selectedLanguageTag.value = tag
            }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            settingsDataStore.themeModeFlow.collectLatest { mode ->
                _selectedThemeMode.value = mode
            }
        }
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

                // We no longer calculate average from comments locally to ensure SSOT with User model
                // But we still observe comments to trigger UI updates if necessary
                Unit
            }.collect { 
                updateSuccessState { current ->
                    val levelInfo = calculateLevelInfo(current.user.totalPoints, current.user.rating)
                    current.copy(
                        averageRating = current.user.rating,
                        totalXp = current.user.totalPoints,
                        level = levelInfo.level,
                        xpInLevel = levelInfo.xpInLevel,
                        xpRequiredForNextLevel = levelInfo.xpRequiredForNextLevel,
                        levelName = levelInfo.levelName,
                        progress = levelInfo.progress
                    )
                }
            }
        }
    }

    private data class LevelInfo(
        val avgRating: Double,
        val totalXp: Int,
        val level: Int,
        val xpInLevel: Int,
        val xpRequiredForNextLevel: Int,
        val levelName: String,
        val progress: Float
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
                if (i + 1 < levels.size) {
                    xpRequiredForNext = levels[i + 1]
                } else {
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
            levels.last()
        } else {
            totalXp - xpRequiredForCurrent
        }
        
        val range = xpRequiredForNext - xpRequiredForCurrent
        val progress = if (totalXp >= levels.last()) {
            1.0f
        } else {
            (totalXp - xpRequiredForCurrent).toFloat() / range.toFloat()
        }

        return LevelInfo(
            avgRating = avgRating,
            totalXp = totalXp,
            level = clampedLevel,
            xpInLevel = totalXp,
            xpRequiredForNextLevel = xpRequiredForNext,
            levelName = levelName,
            progress = progress.coerceIn(0f, 1f)
        )
    }

    private fun observeServiceCounts() {
        viewModelScope.launch {
            serviceRepository.services.collectLatest { services ->
                updateSuccessState { it.copy(
                    pendingCount = services.count { it.status == ServiceStatus.PENDING },
                    approvedCount = services.count { it.status == ServiceStatus.APPROVED },
                    rejectedCount = services.count { it.status == ServiceStatus.REJECTED }
                ) }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val session = sessionDataStore.sessionFlow.firstOrNull()
            if (session != null) {
                userRepository.users.collectLatest { allUsers ->
                    val user = allUsers.find { it.id == session.userId }
                    if (user != null) {
                        val insignias = getEarnedInsigniasUseCase(user).map { it.toUiModel() }
                        val levelInfo = calculateLevelInfo(user.totalPoints, user.rating)
                        updateSuccessState { current ->
                            current.copy(
                                user = user,
                                insignias = insignias,
                                averageRating = user.rating,
                                totalXp = user.totalPoints,
                                level = levelInfo.level,
                                xpInLevel = levelInfo.xpInLevel,
                                xpRequiredForNextLevel = levelInfo.xpRequiredForNextLevel,
                                levelName = levelInfo.levelName,
                                progress = levelInfo.progress
                            )
                        } ?: run {
                            // First time success
                            val services = serviceRepository.services.value
                            _uiState.value = ProfileUiState.Success(
                                user = user,
                                insignias = insignias,
                                pendingCount = services.count { it.status == ServiceStatus.PENDING },
                                approvedCount = services.count { it.status == ServiceStatus.APPROVED },
                                rejectedCount = services.count { it.status == ServiceStatus.REJECTED },
                                averageRating = user.rating,
                                totalXp = user.totalPoints,
                                level = levelInfo.level,
                                xpInLevel = levelInfo.xpInLevel,
                                xpRequiredForNextLevel = levelInfo.xpRequiredForNextLevel,
                                levelName = levelInfo.levelName,
                                progress = levelInfo.progress
                            )
                        }
                    } else {
                        _uiState.value = ProfileUiState.Error("Usuario no encontrado")
                    }
                }
            } else {
                _uiState.value = ProfileUiState.Error("Sesión no encontrada")
            }
        }
    }

    private fun updateSuccessState(update: (ProfileUiState.Success) -> ProfileUiState.Success): ProfileUiState.Success? {
        val current = _uiState.value
        return if (current is ProfileUiState.Success) {
            val updated = update(current)
            _uiState.value = updated
            updated
        } else {
            null
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }

    fun setLanguage(tag: String) {
        viewModelScope.launch {
            languageManager.setLanguage(tag)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsDataStore.saveThemeMode(mode)
        }
    }
}


