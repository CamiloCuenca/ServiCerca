package com.servicerca.app.ui.reservation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.ChatRepository
import com.servicerca.app.domain.repository.ReservationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationDetailsUiState(
    val reservation: Reservation? = null,
    val service: Service? = null,
    val provider: User? = null,
    val customer: User? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ReservationDetailsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailsUiState())
    val uiState: StateFlow<ReservationDetailsUiState> = _uiState.asStateFlow()

    fun loadReservation(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val reservation = reservationRepository.getReservationById(id)
            if (reservation != null) {
                val service = serviceRepository.findById(reservation.serviceId)
                val provider = userRepository.findById(reservation.providerId)
                val customer = userRepository.findById(reservation.userId)
                
                _uiState.value = _uiState.value.copy(
                    reservation = reservation,
                    service = service,
                    provider = provider,
                    customer = customer,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun cancelReservation(id: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            reservationRepository.updateReservationStatus(id, "CANCELLED")
            loadReservation(id)
            onComplete()
        }
    }

    fun onContactProfessional(onNavigate: (String) -> Unit) {
        val provider = uiState.value.provider ?: return

        viewModelScope.launch {
            val chatId = chatRepository.getOrCreateChat(
                userId = provider.id,
                userName = "${provider.name1} ${provider.lastname1}",
                userImage = provider.profilePictureUrl
            )
            onNavigate(chatId)
        }
    }
}
