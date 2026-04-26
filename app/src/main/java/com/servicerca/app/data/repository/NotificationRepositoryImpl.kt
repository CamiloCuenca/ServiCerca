package com.servicerca.app.data.repository

import com.servicerca.app.R
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    override val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        _notifications.value = fetchInitialNotifications()
    }

    override suspend fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    override suspend fun markAllAsRead() {
        _notifications.value = _notifications.value.map {
            it.copy(isRead = true)
        }
    }

    override suspend fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    override suspend fun clearAll() {
        _notifications.value = emptyList()
    }

    override suspend fun addNotification(notification: Notification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    private fun fetchInitialNotifications(): List<Notification> {
        return listOf(
            Notification(
                id = "1",
                userId = "1",
                title = "Nueva solicitud de servicio",
                message = "Has recibido una nueva propuesta para el proyecto \"Reparación Eléctrica\".",
                date = "5 min",
                imageRes = R.drawable.nueva_solicitud_servicio,
                isRead = false
            ),
            Notification(
                id = "2",
                userId = "1",
                title = "Comentario recibido",
                message = "\"Excelente trabajo, muy profesional y a tiempo.\"",
                date = "2 h",
                imageRes = R.drawable.comentario_recibido,
                isRead = false
            ),
            Notification(
                id = "3",
                userId = "1",
                title = "Servicio verificado",
                message = "El usuario @juan_perez ha confirmado tu servicio \"Plomería general\".",
                date = "Ayer",
                imageRes = R.drawable.servicio_verificado,
                isRead = true
            ),
            Notification(
                id = "4",
                userId = "1",
                title = "Publicación rechazada",
                message = "Tu publicación \"Jardinería\" no cumple con las normativas.",
                date = "12 oct",
                imageRes = R.drawable.publicacion_rechazada,
                isRead = true
            ),
            Notification(
                id = "5",
                userId = "1",
                title = "Nueva publicación",
                message = "¡Tu publicación \"Mantenimiento de PC\" ya está disponible!",
                date = "10 oct",
                imageRes = R.drawable.nueva_publicacion,
                isRead = true
            ),
            Notification(
                id = "6",
                userId = "MODERATOR_ROLE",
                title = "Revisión Pendiente",
                message = "El servicio \"Reparación de Techos\" requiere validación.",
                date = "2 h",
                imageRes = R.drawable.nueva_solicitud_servicio,
                isRead = false,
                notificationType = com.servicerca.app.domain.model.NotificationType.MODERATION,
                targetId = "1"
            ),
        )
    }
}
