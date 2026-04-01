package com.servicerca.app.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _allReservations = MutableStateFlow<List<Reservation>>(emptyList())
    
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _filteredReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _filteredReservations.asStateFlow()

    init {
        observeReservations()
    }

    private fun observeReservations() {
        viewModelScope.launch {
            combine(
                _allReservations,
                _selectedTab,
                _selectedDate
            ) { all, tab, date ->
                all.filter { reservation ->
                    val isCorrectRole = if (tab == 0) {
                        reservation.userId == "current_user"
                    } else {
                        reservation.providerId == "current_user"
                    }
                    
                    val reservationLocalDate = reservation.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    
                    val isCorrectDate = reservationLocalDate == date
                    
                    isCorrectRole && isCorrectDate
                }
            }.collect {
                _filteredReservations.value = it
            }
        }
        
        // Carga inicial
        loadAllReservations()
    }

    private fun loadAllReservations() {
        viewModelScope.launch {
            reservationRepository.getReservationsByUser("current_user").collect {
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
