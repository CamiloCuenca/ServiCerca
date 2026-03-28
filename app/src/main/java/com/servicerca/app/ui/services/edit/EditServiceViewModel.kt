package com.servicerca.app.ui.services.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditServiceViewModel : ViewModel() {

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

    // ── Validación global ──────────────────────────────────────────────────

    /** true si al menos un campo fue modificado */
    val hasChanges: Boolean
        get() = title.value.isNotBlank()
                || category.value.isNotBlank()
                || description.value.isNotBlank()
                || minValue.value.isNotBlank()
                || maxValue.value.isNotBlank()

    /** true si todos los campos con contenido tienen formato válido */
    val isFormValid: Boolean
        get() = title.isValid
                && category.isValid
                && description.isValid
                && minValue.isValid
                && maxValue.isValid

    // ── Acciones ──────────────────────────────────────────────────────────

    /**
     * Pre-carga los datos actuales del servicio en los campos del formulario.
     * Se llama al abrir la pantalla pasando los datos del servicio a editar.
     * TODO: recibir un objeto Service del repositorio en lugar de parámetros individuales.
     */
    fun loadService(
        title: String,
        category: String,
        description: String,
        minPrice: String,
        maxPrice: String
    ) {
        this.title.onChange(title)
        this.category.onChange(category)
        this.description.onChange(description)
        this.minValue.onChange(minPrice)
        this.maxValue.onChange(maxPrice)
        // Reseteamos showError tras cargar para no mostrar errores inmediatamente
        this.title.reset(); this.title.onChange(title)
        this.category.reset(); this.category.onChange(category)
        this.description.reset(); this.description.onChange(description)
        this.minValue.reset(); this.minValue.onChange(minPrice)
        this.maxValue.reset(); this.maxValue.onChange(maxPrice)
    }

    /**
     * Guarda los cambios del servicio. Valida todos los campos antes de proceder.
     */
    fun saveService() {
        // Validar solo los campos que tienen contenido
        title.touch(); category.touch(); description.touch()
        minValue.touch(); maxValue.touch()

        if (!hasChanges) {
            _saveResult.value = RequestResult.Failure("No has realizado ningún cambio")
            return
        }

        if (!isFormValid) {
            _saveResult.value = RequestResult.Failure("Por favor corrige los errores antes de guardar")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio con solo los campos modificados
                _saveResult.value = RequestResult.Success("Servicio actualizado correctamente")
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio cuando se implemente la lógica real
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