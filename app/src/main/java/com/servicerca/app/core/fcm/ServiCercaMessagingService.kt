package com.servicerca.app.core.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.servicerca.app.core.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServiCercaMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["type"] ?: "general"
        // Mensajes data-only: title y body siempre están en data
        val title = data["title"] ?: remoteMessage.notification?.title ?: "ServiCerca"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""

        Log.d("FCM", "Mensaje recibido — tipo: $type, título: $title")

        when (type) {
            "chat" -> {
                val senderId = data["senderId"] ?: ""
                NotificationHelper.showChatNotification(applicationContext, title, body, senderId)
            }
            else -> NotificationHelper.showGeneralNotification(applicationContext, title, body)
        }
    }

    // Firebase llama a esto cuando renueva el token (cambio de dispositivo, reinstalación, etc.)
    override fun onNewToken(token: String) {
        Log.d("FCM", "Token renovado: $token")
        fcmTokenManager.onTokenRefresh(token)
    }
}
