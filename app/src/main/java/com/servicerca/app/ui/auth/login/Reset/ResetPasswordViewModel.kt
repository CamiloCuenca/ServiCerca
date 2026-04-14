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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel( ) {

    private val email: String = checkNotNull(savedStateHandle["email"])



    private val _resetyResult = MutableStateFlow<RequestResult?>(null)

    val resetResult: StateFlow<RequestResult?> = _resetyResult.asStateFlow()

    val codeReset  = ValidatedField("") { value ->

        when {
            value.isEmpty() -> "El codigo de recuperación es obligatorio"
            value.length != 6 -> "El codigo de recuperación debe tener 6 digitos"
            else -> null
        }

    }

    val newPassword = ValidatedField(""){ value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = codeReset.isValid
                && newPassword.isValid

    fun reset() {
        if (!isFormValid) {
            _resetyResult.value =
                RequestResult.Failure("Por favor, completa los campos correctamente")
            return
        }

        viewModelScope.launch {
            val result = userRepository.resetPassword(email, codeReset.value, newPassword.value)
            if (result.isSuccess) {
                _resetyResult.value = RequestResult.Success("¡Contraseña restablecida!")
            } else {
                _resetyResult.value = RequestResult.Failure(result.exceptionOrNull()?.message ?: "Error al restablecer contraseña")
            }
        }
    }


    fun resetResult(){
        _resetyResult.value = null
    }
}