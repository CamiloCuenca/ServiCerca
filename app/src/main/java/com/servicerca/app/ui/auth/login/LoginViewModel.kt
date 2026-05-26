package com.servicerca.app.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.fcm.FCMTokenManager
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val fcmTokenManager: FCMTokenManager
) : ViewModel() {

    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    /**
     * Flujo que representa el resultado de la operación de login.
     * Puede ser Success, Failure o null si no hay una operación activa.
     */
    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Campo validado para el correo electrónico.
     */
    val email = ValidatedField("") { value ->
        val trimmedValue = value.trim()
        when {
            trimmedValue.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(trimmedValue).matches() -> "Ingresa un email válido"
            else -> null
        }
    }


    /**
     * Campo validado para la contraseña.
     */
    val password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Propiedad que indica si el formulario actual es válido basándose en las reglas de email y password.
     */
    val isFormValid: Boolean
        get() = email.isValid
                && password.isValid

    /**
     * Ejecuta el proceso de inicio de sesión usando el UserRepository.
     * Si el formulario es válido, se consulta el repositorio y se actualiza el estado según el resultado.
     */
    fun login() {
        if (!isFormValid) {
            _loginResult.value =
                RequestResult.Failure("Por favor, completa los campos correctamente")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = repository.login(email.value.trim(), password.value)

                if (user != null) {
                    if (!user.isEmailVerified) {
                        _loginResult.value = RequestResult.Failure("Por favor, verifica tu correo electrónico antes de iniciar sesión.")
                    } else {
                        // Registrar token FCM del dispositivo para poder recibir notificaciones push
                        fcmTokenManager.saveTokenForUser(user.id)
                        repository.updateOnlineStatus(user.id, true)
                        _loginResult.value = RequestResult.SuccessLogin(user.id, user.role)
                    }
                } else {
                    _loginResult.value = RequestResult.Failure("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _loginResult.value = RequestResult.Failure("Error en el inicio de sesión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ejecuta el proceso de inicio de sesión con Google usando el UserRepository.
     */
    fun loginWithGoogle(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = repository.googleSignIn(idToken)

                if (user != null) {
                    // Registrar token FCM del dispositivo para poder recibir notificaciones push
                    fcmTokenManager.saveTokenForUser(user.id)
                    repository.updateOnlineStatus(user.id, true)
                    _loginResult.value = RequestResult.SuccessLogin(user.id, user.role)
                } else {
                    _loginResult.value = RequestResult.Failure("No se pudo iniciar sesión con Google")
                }
            } catch (e: Exception) {
                _loginResult.value = RequestResult.Failure("Error en el inicio de sesión con Google: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reinicia los campos del formulario a sus valores iniciales.
     * Útil después de un login exitoso o al cerrar sesión.
     */
    fun resetForm() {
        email.reset()
        password.reset()
    }

    /**
     * Limpia el estado del resultado de login para evitar que se procese múltiples veces (e.g. al rotar pantalla).
     */
    fun resetLoginResult(){
        _loginResult.value = null
    }

}
