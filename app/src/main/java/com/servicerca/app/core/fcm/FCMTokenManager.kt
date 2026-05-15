package com.servicerca.app.core.fcm

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
    private val auth: FirebaseAuth
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Llamar después de login exitoso para registrar el token del dispositivo
    suspend fun saveTokenForUser(userId: String) {
        try {
            val token = messaging.token.await()
            firestore.collection("users").document(userId)
                .update("fcmToken", token)
                .await()
            Log.d("FCM", "Token registrado para usuario $userId")
        } catch (e: Exception) {
            Log.e("FCM", "Error guardando token FCM", e)
        }
    }

    // Llamado desde ServiCercaMessagingService.onNewToken cuando Firebase renueva el token
    fun onTokenRefresh(token: String) {
        val uid = auth.currentUser?.uid ?: return
        scope.launch {
            try {
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
                    .await()
                Log.d("FCM", "Token renovado para $uid")
            } catch (e: Exception) {
                Log.e("FCM", "Error actualizando token FCM", e)
            }
        }
    }
}
