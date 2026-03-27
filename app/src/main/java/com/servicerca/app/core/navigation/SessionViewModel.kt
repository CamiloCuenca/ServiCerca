package com.servicerca.app.core.navigation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.data.model.UserSession
import com.servicerca.app.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define los posibles estados de la sesión
sealed interface SessionState {
    data object Loading : SessionState
    data object NotAuthenticated : SessionState
    data class Authenticated(val session: UserSession) : SessionState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // Flujo que representa el estado de la sesión
    val sessionState: StateFlow<SessionState> = sessionDataStore.sessionFlow
        .map { session -> // Mapea la sesión a un estado de sesión
            if (session != null) {
                SessionState.Authenticated(session)
            } else {
                SessionState.NotAuthenticated
            }
        }
        .stateIn( // Convierte el flujo en un StateFlow para que pueda ser observado
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionState.Loading
        )

    fun login(userId: String, role: UserRole) {
        // Guarda la sesión del usuario en Data Store. Se utiliza viewModelScope para lanzar la corrutina.
        viewModelScope.launch {
            sessionDataStore.saveSession(userId, role)
        }
    }

    fun logout() {
        // Limpia la sesión del usuario en Data Store. Se utiliza viewModelScope para lanzar la corrutina.
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }
}