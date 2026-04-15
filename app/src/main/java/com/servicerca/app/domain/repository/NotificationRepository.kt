package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Notification
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val notifications: StateFlow<List<Notification>>

    suspend fun markAsRead(id: String)
    
    suspend fun markAllAsRead()

    suspend fun deleteNotification(id: String)
    
    suspend fun clearAll()

    suspend fun addNotification(notification: Notification)
}
