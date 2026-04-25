package com.servicerca.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Categories
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

enum class SearchFilter(val displayName: String) {
    ALL("Todos"),
    CHEAPEST("Más económicos"),
    PREMIUM("Premium")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Categories?>(null)
    val selectedCategory: StateFlow<Categories?> = _selectedCategory.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _selectedFilter = MutableStateFlow(SearchFilter.ALL)
    val selectedFilter: StateFlow<SearchFilter> = _selectedFilter.asStateFlow()

    val searchResults: StateFlow<List<SearchServiceResult>> = combine(
        serviceRepository.services,
        userRepository.users,
        sessionDataStore.sessionFlow,
        _query,
        _selectedFilter
    ) { services, users, session, query, filter ->
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            return@combine emptyList()
        }

        val currentUser = users.firstOrNull { it.id == session?.userId }
        val interestingIds = currentUser?.listInteresting?.toSet().orEmpty()

        var results = services
            .asSequence()
            .filter { it.status != ServiceStatus.DELETED }
            .filter { it.title.contains(normalizedQuery, ignoreCase = true) }
            .map { service ->
                SearchServiceResult(
                    service = service,
                    isBookmarked = service.id in interestingIds
                )
            }

        results = when (filter) {
            SearchFilter.ALL -> results
            SearchFilter.CHEAPEST -> results.sortedBy { it.service.priceMin }
            SearchFilter.PREMIUM -> results.sortedByDescending { it.service.priceMin }
        }

        results.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categoryResults: StateFlow<List<SearchServiceResult>> = combine(
        serviceRepository.services,
        userRepository.users,
        sessionDataStore.sessionFlow,
        _selectedCategory,
        _selectedFilter
    ) { services, users, session, selectedCategory, filter ->
        val category = selectedCategory ?: return@combine emptyList()
        val currentUser = users.firstOrNull { it.id == session?.userId }
        val interestingIds = currentUser?.listInteresting?.toSet().orEmpty()

        var results = services
            .asSequence()
            .filter { it.status != ServiceStatus.DELETED }
            .filter { it.type.contains(category.displayName, ignoreCase = true) }
            .map { service ->
                SearchServiceResult(
                    service = service,
                    isBookmarked = service.id in interestingIds
                )
            }

        results = when (filter) {
            SearchFilter.ALL -> results
            SearchFilter.CHEAPEST -> results.sortedBy { it.service.priceMin }
            SearchFilter.PREMIUM -> results.sortedByDescending { it.service.priceMin }
        }

        results.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onFilterSelect(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isNotBlank()) {
            _selectedCategory.value = null
        }
    }

    fun submitCurrentSearch() {
        addRecentSearch(_query.value)
    }

    fun selectRecentSearch(search: String) {
        _selectedCategory.value = null
        _query.value = search
        addRecentSearch(search)
    }

    fun selectCategory(category: Categories) {
        _query.value = ""
        _selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
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


