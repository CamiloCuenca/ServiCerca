package com.servicerca.app.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _allReservations = MutableStateFlow<List<Reservation>>(emptyList())
    
    val markedDates: StateFlow<Set<LocalDate>> = _allReservations.map { list ->
        list.map { reservation ->
            reservation.date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _filteredReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _filteredReservations.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadSessionAndObserve()
    }

    private fun loadSessionAndObserve() {
        viewModelScope.launch {
            sessionDataStore.sessionFlow.collect { session ->
                currentUserId = session?.userId
                currentUserId?.let { userId ->
                    observeFilteredReservations(userId)
                    loadAllReservations(userId)
                }
            }
        }
    }

    private fun observeFilteredReservations(userId: String) {
        viewModelScope.launch {
            combine(
                _allReservations,
                _selectedTab,
                _selectedDate
            ) { all, tab, date ->
                all.filter { reservation ->
                    // Permitimos PENDING para que el usuario vea sus nuevas reservas
                    val isVisible = reservation.status != com.servicerca.app.domain.model.ReservationStatus.CANCELLED
                    
                    val reservationLocalDate = reservation.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    
                    val isCorrectDate = reservationLocalDate == date
                    
                    val isCorrectRole = if (tab == 0) {
                        reservation.userId == userId && isVisible
                    } else {
                        reservation.providerId == userId
                    }
                    
                    isCorrectRole && isCorrectDate
                }
            }.collect {
                _filteredReservations.value = it
            }
        }
    }

    private fun loadAllReservations(userId: String) {
        viewModelScope.launch {
            combine(
                reservationRepository.getReservationsByUser(userId),
                reservationRepository.getReservationsByProvider(userId)
            ) { asUser, asProvider ->
                (asUser + asProvider).distinctBy { it.id }
            }.collect {
                _allReservations.value = it
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }
}
