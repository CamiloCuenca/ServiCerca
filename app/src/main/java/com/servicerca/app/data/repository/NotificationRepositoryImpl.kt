package com.servicerca.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.servicerca.app.R
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.NotificationType
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sessionDataStore: SessionDataStore
) : NotificationRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    override val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null

    private val collection = firestore.collection("notifications")

    init {
        scope.launch {
            sessionDataStore.sessionFlow.collect { session ->
                firestoreListener?.remove()
                firestoreListener = null
                _notifications.value = emptyList()

                val userId = session?.userId ?: return@collect
                val isModerator = session.role == UserRole.MODERATOR || session.role == UserRole.ADMIN
                startListening(userId, isModerator)
            }
        }
    }

    private fun startListening(userId: String, isModerator: Boolean) {
        val query = if (isModerator) {
            collection.whereIn("userId", listOf(userId, "MODERATOR_ROLE"))
        } else {
            collection.whereEqualTo("userId", userId)
        }

        // Sin orderBy: evita requerir índice compuesto en Firestore.
        // El orden se aplica en el cliente por el campo timestamp.
        firestoreListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("NotificationRepo", "Error escuchando notificaciones", error)
                return@addSnapshotListener
            }
            _notifications.value = snapshot?.documents
                ?.mapNotNull { doc ->
                    runCatching {
                        val timestampMillis = doc.getTimestamp("timestamp")?.toDate()?.time ?: System.currentTimeMillis()
                        val notification = Notification(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            date = doc.getString("date") ?: "",
                            imageRes = iconResFromName(doc.getString("iconName") ?: ""),
                            isRead = doc.getBoolean("isRead") ?: false,
                            targetId = doc.getString("targetId"),
                            notificationType = runCatching {
                                NotificationType.valueOf(
                                    doc.getString("notificationType") ?: "SYSTEM"
                                )
                            }.getOrDefault(NotificationType.SYSTEM),
                            timestamp = timestampMillis
                        )
                        Pair(timestampMillis, notification)
                    }.getOrNull()
                }
                ?.sortedByDescending { it.first }
                ?.map { it.second }
                ?: emptyList()
        }
    }

    override suspend fun markAsRead(id: String) {
        try {
            collection.document(id).update("isRead", true).await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error marcando como leída", e)
        }
    }

    override suspend fun markAllAsRead() {
        try {
            val batch = firestore.batch()
            _notifications.value.filter { !it.isRead }.forEach { n ->
                batch.update(collection.document(n.id), "isRead", true)
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error marcando todas como leídas", e)
        }
    }

    override suspend fun deleteNotification(id: String) {
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error eliminando notificación", e)
        }
    }

    override suspend fun clearAll() {
        try {
            val batch = firestore.batch()
            _notifications.value.forEach { n ->
                batch.delete(collection.document(n.id))
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error limpiando notificaciones", e)
        }
    }

    override suspend fun addNotification(notification: Notification) {
        Log.d("NotificationRepo", "addNotification: guardando id=${notification.id}, userId=${notification.userId}")
        try {
            val data = mapOf(
                "id" to notification.id,
                "userId" to notification.userId,
                "title" to notification.title,
                "message" to notification.message,
                "date" to notification.date,
                "iconName" to iconNameFromRes(notification.imageRes),
                "isRead" to notification.isRead,
                "targetId" to notification.targetId,
                "notificationType" to notification.notificationType.name,
                "timestamp" to FieldValue.serverTimestamp()
            )
            collection.document(notification.id).set(data).await()
            Log.d("NotificationRepo", "addNotification: guardado exitosamente en Firestore")
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error guardando notificación", e)
        }
    }

    // ── Mapeo icono ──────────────────────────────────────────────────────────

    private fun iconNameFromRes(imageRes: Int): String = when (imageRes) {
        R.drawable.nueva_solicitud_servicio -> "nueva_solicitud_servicio"
        R.drawable.comentario_recibido      -> "comentario_recibido"
        R.drawable.servicio_verificado      -> "servicio_verificado"
        R.drawable.publicacion_rechazada    -> "publicacion_rechazada"
        R.drawable.nueva_publicacion        -> "nueva_publicacion"
        R.drawable.insignia_favorita        -> "insignia_favorita"
        else                                -> "nueva_publicacion"
    }

    private fun iconResFromName(iconName: String): Int = when (iconName) {
        "nueva_solicitud_servicio" -> R.drawable.nueva_solicitud_servicio
        "comentario_recibido"      -> R.drawable.comentario_recibido
        "servicio_verificado"      -> R.drawable.servicio_verificado
        "publicacion_rechazada"    -> R.drawable.publicacion_rechazada
        "nueva_publicacion"        -> R.drawable.nueva_publicacion
        "insignia_favorita"        -> R.drawable.insignia_favorita
        else                       -> R.drawable.nueva_publicacion
    }
}
