package com.servicerca.app.ui.auth.login.Recover

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecoverPasswordViewModel : ViewModel( ){

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
        } else {
            _recoverResult.value =
                RequestResult.Success("¡Instrucciones enviadas!")
        }

    }

    fun resetRecoverResult(){
        _recoverResult.value = null
    }

}