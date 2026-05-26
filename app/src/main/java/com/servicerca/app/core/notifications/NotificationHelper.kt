package com.servicerca.app.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.servicerca.app.MainActivity
import com.servicerca.app.R
import androidx.core.net.toUri

object NotificationHelper {

    const val CHANNEL_CHAT = "channel_chat"
    const val CHANNEL_GENERAL = "channel_general"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CHAT,
                "Mensajes de Chat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de nuevos mensajes de chat"
                enableVibration(true)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_GENERAL,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alertas de servicios, moderación y más"
            }
        )
    }

    // Notificación de chat: al tocarla navega al chat con ese usuario usando Deep Linking
    fun showChatNotification(
        context: Context,
        title: String,
        body: String,
        senderId: String
    ) {
        // Usar Deep Link para navegación segura
        val deepLinkUri = "https://servicerca-6ee07.web.app/chat/$senderId".toUri()
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri, context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            senderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        show(context, CHANNEL_CHAT, title, body, NotificationCompat.PRIORITY_HIGH, pendingIntent)
    }

    fun showGeneralNotification(context: Context, title: String, body: String) {
        val requestCode = (title + body + System.currentTimeMillis()).hashCode()
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        show(context, CHANNEL_GENERAL, title, body, NotificationCompat.PRIORITY_DEFAULT, pendingIntent)
    }

    private fun show(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        priority: Int,
        pendingIntent: PendingIntent
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(priority)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
