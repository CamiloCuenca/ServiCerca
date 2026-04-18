package com.servicerca.app.data.repository

import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.repository.ReservationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : ReservationRepository {

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            sessionDataStore.sessionFlow.collect { session ->
                val userId = session?.userId ?: return@collect
                _reservations.update { current ->
                    if (current.isEmpty()) fetchInitialReservations(userId) else current
                }
            }
        }
    }

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

    override suspend fun completeReservation(reservationId: String) {
        _reservations.update { current ->
            current.mapNotNull { reservation ->
                if (reservation.id == reservationId) {
                    null
                } else {
                    reservation
                }
            }
        }
    }

    private fun fetchInitialReservations(userId: String): List<Reservation> {
        val date1 = java.util.Date(1775815200000L) // 10 de abril de 2026
        val date2 = java.util.Date(1776247200000L) // 15 de abril de 2026
        val date3 = java.util.Date(1776679200000L) // 20 de abril de 2026

        return listOf(
            Reservation(
                id = "1",
                serviceId = "1",
                serviceTitle = "Plomería General",
                serviceImageUrl = "https://projectssdn.com/wp-content/uploads/elementor/thumbs/plomeria-en-general-qp5x9n6u64ze4tk30xqoxt57okaxdr7apr7hp13vds.png",
                userId = "1",
                providerId = "1",
                date = date1,
                time = "10:00 AM",
                status = ReservationStatus.CONFIRMED
            ),
            Reservation(
                id = "2",
                serviceId = "2",
                serviceTitle = "Electricista Residencial",
                serviceImageUrl = "https://comfenalcoquindio.com/wp-content/uploads/2022/05/tecnico-electricista-en-construccion-residencial-1.jpg",
                userId = "2",
                providerId = "2",
                date = date2,
                time = "02:30 PM",
                status = ReservationStatus.CONFIRMED
            ),
            Reservation(
                id = "3",
                serviceId = "3",
                serviceTitle = "Limpieza de Muebles",
                serviceImageUrl = "https://extremecleangm.com/wp-content/uploads/2025/01/Lavado-de-Muebles-Iniciando-el-2025.jpg",
                userId = "3",
                providerId = "3",
                date = date3,
                time = "03:00 PM",
                status = ReservationStatus.CONFIRMED
            )
        )
    }
}
