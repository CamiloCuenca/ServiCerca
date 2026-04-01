package com.servicerca.app.data.repository

import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepositoryImpl @Inject constructor() : ReservationRepository {

    private val _reservations = MutableStateFlow<List<Reservation>>(fetchInitialReservations())

    override fun getReservationsByUser(userId: String): Flow<List<Reservation>> {
        return _reservations.map { list -> list.filter { it.userId == userId } }
    }

    override fun getReservationsByProvider(providerId: String): Flow<List<Reservation>> {
        return _reservations.map { list -> list.filter { it.providerId == providerId } }
    }

    override suspend fun getReservationById(reservationId: String): Reservation? {
        return _reservations.value.find { it.id == reservationId }
    }

    override suspend fun createReservation(reservation: Reservation) {
        _reservations.value = _reservations.value + reservation
    }

    override suspend fun updateReservationStatus(reservationId: String, status: String) {
        val newStatus = try {
            ReservationStatus.valueOf(status)
        } catch (e: Exception) {
            ReservationStatus.PENDING
        }

        _reservations.value = _reservations.value.map {
            if (it.id == reservationId) it.copy(status = newStatus) else it
        }
    }

    override suspend fun deleteReservation(reservationId: String) {
        _reservations.value = _reservations.value.filter { it.id != reservationId }
    }

    private fun fetchInitialReservations(): List<Reservation> {
        return listOf(
            Reservation(
                id = "1",
                serviceId = "1",
                serviceTitle = "Plomería General",
                userId = "current_user",
                providerId = "1",
                date = Date(),
                time = "10:00 AM",
                status = ReservationStatus.CONFIRMED
            ),
            Reservation(
                id = "2",
                serviceId = "2",
                serviceTitle = "Electricista Residencial",
                userId = "current_user",
                providerId = "2",
                date = Date(),
                time = "02:30 PM",
                status = ReservationStatus.PENDING
            ),
            Reservation(
                id = "3",
                serviceId = "3",
                serviceTitle = "Limpieza de Muebles",
                userId = "current_user",
                providerId = "3",
                date = Date(),
                time = "03:00 PM",
                status = ReservationStatus.CANCELLED
            )
        )
    }
}
