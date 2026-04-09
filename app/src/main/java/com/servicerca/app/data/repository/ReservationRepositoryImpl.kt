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

    private fun fetchInitialReservations(userId: String): List<Reservation> {
        return listOf(
            Reservation(
                id = "1",
                serviceId = "1",
                serviceTitle = "Plomería General",
                serviceImageUrl = "https://projectssdn.com/wp-content/uploads/elementor/thumbs/plomeria-en-general-qp5x9n6u64ze4tk30xqoxt57okaxdr7apr7hp13vds.png",
                userId = userId,
                providerId = "1",
                date = Date(),
                time = "10:00 AM",
                status = ReservationStatus.CONFIRMED
            ),
            Reservation(
                id = "2",
                serviceId = "2",
                serviceTitle = "Electricista Residencial",
                serviceImageUrl = "https://comfenalcoquindio.com/wp-content/uploads/2022/05/tecnico-electricista-en-construccion-residencial-1.jpg",
                userId = userId,
                providerId = "2",
                date = Date(),
                time = "02:30 PM",
                status = ReservationStatus.PENDING
            ),
            Reservation(
                id = "3",
                serviceId = "3",
                serviceTitle = "Limpieza de Muebles",
                serviceImageUrl = "https://extremecleangm.com/wp-content/uploads/2025/01/Lavado-de-Muebles-Iniciando-el-2025.jpg",
                userId = userId,
                providerId = "3",
                date = Date(),
                time = "03:00 PM",
                status = ReservationStatus.CANCELLED
            )
        )
    }
}
