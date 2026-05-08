package com.servicerca.app.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.BuildConfig
import com.servicerca.app.core.cloudinary.CloudinaryUploader
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore,
    @ApplicationContext private val context: Context
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
        // Teléfono ahora es opcional
        if (value.isNotBlank() && value.length < 7) "Ingresa un número de teléfono válido" else null
    }

    val city = ValidatedField("") { value ->
        if (value.isBlank()) "La ciudad es obligatoria" else null
    }

    private val _profilePictureUrl = MutableStateFlow("")
    val profilePictureUrl: StateFlow<String> = _profilePictureUrl.asStateFlow()

    // Bytes de la imagen seleccionada; null si el usuario no cambió la foto
    private var pendingImageBytes: ByteArray? = null

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
                        firstName.loadInitialValue(user.name1)
                        middleName.loadInitialValue(user.name2 ?: "")
                        firstLastName.loadInitialValue(user.lastname1)
                        secondLastName.loadInitialValue(user.lastname2 ?: "")
                        address.loadInitialValue(user.address)
                        phone.loadInitialValue(user.phoneNumber)
                        city.loadInitialValue(user.city)
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
                && firstLastName.isValid
                && address.isValid
                && city.isValid

    fun saveProfile() {
        firstName.touch()
        firstLastName.touch()
        address.touch()
        city.touch()

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
                        // Si hay una imagen nueva pendiente, subirla a Cloudinary primero
                        val bytes = pendingImageBytes
                        if (bytes != null) {
                            val uploadResult = CloudinaryUploader.uploadImage(
                                imageBytes = bytes,
                                cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME,
                                uploadPreset = BuildConfig.CLOUDINARY_UPLOAD_PRESET
                            )
                            if (uploadResult.isSuccess) {
                                _profilePictureUrl.value = uploadResult.getOrThrow()
                                pendingImageBytes = null
                            } else {
                                _saveResult.value = RequestResult.Failure(
                                    "Error al subir la imagen: ${uploadResult.exceptionOrNull()?.message}"
                                )
                                _isLoading.value = false
                                return@launch
                            }
                        }

                        val updatedUser = currentUser.copy(
                            name1 = firstName.value,
                            name2 = middleName.value.ifBlank { null },
                            lastname1 = firstLastName.value,
                            lastname2 = secondLastName.value.ifBlank { null },
                            address = address.value,
                            phoneNumber = phone.value,
                            city = city.value,
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

    fun onPictureChanged(imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull() ?: return@launch
                // Guardar bytes para subirlos en saveProfile()
                pendingImageBytes = imageBytes
                // Previsualizar localmente mientras no se guarda
                val cacheFile = File(context.cacheDir, "profile_edit_${session.userId}.jpg")
                cacheFile.writeBytes(imageBytes)
                _profilePictureUrl.value = "file://${cacheFile.absolutePath}"
            } catch (e: Exception) {
                // Error silencioso al previsualizar
            }
        }
    }
}
