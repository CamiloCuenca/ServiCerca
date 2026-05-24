package com.servicerca.app.core.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.servicerca.app.R
import com.servicerca.app.core.notifications.NotificationHelper
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.NotificationType
import com.servicerca.app.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ServiCercaMessagingService : FirebaseMessagingService() {

    @Inject lateinit var fcmTokenManager: FCMTokenManager
    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var sessionDataStore: SessionDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["type"] ?: "general"
        val title = data["title"] ?: remoteMessage.notification?.title ?: "ServiCerca"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""

        Log.d("FCM", "Mensaje recibido — tipo: $type, título: $title")

        when (type) {
            "chat" -> {
                val senderId = data["senderId"] ?: ""
                if (senderId.isNotBlank()) {
                    NotificationHelper.showChatNotification(applicationContext, title, body, senderId)
                    saveToInAppList(data, title, body)
                }
            }
            else -> {
                NotificationHelper.showGeneralNotification(applicationContext, title, body)
                if (data["noSave"] != "true") {
                    saveToInAppList(data, title, body)
                }
            }
        }
    }

    private fun saveToInAppList(data: Map<String, String>, title: String, body: String) {
        serviceScope.launch {
            try {
                val targetUserId = data["userId"]
                    ?: sessionDataStore.sessionFlow.firstOrNull()?.userId

                if (targetUserId == null) return@launch

                val notificationTypeStr = data["notificationType"] ?: "SYSTEM"
                val notificationType = try {
                    NotificationType.valueOf(notificationTypeStr)
                } catch (e: Exception) {
                    NotificationType.SYSTEM
                }

                val iconRes = when {
                    data["type"] == "chat" -> R.drawable.insignia_chat
                    data["type"] == "rejection" -> R.drawable.publicacion_rechazada
                    notificationType == NotificationType.RESERVATION -> R.drawable.nueva_solicitud_servicio
                    notificationType == NotificationType.MODERATION -> R.drawable.servicio_verificado
                    notificationType == NotificationType.SERVICE -> R.drawable.nueva_publicacion
                    else -> R.drawable.nueva_publicacion
                }

                notificationRepository.addNotification(
                    Notification(
                        id = data["notificationId"] ?: UUID.randomUUID().toString(),
                        userId = targetUserId,
                        title = title,
                        message = body,
                        date = "Ahora",
                        imageRes = iconRes,
                        isRead = false,
                        targetId = data["targetId"] ?: data["senderId"], // Usar senderId como target para chats
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("FCM", "Error guardando notificación in-app", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        fcmTokenManager.onTokenRefresh(token)
    }
}
