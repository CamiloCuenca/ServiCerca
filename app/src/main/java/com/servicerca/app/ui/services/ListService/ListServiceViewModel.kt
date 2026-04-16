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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class ListServiceViewModel @Inject constructor(
	private val serviceRepository: ServiceRepository,
	private val sessionDataStore: SessionDataStore
) : ViewModel() {

	val services: StateFlow<List<Service>> = sessionDataStore.sessionFlow
		.flatMapLatest { session ->
			val ownerId = session?.userId
			if (!ownerId.isNullOrBlank()) {
				serviceRepository.findByOwnerId(ownerId)
			} else {
				flowOf(emptyList())
			}
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	fun refresh() {
		// Con la implementación reactiva, el refresh no suele ser necesario
		// pero si el repositorio no es reactivo se podría forzar algo aquí.
	}

	fun deleteService(id: String) {
		viewModelScope.launch {
			try {
				serviceRepository.delete(id)
			} catch (_: Exception) {
				// Ignorar errores locales
			}
		}
	}
}

