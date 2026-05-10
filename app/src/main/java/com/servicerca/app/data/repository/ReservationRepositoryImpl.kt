package com.servicerca.app.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.servicerca.app.domain.model.Reservation
import com.servicerca.app.domain.model.ReservationStatus
import com.servicerca.app.domain.repository.ReservationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReservationRepository {

    private val reservationsCollection = firestore.collection("reservations")

    override fun getReservationsByUser(userId: String): Flow<List<Reservation>> = callbackFlow {
        val listener = reservationsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ReservationRepository", "Error observando reservas del usuario", error)
                    close(error)
                    return@addSnapshotListener
                }
                val reservations = snapshot?.documents?.mapNotNull { it.toReservation() } ?: emptyList()
                trySend(reservations)
            }
        awaitClose { listener.remove() }
    }

    override fun getReservationsByProvider(providerId: String): Flow<List<Reservation>> = callbackFlow {
        val listener = reservationsCollection
            .whereEqualTo("providerId", providerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ReservationRepository", "Error observando reservas del proveedor", error)
                    close(error)
                    return@addSnapshotListener
                }
                val reservations = snapshot?.documents?.mapNotNull { it.toReservation() } ?: emptyList()
                trySend(reservations)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getReservationById(reservationId: String): Reservation? {
        return try {
            val doc = reservationsCollection.document(reservationId).get().await()
            doc.toReservation()
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error obteniendo reserva por id", e)
            null
        }
    }

    override suspend fun createReservation(reservation: Reservation) {
        try {
            reservationsCollection.document(reservation.id).set(reservation.toFirestoreMap()).await()
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error creando reserva", e)
            throw e
        }
    }

    override suspend fun updateReservationStatus(reservationId: String, status: String) {
        try {
            reservationsCollection.document(reservationId).update("status", status).await()
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error actualizando estado de reserva", e)
            throw e
        }
    }

    override suspend fun deleteReservation(reservationId: String) {
        try {
            reservationsCollection.document(reservationId).delete().await()
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error eliminando reserva", e)
            throw e
        }
    }

    override suspend fun completeReservation(reservationId: String) {
        try {
            reservationsCollection.document(reservationId)
                .update("status", ReservationStatus.COMPLETED.name).await()
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error completando reserva", e)
            throw e
        }
    }

    private fun Reservation.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "serviceId" to serviceId,
        "serviceTitle" to serviceTitle,
        "serviceImageUrl" to serviceImageUrl,
        "userId" to userId,
        "providerId" to providerId,
        "date" to Timestamp(date),
        "time" to time,
        "status" to status.name,
        "message" to message
    )

    private fun DocumentSnapshot.toReservation(): Reservation? {
        return try {
            Reservation(
                id = getString("id") ?: this.id,
                serviceId = getString("serviceId") ?: "",
                serviceTitle = getString("serviceTitle") ?: "",
                serviceImageUrl = getString("serviceImageUrl"),
                userId = getString("userId") ?: "",
                providerId = getString("providerId") ?: "",
                date = getTimestamp("date")?.toDate() ?: Date(),
                time = getString("time") ?: "",
                status = ReservationStatus.valueOf(getString("status") ?: ReservationStatus.PENDING.name),
                message = getString("message") ?: ""
            )
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Error parseando documento ${this.id}", e)
            null
        }
    }
}
