package com.servicerca.app.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.ValidatedField

class RegisterViewModel : ViewModel() {

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
            value.isEmpty() -> "El nombre es obligatoria"
            else -> null
        }
    }

    val SecondName = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatoria"
            else -> null
        }
    }

    val Lastname = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatoria"
            else -> null
        }
    }

    val SecondLastname = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatoria"
            else -> null
        }
    }





    val isFormValid: Boolean
        get() = email.isValid
                && password.isValid

    // Es útil para resetear el formulario después de un login exitoso
    fun resetForm() {
        email.reset()
        password.reset()
    }
}