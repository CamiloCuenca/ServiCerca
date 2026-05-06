package com.servicerca.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AppThemeMode {
    SYSTEM_DEFAULT, LIGHT, DARK
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val languageTagFlow: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE_TAG] ?: SYSTEM_LANGUAGE_TAG
    }

    suspend fun saveLanguageTag(tag: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_TAG] = tag
        }
    }

    suspend fun getLanguageTag(): String {
        return languageTagFlow.firstOrNull() ?: SYSTEM_LANGUAGE_TAG
    }

    val themeModeFlow: Flow<AppThemeMode> = context.settingsDataStore.data.map { prefs ->
        val themeStr = prefs[Keys.THEME_MODE]
        if (themeStr != null) {
            try {
                AppThemeMode.valueOf(themeStr)
            } catch (e: Exception) {
                AppThemeMode.SYSTEM_DEFAULT
            }
        } else {
            AppThemeMode.SYSTEM_DEFAULT
        }
    }

    suspend fun saveThemeMode(mode: AppThemeMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    companion object {
        const val SYSTEM_LANGUAGE_TAG = "system"
    }
}

