package com.servicerca.app.ui.services.ListService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListServiceViewModel @Inject constructor(
	private val serviceRepository: ServiceRepository,
	private val sessionDataStore: SessionDataStore
) : ViewModel() {

	private val _services = MutableStateFlow<List<Service>>(emptyList())
	val services: StateFlow<List<Service>> = _services.asStateFlow()

	init {
		loadUserServices()
	}

	fun refresh() {
		loadUserServices()
	}

	fun deleteService(id: String) {
		viewModelScope.launch {
			try {
				serviceRepository.delete(id)
			} catch (_: Exception) {
				// Ignorar errores locales
			}
			// Refrescar la lista local
			loadUserServices()
		}
	}

	private fun loadUserServices() {
		viewModelScope.launch {
			val session = sessionDataStore.sessionFlow.first()
			val ownerId = session?.userId
			if (!ownerId.isNullOrBlank()) {
				// Obtener servicios desde el repositorio filtrados por owner
				val ownerServicesFlow = serviceRepository.findByOwnerId(ownerId)
				_services.value = ownerServicesFlow.first()
			} else {
				_services.value = emptyList()
			}
		}
	}
}

