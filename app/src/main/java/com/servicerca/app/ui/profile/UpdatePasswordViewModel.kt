package com.servicerca.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdatePasswordViewModel : ViewModel() {

    private val _updatePasswordResult = MutableStateFlow<RequestResult?>(null)
    val updateResult: StateFlow<RequestResult?> = _updatePasswordResult.asStateFlow()

    val currentPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña actual es obligatoria"
            else -> null
        }
    }

    val newPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña nueva es obligatoria"
            value.length < 6 -> "La contraseña nueva debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val confirmNewPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La confirmación de contraseña es obligatoria"
            value.length < 6 -> "La confirmación debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = currentPassword.isValid
                && newPassword.isValid
                && confirmNewPassword.isValid

    fun updatePassword() {
        // Forzar validación visual de todos los campos
        currentPassword.touch()
        newPassword.touch()
        confirmNewPassword.touch()

        if (!isFormValid) {
            _updatePasswordResult.value = RequestResult.Failure("Por favor completa todos los campos")
            return
        }

        //  Usar las variables correctas del ViewModel
        if (newPassword.value != confirmNewPassword.value) {
            _updatePasswordResult.value = RequestResult.Failure("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            try {
                _updatePasswordResult.value = RequestResult.Success("Contraseña actualizada exitosamente")
            } catch (e: Exception) {
                _updatePasswordResult.value = RequestResult.Failure("Error al actualizar: ${e.message}")
            }
        }
    }

    fun resetUpdateResult() {
        _updatePasswordResult.value = null
    }

    // Útil para limpiar el formulario después de una actualización exitosa
    fun resetForm() {
        currentPassword.reset()
        newPassword.reset()
        confirmNewPassword.reset()
    }
}