package com.servicerca.app.ui.auth.login.Reset

import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.servicerca.app.ai.ToxicityRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de restablecimiento de contraseña.
 * Recibe el oobCode de Firebase a través del deep link (vía navegación)
 * y gestiona la validación de la nueva contraseña antes de llamar a
 * [UserRepository.resetPassword].
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _resetResult = MutableStateFlow<RequestResult?>(null)
    val resetResult: StateFlow<RequestResult?> = _resetResult.asStateFlow()

    /** Nueva contraseña — mínimo 6 caracteres */
    val newPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /** Confirmación de contraseña — debe coincidir con newPassword */
    val confirmPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "Confirma tu contraseña"
            value != newPassword.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = newPassword.isValid && confirmPassword.isValid

    /**
     * Restablece la contraseña usando el [oobCode] de Firebase.
     * El oobCode es el token que llega en el deep link del email de recuperación.
     */
    fun reset(oobCode: String) {
        // Forzar visualización de errores en ambos campos antes de enviar
        newPassword.touch()
        confirmPassword.touch()
        if (!isFormValid) {
            _resetResult.value = RequestResult.Failure("Por favor, completa los campos correctamente")
            return
        }

        viewModelScope.launch {
            // Validación de IA para contenido ofensivo
            if (ToxicityRepository.isToxic(newPassword.value)) {
                _resetResult.value = RequestResult.Failure("Contenido ofensivo detectado")
                return@launch
            }

            val result = userRepository.resetPassword("", oobCode, newPassword.value)
            if (result.isSuccess) {
                _resetResult.value = RequestResult.Success("¡Contraseña restablecida exitosamente!")
            } else {
                _resetResult.value = RequestResult.Failure(
                    result.exceptionOrNull()?.message ?: "Error al restablecer la contraseña"
                )
            }
        }
    }

    fun clearResult() {
        _resetResult.value = null
    }
}