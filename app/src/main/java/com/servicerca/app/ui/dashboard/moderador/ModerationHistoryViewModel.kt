package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.ModerationItem
import com.servicerca.app.domain.model.ModerationStatus
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ModerationHistoryViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado para la pestaña seleccionada (0: Todos, 1: Aprobados, 2: Rechazados)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Fuente de datos real desde el repositorio
    val filteredHistory: StateFlow<List<ModerationItem>> = combine(
        serviceRepository.services,
        userRepository.users,
        _selectedTab
    ) { services, users, tab ->
        // Filtramos para obtener solo los servicios que ya fueron procesados (NO están en PENDING)
        val historyServices = services.filter { it.status != ServiceStatus.PENDING }
        
        val items = historyServices.map { service ->
            val owner = users.find { it.id == service.ownerId }
            val status = if (service.status == ServiceStatus.APPROVED) ModerationStatus.APRBADA else ModerationStatus.RECHAZADA
            
            ModerationItem(
                serviceId = service.id,
                title = service.title,
                resultado = if (status == ModerationStatus.APRBADA) "APROBADA" else "RECHAZADA",
                userName = "${owner?.name1 ?: ""} ${owner?.lastname1 ?: ""}".trim(),
                actionPerformed = "Moderación de Servicio",
                reason = if (status == ModerationStatus.APRBADA) "Cumple con las políticas" else "No cumple con las políticas",
                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
                status = status
            )
        }

        when (tab) {
            1 -> items.filter { it.status == ModerationStatus.APRBADA }
            2 -> items.filter { it.status == ModerationStatus.RECHAZADA }
            else -> items
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }
}