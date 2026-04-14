package com.servicerca.app.ui.auth.login.Recover

import android.util.Patterns
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
import kotlinx.coroutines.launch

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel( ){


    private val _recoverResult = MutableStateFlow<RequestResult?>(null)


    val recoverResult: StateFlow<RequestResult?> = _recoverResult.asStateFlow()

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = email.isValid

    fun recover() {
        if (!isFormValid) {
            _recoverResult.value =
                RequestResult.Failure("Por favor, completa los campos correctamente")
            return
        }

        viewModelScope.launch {
            val result = userRepository.initiatePasswordRecovery(email.value.trim())
            if (result.isSuccess) {
                _recoverResult.value = RequestResult.Success("¡Instrucciones enviadas!")
            } else {
                _recoverResult.value = RequestResult.Failure(result.exceptionOrNull()?.message ?: "Error al procesar la solicitud")
            }
        }
    }


    fun resetRecoverResult(){
        _recoverResult.value = null
    }

}