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
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // ── Campos del formulario ──────────────────────────────────────────────

    val firstName = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El primer nombre es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El nombre solo puede contener letras"
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
            value.isBlank() -> "El primer apellido es obligatorio"
            !value.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) -> "El apellido solo puede contener letras"
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

    val address = ValidatedField("") { value ->
        if (value.isBlank()) "La dirección es obligatoria" else null
    }

    val phone = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El teléfono es obligatorio"
            value.length < 7 -> "Ingresa un número de teléfono válido"
            else -> null
        }
    }

    private val _profilePictureUrl = MutableStateFlow("")
    val profilePictureUrl: StateFlow<String> = _profilePictureUrl.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _role = MutableStateFlow("")
    val role: StateFlow<String> = _role.asStateFlow()

    // ── Estado de la operación ────────────────────────────────────────────────

    private val _saveResult = MutableStateFlow<RequestResult?>(null)
    val saveResult: StateFlow<RequestResult?> = _saveResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                if (session != null) {
                    val user = userRepository.findById(session.userId)
                    if (user != null) {
                        firstName.onChange(user.name1)
                        middleName.onChange(user.name2 ?: "")
                        firstLastName.onChange(user.lastname1)
                        secondLastName.onChange(user.lastname2 ?: "")
                        address.onChange(user.address)
                        phone.onChange(user.phoneNumber)
                        _profilePictureUrl.value = user.profilePictureUrl
                        _email.value = user.email
                        _role.value = user.role.name
                    }
                }
            } catch (e: Exception) {
                // Error silencioso al cargar
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Lógica ────────────────────────────────────────────────────────────────

    val isFormValid: Boolean
        get() = firstName.isValid
                && middleName.isValid
                && firstLastName.isValid
                && secondLastName.isValid
                && address.isValid
                && phone.isValid

    fun saveProfile() {
        firstName.touch()
        firstLastName.touch()
        address.touch()
        phone.touch()

        if (!isFormValid) {
            _saveResult.value = RequestResult.Failure("Completa los campos obligatorios correctamente")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                if (session != null) {
                    val currentUser = userRepository.findById(session.userId)
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(
                            name1 = firstName.value,
                            name2 = middleName.value.ifBlank { null },
                            lastname1 = firstLastName.value,
                            lastname2 = secondLastName.value.ifBlank { null },
                            address = address.value,
                            phoneNumber = phone.value,
                            profilePictureUrl = _profilePictureUrl.value
                        )
                        userRepository.save(updatedUser)
                        _saveResult.value = RequestResult.Success("Perfil actualizado correctamente")
                    }
                }
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
}
