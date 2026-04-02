package com.servicerca.app.ui.services.create

import android.content.Context
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Location
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val sessionDataStore: SessionDataStore,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    // ── Categorías disponibles ─────────────────────────────────────────────
    val categories = listOf(
        "Plomería",
        "Electricidad",
        "Carpintería",
        "Pintura",
        "Jardinería",
        "Limpieza",
        "Mudanzas",
        "Cerrajería",
        "Otro"
    )

    // ── Campos del formulario ──────────────────────────────────────────────

    val title = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El título es obligatorio"
            value.length < 5 -> "El título debe tener al menos 5 caracteres"
            else -> null
        }
    }

    val category = ValidatedField("") { value ->
        when {
            value.isBlank() -> "Selecciona una categoría"
            else -> null
        }
    }

    val description = ValidatedField("") { value ->
        when {
            value.isBlank() -> "La descripción es obligatoria"
            value.length < 20 -> "La descripción debe tener al menos 20 caracteres"
            else -> null
        }
    }

    val minValue = ValidatedField("") { value ->
        val min = value.toDoubleOrNull()
        when {
            value.isBlank() -> "El precio mínimo es obligatorio"
            min == null -> "Ingresa un valor numérico válido"
            min < 0 -> "El precio no puede ser negativo"
            else -> null
        }
    }

    val maxValue = ValidatedField("") { value ->
        val max = value.toDoubleOrNull()
        val min = minValue.value.toDoubleOrNull()
        when {
            value.isBlank() -> "El precio máximo es obligatorio"
            max == null -> "Ingresa un valor numérico válido"
            max < 0 -> "El precio no puede ser negativo"
            min != null && max < min -> "El precio máximo debe ser mayor al mínimo"
            else -> null
        }
    }

    // ── Imágenes temporales en memoria (ByteArray) ─────────────────────────
    private val _images = MutableStateFlow<List<ByteArray>>(emptyList())
    val images: StateFlow<List<ByteArray>> = _images.asStateFlow()

    fun addImage(bytes: ByteArray) {
        if (_images.value.size >= 5) {
            _createResult.value = RequestResult.Failure("Máximo 5 imágenes permitidas")
            return
        }
        _images.value = _images.value + bytes
    }

    fun removeImageAt(index: Int) {
        if (index !in _images.value.indices) return
        val mutable = _images.value.toMutableList()
        mutable.removeAt(index)
        _images.value = mutable
    }

    fun clearImages() {
        _images.value = emptyList()
    }

    // ── Estado de la operación ─────────────────────────────────────────────

    private val _createResult = MutableStateFlow<RequestResult?>(null)
    val createResult: StateFlow<RequestResult?> = _createResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Validación global ──────────────────────────────────────────────────

    val isFormValid: Boolean
        get() = title.isValid
                && category.isValid
                && description.isValid
                && minValue.isValid
                && maxValue.isValid
                && _images.value.size >= 1

    // ── Acciones ──────────────────────────────────────────────────────────

    /**
     * Publica el servicio. Valida todos los campos antes de proceder.
     * En una implementación real, aquí va la llamada al repositorio.
     */
    fun createService() {
        // Forzar validación visual de todos los campos
        title.touch()
        category.touch()
        description.touch()
        minValue.touch()
        maxValue.touch()

        // Touch images -> forzar error si no hay imágenes
        if (_images.value.isEmpty()) {
            _createResult.value = RequestResult.Failure("Agrega al menos una imagen")
            return
        }

        if (!isFormValid) {
            _createResult.value = RequestResult.Failure("Por favor corrige los errores antes de publicar")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener sesión actual
                val session = sessionDataStore.sessionFlow.first()
                val ownerId = session?.userId
                if (ownerId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure("Debes iniciar sesión para publicar")
                    _isLoading.value = false
                    return@launch
                }

                // Construir service
                val id = UUID.randomUUID().toString()
                // Guardar la primera imagen en un archivo temporal en cache y usar su URI
                val firstImage = _images.value.first()
                val cacheFile = File(context.cacheDir, "service_$id.jpg")
                cacheFile.writeBytes(firstImage)
                val fileUri = "file://${cacheFile.absolutePath}"

                val service = Service(
                    id = id,
                    title = title.value,
                    description = description.value,
                    location = Location(0.0, 0.0), // TODO: obtener ubicación real desde UI
                    priceMin = minValue.value.toDouble(),
                    priceMax = maxValue.value.toDouble(),
                    status = ServiceStatus.PENDING,
                    type = category.value,
                    photoUrl = fileUri,
                    ownerId = ownerId
                )

                // Log para depuración: verificar el archivo generado
                Log.d("CreateServiceVM", "wrote image to fileUri=$fileUri size=${cacheFile.length()}")

                // Guardar en repositorio
                serviceRepository.save(service)

                _createResult.value = RequestResult.Success("¡Servicio publicado exitosamente!")
                // Limpiar formulario
                resetForm()
                clearImages()
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure("Error al publicar el servicio: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCreateResult() {
        _createResult.value = null
    }

    fun resetForm() {
        title.reset()
        category.reset()
        description.reset()
        minValue.reset()
        maxValue.reset()
    }
}