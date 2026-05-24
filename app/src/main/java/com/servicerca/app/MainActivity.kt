package com.servicerca.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.servicerca.app.core.i18n.LanguageManager
import com.servicerca.app.core.navigation.AppNavigation
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.ui.theme.ServiCercaTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.servicerca.app.data.datastore.AppThemeMode
import com.servicerca.app.data.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var sessionDataStore: SessionDataStore

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* El usuario ya decidió; no forzamos nada */ }

    // Necesario para que los deep links funcionen cuando la app ya está abierta (launchMode=singleTask)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            languageManager.applySavedLanguage()
            // Set online status if already logged in
            sessionDataStore.sessionFlow.first()?.userId?.let { uid ->
                userRepository.updateOnlineStatus(uid, true)
            }
        }
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()

        setContent {
            val themeMode by settingsDataStore.themeModeFlow.collectAsStateWithLifecycle(
                initialValue = AppThemeMode.SYSTEM_DEFAULT
            )

            val isDarkTheme = when (themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }

            ServiCercaTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Set offline status on exit
        runBlocking {
            sessionDataStore.sessionFlow.first()?.userId?.let { uid ->
                userRepository.updateOnlineStatus(uid, false)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    ServiCercaTheme {}
}