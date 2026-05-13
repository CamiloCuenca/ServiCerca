package com.servicerca.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.appcompat.app.AppCompatActivity
import com.servicerca.app.core.i18n.LanguageManager
import com.servicerca.app.core.navigation.AppNavigation
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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    // oobCode extraído del deep link de Firebase Password Reset.
    // Se expone como StateFlow para que AppNavigation lo observe.
    private val _pendingOobCode = MutableStateFlow<String?>(null)
    val pendingOobCode: StateFlow<String?> = _pendingOobCode.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            languageManager.applySavedLanguage()
        }
        enableEdgeToEdge()

        // Manejar el intent inicial (app abierta desde un deep link)
        handleIntent(intent)

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
                AppNavigation(pendingOobCode = pendingOobCode)
            }
        }
    }

    // Manejar intents recibidos mientras la app está en primer plano
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * Extrae el oobCode del Intent si viene de un deep link de Firebase
     * Password Reset. El link tiene la forma:
     *   servicerca://reset-password?oobCode=XXXX
     * o bien un Firebase Dynamic Link con el mismo query param.
     */
    private fun handleIntent(intent: Intent?) {
        val data: Uri = intent?.data ?: return
        val oobCode = data.getQueryParameter("oobCode") ?: return
        _pendingOobCode.value = oobCode
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    ServiCercaTheme {

    }
}