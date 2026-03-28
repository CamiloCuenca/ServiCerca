package com.servicerca.app.ui.services.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateServiceViewModel : ViewModel() {

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

        if (!isFormValid) {
            _createResult.value = RequestResult.Failure("Por favor corrige los errores antes de publicar")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio cuando se implemente la lógica real
                // repository.createService(
                //     title = title.value,
                //     category = category.value,
                //     description = description.value,
                //     minPrice = minValue.value.toDouble(),
                //     maxPrice = maxValue.value.toDouble()
                // )
                _createResult.value = RequestResult.Success("¡Servicio publicado exitosamente!")
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