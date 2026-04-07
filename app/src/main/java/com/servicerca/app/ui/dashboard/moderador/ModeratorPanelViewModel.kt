package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModeratorPanelUiState (
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ModeratorPanelViewModel @Inject constructor(
        private val serviceRepository: ServiceRepository): ViewModel(){
            private val _uiState = MutableStateFlow(ModeratorPanelUiState())
            val uiState: StateFlow<ModeratorPanelUiState> = _uiState.asStateFlow()

            init {
                loadServicesByTab(0)
            }

    fun loadServicesByTab(tabIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true)}
            val status = when (tabIndex){
                0 -> ServiceStatus.PENDING
                1 -> ServiceStatus.IN_PROGRESS
                2 -> ServiceStatus.RESOLVED
                else -> ServiceStatus.PENDING
            }
            serviceRepository.findByStatus(status).collect { servicesList ->
                _uiState.update {
                    it.copy(
                        services = servicesList,
                        isLoading = false
                    )
                }
            }
        }
    }
}