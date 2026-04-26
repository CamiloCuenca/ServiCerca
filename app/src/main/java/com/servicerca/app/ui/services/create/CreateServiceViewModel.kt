package com.servicerca.app.ui.services.create


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.R
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.core.utils.ValidatedField
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Location
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.model.Notification
import com.servicerca.app.domain.model.NotificationType
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.repository.NotificationRepository
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    // ── Categorías disponibles ─────────────────────────────────────────────
    val categories = listOf(
        context.getString(R.string.category_plumbing),
        context.getString(R.string.category_electricity),
        context.getString(R.string.category_carpentry),
        context.getString(R.string.category_painting),
        context.getString(R.string.category_gardening),
        context.getString(R.string.category_cleaning),
        context.getString(R.string.category_moving),
        context.getString(R.string.category_locksmith),
        context.getString(R.string.category_other)
    )

    // ── Campos del formulario ──────────────────────────────────────────────

    val title = ValidatedField("") { value ->
        when {
            value.isBlank() -> context.getString(R.string.error_title_required)
            value.length < 5 -> context.getString(R.string.error_title_min_length)
            else -> null
        }
    }

    val category = ValidatedField("") { value ->
        when {
            value.isBlank() -> context.getString(R.string.error_select_category)
            else -> null
        }
    }

    val description = ValidatedField("") { value ->
        when {
            value.isBlank() -> context.getString(R.string.error_description_required)
            value.length < 20 -> context.getString(R.string.error_description_min_length)
            else -> null
        }
    }

    val minValue = ValidatedField("") { value ->
        val min = value.toDoubleOrNull()
        when {
            value.isBlank() -> context.getString(R.string.error_min_price_required)
            min == null -> context.getString(R.string.error_valid_numeric_value)
            min < 0 -> context.getString(R.string.error_price_negative)
            else -> null
        }
    }

    val maxValue = ValidatedField("") { value ->
        val max = value.toDoubleOrNull()
        val min = minValue.value.toDoubleOrNull()
        when {
            value.isBlank() -> context.getString(R.string.error_max_price_required)
            max == null -> context.getString(R.string.error_valid_numeric_value)
            max < 0 -> context.getString(R.string.error_price_negative)
            min != null && max < min -> context.getString(R.string.error_max_price_greater_than_min)
            else -> null
        }
    }

    // ── Imágenes temporales en memoria (ByteArray) ─────────────────────────
    private val _images = MutableStateFlow<List<ByteArray>>(emptyList())
    val images: StateFlow<List<ByteArray>> = _images.asStateFlow()

    fun addImage(bytes: ByteArray) {
        if (_images.value.size >= 5) {
            _createResult.value = RequestResult.Failure(context.getString(R.string.error_max_images_allowed))
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
            _createResult.value = RequestResult.Failure(context.getString(R.string.error_add_at_least_one_image))
            return
        }

        if (!isFormValid) {
            _createResult.value = RequestResult.Failure(context.getString(R.string.error_fix_form_before_publish))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener sesión actual
                val session = sessionDataStore.sessionFlow.first()
                val ownerId = session?.userId
                if (ownerId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure(context.getString(R.string.error_login_required_to_publish))
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

                // Notificar a los moderadores
                val notification = Notification(
                    id = UUID.randomUUID().toString(),
                    userId = "MODERATOR_ROLE", // O un ID específico si fuera necesario
                    title = context.getString(R.string.notification_moderation_title),
                    message = context.getString(R.string.notification_moderation_message, title.value),
                    date = "Ahora",
                    imageRes = R.drawable.nueva_solicitud_servicio, // Usar un icono apropiado
                    notificationType = NotificationType.MODERATION,
                    targetId = id
                )
                notificationRepository.addNotification(notification)

                _createResult.value = RequestResult.Success(context.getString(R.string.service_published_success))
                // Limpiar formulario
                resetForm()
                clearImages()
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure(context.getString(R.string.error_publishing_service, e.message ?: ""))
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
