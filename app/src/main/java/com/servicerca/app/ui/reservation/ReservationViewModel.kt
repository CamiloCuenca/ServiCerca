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
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ReservationUiModel(
    val id: String,
    val serviceTitle: String,
    val serviceImageUrl: String?,
    val date: java.util.Date,
    val time: String,
    val status: com.servicerca.app.domain.model.ReservationStatus,
    val otherUserName: String, // Profesional si es "Mis Pedidos" (0), Cliente si es "Mis Trabajos" (1)
    val isIncoming: Boolean // true si es una solicitud recibida (Mis Trabajos)
)
@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _allReservations = MutableStateFlow<List<Reservation>>(emptyList())
    
    val markedDates: StateFlow<Set<LocalDate>> = combine(
        _allReservations,
        _selectedTab,
        sessionDataStore.sessionFlow
    ) { all, tab, session ->
        val userId = session?.userId ?: return@combine emptySet<LocalDate>()
        all.filter { reservation ->
            val isVisible = reservation.status != com.servicerca.app.domain.model.ReservationStatus.CANCELLED
            if (tab == 0) {
                reservation.userId == userId && isVisible
            } else {
                reservation.providerId == userId && isVisible
            }
        }.map { reservation ->
            reservation.date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _reservationsUi = MutableStateFlow<List<ReservationUiModel>>(emptyList())
    val reservations: StateFlow<List<ReservationUiModel>> = _reservationsUi.asStateFlow()

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
                _selectedDate,
                userRepository.users
            ) { all, tab, date, users ->
                all.filter { reservation ->
                    val isVisible = reservation.status != com.servicerca.app.domain.model.ReservationStatus.CANCELLED
                    val reservationLocalDate = reservation.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    
                    val isCorrectDate = reservationLocalDate == date
                    val isCorrectRole = if (tab == 0) {
                        reservation.userId == userId && isVisible
                    } else {
                        reservation.providerId == userId && isVisible
                    }
                    
                    isCorrectRole && isCorrectDate
                }.map { reservation ->
                    // 🔹 Pestaña 0 (Mis Pedidos): El "otro" es el Proveedor
                    // 🔹 Pestaña 1 (Mis Trabajos): El "otro" es el Cliente
                    val otherUserId = if (tab == 0) reservation.providerId else reservation.userId
                    val otherUser = users.find { it.id == otherUserId }
                    
                    ReservationUiModel(
                        id = reservation.id,
                        serviceTitle = reservation.serviceTitle,
                        serviceImageUrl = reservation.serviceImageUrl,
                        date = reservation.date,
                        time = reservation.time,
                        status = reservation.status,
                        otherUserName = otherUser?.let { "${it.name1} ${it.lastname1}" } ?: "Usuario Desconocido",
                        isIncoming = tab == 1
                    )
                }
            }.collect {
                _reservationsUi.value = it
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
