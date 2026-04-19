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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            languageManager.applySavedLanguage()
        }
        enableEdgeToEdge()

        setContent {
            ServiCercaTheme {
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