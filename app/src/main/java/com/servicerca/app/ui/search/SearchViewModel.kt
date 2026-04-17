package com.servicerca.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchServiceResult(
    val service: Service,
    val isBookmarked: Boolean
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val searchResults: StateFlow<List<SearchServiceResult>> = combine(
        serviceRepository.services,
        userRepository.users,
        sessionDataStore.sessionFlow,
        _query
    ) { services, users, session, query ->
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            return@combine emptyList()
        }

        val currentUser = users.firstOrNull { it.id == session?.userId }
        val interestingIds = currentUser?.listInteresting?.toSet().orEmpty()

        services
            .asSequence()
            .filter { it.status != ServiceStatus.DELETED }
            .filter { it.title.contains(normalizedQuery, ignoreCase = true) }
            .map { service ->
                SearchServiceResult(
                    service = service,
                    isBookmarked = service.id in interestingIds
                )
            }
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun submitCurrentSearch() {
        addRecentSearch(_query.value)
    }

    fun selectRecentSearch(search: String) {
        _query.value = search
        addRecentSearch(search)
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun onBookmarkClick(serviceId: String) {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
            userRepository.toggleInterestingService(
                userId = session.userId,
                serviceId = serviceId
            )
        }
    }

    private fun addRecentSearch(search: String) {
        val normalized = search.trim()
        if (normalized.isBlank()) return

        _recentSearches.value = buildList {
            add(normalized)
            addAll(
                _recentSearches.value.filterNot {
                    it.equals(normalized, ignoreCase = true)
                }
            )
        }.take(5)
    }
}


