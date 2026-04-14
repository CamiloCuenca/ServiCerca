package com.servicerca.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.servicerca.app.data.model.UserSession
import com.servicerca.app.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionDataStore @Inject constructor(
    @param:ApplicationContext val context: Context
) {
    // Claves para las preferencias
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
    }

    // Flujo para observar los datos de la sesión
    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        // Intenta obtener los datos de la sesión desde Data Store
        val userId = prefs[Keys.USER_ID]
        val roleStr = prefs[Keys.ROLE]

        // Si alguno de los datos es nulo, retorna null
        if (userId.isNullOrBlank() || roleStr.isNullOrBlank()) {
            null
        } else {
            // Retorna un objeto UserSession con los datos obtenidos si existen
            UserSession(
                userId = userId,
                role = UserRole.valueOf(roleStr)
            )
        }
    }

    suspend fun saveSession(userId: String, role: UserRole) {
        // Guarda los datos de la sesión en Data Store (clave-valor)
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.ROLE] = role.name
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}