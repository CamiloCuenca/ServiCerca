package com.servicerca.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _updatePasswordResult = MutableStateFlow<RequestResult?>(null)
    val updateResult: StateFlow<RequestResult?> = _updatePasswordResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
            _isLoading.value = true
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                if (session == null) {
                    _updatePasswordResult.value = RequestResult.Failure("Sesión no válida")
                    _isLoading.value = false
                    return@launch
                }

                val result = userRepository.updatePassword(
                    userId = session.userId,
                    currentPassword = currentPassword.value,
                    newPassword = newPassword.value
                )

                result.onSuccess {
                    _updatePasswordResult.value = RequestResult.Success("Contraseña actualizada exitosamente")
                }.onFailure { e ->
                    _updatePasswordResult.value = RequestResult.Failure(e.message ?: "Error al actualizar")
                }
            } catch (e: Exception) {
                _updatePasswordResult.value = RequestResult.Failure("Error al actualizar: ${e.message}")
            } finally {
                _isLoading.value = false
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

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }
}