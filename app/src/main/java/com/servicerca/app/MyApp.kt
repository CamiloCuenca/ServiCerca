package com.servicerca.app

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.servicerca.app.core.fcm.FCMTokenManager
import com.servicerca.app.core.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    @Inject
    lateinit var auth: FirebaseAuth

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        // Si el usuario ya tenía sesión activa al instalar la actualización,
        // aseguramos que su token FCM esté guardado en Firestore.
        refreshFcmTokenIfLoggedIn()
    }

    private fun refreshFcmTokenIfLoggedIn() {
        val uid = auth.currentUser?.uid ?: return
        scope.launch {
            fcmTokenManager.saveTokenForUser(uid)
        }
    }
}