package com.servicerca.app.core.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.servicerca.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {

    val selectedLanguageTag: Flow<String> = settingsDataStore.languageTagFlow

    suspend fun applySavedLanguage() {
        applyLanguageTag(settingsDataStore.getLanguageTag())
    }

    suspend fun setLanguage(tag: String) {
        settingsDataStore.saveLanguageTag(tag)
        applyLanguageTag(tag)
    }

    private fun applyLanguageTag(tag: String) {
        val locales = if (tag == SettingsDataStore.SYSTEM_LANGUAGE_TAG) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}

