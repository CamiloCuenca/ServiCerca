package com.servicerca.app

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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            languageManager.applySavedLanguage()
        }
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
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    ServiCercaTheme {

    }
}