package com.servicerca.app.ui.dashboard.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeUserViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    // Obtenemos solo los servicios APROBADOS del repositorio
    val services: StateFlow<List<Service>> = serviceRepository.services
        .map { list ->
            list.filter { it.status == ServiceStatus.APPROVED }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}