package com.servicerca.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    // ── Campos del formulario (todos opcionales) ──────────────────────────────
    // Validación: solo si el usuario escribió algo, se valida el formato.
    // Un campo en blanco significa "no quiero cambiar este dato".

    val firstName = ValidatedField("") { value ->
        when {
            value.isNotBlank() && !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) ->
                "El nombre solo puede contener letras"
            else -> null
        }
    }

    val middleName = ValidatedField("") { value ->
        when {
            value.isNotBlank() && !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) ->
                "El nombre solo puede contener letras"
            else -> null
        }
    }

    val firstLastName = ValidatedField("") { value ->
        when {
            value.isNotBlank() && !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) ->
                "El apellido solo puede contener letras"
            else -> null
        }
    }

    val secondLastName = ValidatedField("") { value ->
        when {
            value.isNotBlank() && !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) ->
                "El apellido solo puede contener letras"
            else -> null
        }
    }

    val address = ValidatedField("") { _ -> null }

    val phone = ValidatedField("") { value ->
        // Solo valida formato si el usuario escribió algo
        when {
            value.isNotBlank() && value.length < 7 -> "Ingresa un número de teléfono válido (mínimo 7 dígitos)"
            else -> null
        }
    }

    // ── Estado de la operación ────────────────────────────────────────────────

    private val _saveResult = MutableStateFlow<RequestResult?>(null)
    val saveResult: StateFlow<RequestResult?> = _saveResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Lógica ────────────────────────────────────────────────────────────────

    /**
     * Retorna true si al menos un campo tiene contenido.
     * No tiene sentido guardar si no se cambió nada.
     */
    val hasChanges: Boolean
        get() = firstName.value.isNotBlank()
                || middleName.value.isNotBlank()
                || firstLastName.value.isNotBlank()
                || secondLastName.value.isNotBlank()
                || address.value.isNotBlank()
                || phone.value.isNotBlank()

    val isFormValid: Boolean
        get() = firstName.isValid
                && middleName.isValid
                && firstLastName.isValid
                && secondLastName.isValid
                && phone.isValid

    /**
     * Guarda únicamente los campos que el usuario modificó.
     * En una implementación real, aquí iría la llamada al repositorio
     * pasando solo los campos no vacíos.
     */
    fun saveProfile() {
        // Validar formato de teléfono si se escribió algo
        phone.touch()

        if (!isFormValid) {
            _saveResult.value = RequestResult.Failure("Verifica el formato del teléfono")
            return
        }

        if (!hasChanges) {
            _saveResult.value = RequestResult.Failure("No has realizado ningún cambio")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio con solo los campos modificados:
                // val updatedFields = buildMap {
                //     if (firstName.value.isNotBlank()) put("firstName", firstName.value)
                //     if (phone.value.isNotBlank())     put("phone", phone.value)
                //     ...
                // }
                _saveResult.value = RequestResult.Success("Perfil actualizado correctamente")
            } catch (e: Exception) {
                _saveResult.value = RequestResult.Failure("Error al guardar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    fun resetForm() {
        firstName.reset()
        middleName.reset()
        firstLastName.reset()
        secondLastName.reset()
        address.reset()
        phone.reset()
    }
}
