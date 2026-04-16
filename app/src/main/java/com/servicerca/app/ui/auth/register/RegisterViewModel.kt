package com.servicerca.app.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {



    private val _RegisterResult = MutableStateFlow<RequestResult?>(null)

    val registerResult: StateFlow<RequestResult?> = _RegisterResult.asStateFlow()

    fun resetLoginResult(){
        _RegisterResult.value = null
    }


    val email = ValidatedField("") { value ->
        val trimmedValue = value.trim()
        when {
            trimmedValue.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(trimmedValue).matches() -> "Ingresa un email válido"
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
            value.isEmpty() -> null // Opcional
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

    val city = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La ciudad es obligatoria"
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
        city.touch()
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
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    name1 = name.value,
                    name2 = SecondName.value,
                    lastname1 = Lastname.value,
                    lastname2 = SecondLastname.value,
                    city = city.value,

                    address = address.value,
                    email = email.value.trim(),
                    password = password.value,
                    profilePictureUrl = "https://cdn-icons-png.flaticon.com/512/149/149071.png", // Avatar por defecto
                )

                userRepository.save(newUser)
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
                && city.isValid
                && category.isValid


    // Es útil para resetear el formulario después de un login exitoso
    fun resetForm() {
        email.reset()
        password.reset()
        category.reset()
    }
}
