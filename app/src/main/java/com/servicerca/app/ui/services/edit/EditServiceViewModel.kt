package com.servicerca.app.ui.services.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditServiceViewModel @Inject constructor(
    private val serviceRepository: com.servicerca.app.domain.repository.ServiceRepository,
    @ApplicationContext private val context: Context
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
    // En edición, los campos se pre-cargan con los valores actuales del servicio.
    // Un campo sin cambios se enviará con el valor original al repositorio.

    val title = ValidatedField("") { value ->
        when {
            value.isNotBlank() && value.length < 5 -> "El título debe tener al menos 5 caracteres"
            else -> null
        }
    }

    val category = ValidatedField("") { _ -> null } // validación implícita: selección en dropdown

    val description = ValidatedField("") { value ->
        when {
            value.isNotBlank() && value.length < 20 -> "La descripción debe tener al menos 20 caracteres"
            else -> null
        }
    }

    val minValue = ValidatedField("") { value ->
        val min = value.toDoubleOrNull()
        when {
            value.isNotBlank() && min == null -> "Ingresa un valor numérico válido"
            value.isNotBlank() && min != null && min < 0 -> "El precio no puede ser negativo"
            else -> null
        }
    }

    val maxValue = ValidatedField("") { value ->
        val max = value.toDoubleOrNull()
        val min = minValue.value.toDoubleOrNull()
        when {
            value.isNotBlank() && max == null -> "Ingresa un valor numérico válido"
            value.isNotBlank() && max != null && max < 0 -> "El precio no puede ser negativo"
            value.isNotBlank() && min != null && max != null && max < min ->
                "El precio máximo debe ser mayor al mínimo"
            else -> null
        }
    }

    // ── Estado de la operación ─────────────────────────────────────────────

    private val _saveResult = MutableStateFlow<RequestResult?>(null)
    val saveResult: StateFlow<RequestResult?> = _saveResult.asStateFlow()

    private val _deleteResult = MutableStateFlow<RequestResult?>(null)
    val deleteResult: StateFlow<RequestResult?> = _deleteResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _images = MutableStateFlow<List<ByteArray>>(emptyList())
    val images: StateFlow<List<ByteArray>> = _images.asStateFlow()

    // ── Validación global ──────────────────────────────────────────────────

    /** true si al menos un campo fue modificado */
    val hasChanges: Boolean
        get() = title.value.isNotBlank()
                || category.value.isNotBlank()
                || description.value.isNotBlank()
                || minValue.value.isNotBlank()
                || maxValue.value.isNotBlank()
                || _images.value.isNotEmpty()

    /** true si todos los campos con contenido tienen formato válido */
    val isFormValid: Boolean
        get() = title.isValid
                && category.isValid
                && description.isValid
                && minValue.isValid
                && maxValue.isValid

    // ── Acciones ──────────────────────────────────────────────────────────

    private var currentService: com.servicerca.app.domain.model.Service? = null

    /**
     * Pre-carga los datos del servicio.
     */
    fun loadService(service: com.servicerca.app.domain.model.Service) {
        currentService = service
        this.title.onChange(service.title)
        this.category.onChange(service.type)
        this.description.onChange(service.description)
        this.minValue.onChange(service.priceMin.toString())
        this.maxValue.onChange(service.priceMax.toString())
        // Si ya hay una imagen en el modelo, podríamos cargarla si fuera ByteArray, 
        // pero como es URL por ahora manejamos las nuevas imágenes.
        
        // Reseteamos showError tras cargar
        this.title.reset(); this.title.onChange(service.title)
        this.category.reset(); this.category.onChange(service.type)
        this.description.reset(); this.description.onChange(service.description)
        this.minValue.reset(); this.minValue.onChange(service.priceMin.toString())
        this.maxValue.reset(); this.maxValue.onChange(service.priceMax.toString())
    }

    fun addImage(image: ByteArray) {
        if (_images.value.size >= 5) return
        _images.value = _images.value + image
    }

    fun removeImageAt(index: Int) {
        if (index !in _images.value.indices) return
        val mutable = _images.value.toMutableList()
        mutable.removeAt(index)
        _images.value = mutable
    }

    /**
     * Guarda los cambios del servicio.
     */
    fun saveService() {
        title.touch(); category.touch(); description.touch()
        minValue.touch(); maxValue.touch()

        if (!isFormValid) {
            _saveResult.value = RequestResult.Failure("Por favor corrige los errores antes de guardar")
            return
        }

        val service = currentService ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                var photoUrl = service.photoUrl
                
                // Si hay nuevas imágenes, guardamos la primera en cache (similar a CreateService)
                if (_images.value.isNotEmpty()) {
                    val firstImage = _images.value.first()
                    val cacheFile = File(context.cacheDir, "service_edit_${service.id}.jpg")
                    cacheFile.writeBytes(firstImage)
                    photoUrl = "file://${cacheFile.absolutePath}"
                }

                val updatedService = service.copy(
                    title = title.value,
                    type = category.value,
                    description = description.value,
                    priceMin = minValue.value.toDoubleOrNull() ?: service.priceMin,
                    priceMax = maxValue.value.toDoubleOrNull() ?: service.priceMax,
                    status = com.servicerca.app.domain.model.ServiceStatus.PENDING,
                    photoUrl = photoUrl
                )
                serviceRepository.update(updatedService)
                _saveResult.value = RequestResult.Success("Servicio actualizado y enviado a revisión")
            } catch (e: Exception) {
                _saveResult.value = RequestResult.Failure("Error al guardar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina el servicio actual.
     */
    fun deleteService() {
        val service = currentService ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                serviceRepository.delete(service.id)
                _deleteResult.value = RequestResult.Success("Servicio eliminado correctamente")
            } catch (e: Exception) {
                _deleteResult.value = RequestResult.Failure("Error al eliminar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveResult() { _saveResult.value = null }
    fun resetDeleteResult() { _deleteResult.value = null }
}