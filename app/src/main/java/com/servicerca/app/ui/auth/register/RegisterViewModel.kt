package com.servicerca.app.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {


    private val _RegisterResult = MutableStateFlow<RequestResult?>(null)

    val registerResult: StateFlow<RequestResult?> = _RegisterResult.asStateFlow()

    fun resetLoginResult(){
        _RegisterResult.value = null
    }


    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val confirmPassword = ValidatedField("") { value ->

        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val name = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El nombre solo puede contener letras"
            else -> null
        }
    }

    val SecondName = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El segundo nombre es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El nombre solo puede contener letras"
            else -> null
        }
    }

    val Lastname = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El apellido es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El apellido solo puede contener letras"
            else -> null
        }
    }

    val SecondLastname = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El segundo apellido es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El apellido solo puede contener letras"
            else -> null
        }
    }

    val address = ValidatedField("") { value ->
        when{
            value.isEmpty() -> "La dirección es obligatoria"
            else -> null
        }
    }

    val category = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La categoría es obligatoria"
            else -> null
        }
    }

    fun register() {
        // Forzar validación visual de todos los campos
        name.touch()
        SecondName.touch()
        Lastname.touch()
        SecondLastname.touch()
        address.touch()
        category.touch()
        email.touch()
        password.touch()
        confirmPassword.touch()

        if (!isFormValid) {
            _RegisterResult.value = RequestResult.Failure("Por favor completa todos los campos")
            return
        }

        if (password.value != confirmPassword.value) {
            _RegisterResult.value = RequestResult.Failure("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            try {
                _RegisterResult.value = RequestResult.Success("Registro exitoso")
            } catch (e: Exception) {
                _RegisterResult.value = RequestResult.Failure("Error al registrarse: ${e.message}")
            }
        }
    }





    val isFormValid: Boolean
        get() = email.isValid
                && password.isValid
                && confirmPassword.isValid
                && name.isValid
                && SecondName.isValid
                && Lastname.isValid
                && SecondLastname.isValid
                && address.isValid
                && category.isValid

    // Es útil para resetear el formulario después de un login exitoso
    fun resetForm() {
        email.reset()
        password.reset()
        category.reset()
    }
}
