package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getReservationsByUser(userId: String): Flow<List<Reservation>>
    fun getReservationsByProvider(providerId: String): Flow<List<Reservation>>
    suspend fun getReservationById(reservationId: String): Reservation?
    suspend fun createReservation(reservation: Reservation)
    suspend fun updateReservationStatus(reservationId: String, status: String)
    suspend fun deleteReservation(reservationId: String)
    suspend fun completeReservation(reservationId: String)
}
