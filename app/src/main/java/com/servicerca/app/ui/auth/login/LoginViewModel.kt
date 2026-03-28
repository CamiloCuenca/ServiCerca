package com.servicerca.app.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel encargado de gestionar la lógica de negocio y el estado de la pantalla de Login.
 * Maneja la validación de campos, el estado de la autenticación y los resultados de las peticiones.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    /**
     * Flujo que representa el resultado de la operación de login.
     * Puede ser Success, Failure o null si no hay una operación activa.
     */
    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

    /**
     * Campo validado para el correo electrónico.
     */
    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
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

        val user = repository.login(email.value, password.value)
        _loginResult.value = if (user != null) {
            // Si el usuario es ADMIN navegamos al panel de moderador
            if (user.role == UserRole.ADMIN) {
                RequestResult.SuccessLogin(user.id, user.role)
            } else {
                RequestResult.SuccessLogin(user.id, user.role)
            }
        } else {
            RequestResult.Failure("Credenciales inválidas")
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
