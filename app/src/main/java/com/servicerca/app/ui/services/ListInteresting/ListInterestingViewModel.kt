package com.servicerca.app.ui.services.ListInteresting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListInterestingViewModel @Inject constructor(
    sessionDataStore: SessionDataStore,
    userRepository: UserRepository,
    serviceRepository: ServiceRepository
) : ViewModel() {

    val interestingServices: StateFlow<List<Service>> = combine(
        sessionDataStore.sessionFlow,
        userRepository.users,
        serviceRepository.services
    ) { session, users, services ->
        val userId = session?.userId ?: return@combine emptyList()
        val user = users.firstOrNull { it.id == userId } ?: return@combine emptyList()
        services.filter { it.id in user.listInteresting }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}