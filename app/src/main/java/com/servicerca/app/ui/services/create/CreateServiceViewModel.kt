package com.servicerca.app.ui.services.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.BuildConfig
import com.servicerca.app.R
import com.servicerca.app.core.cloudinary.CloudinaryUploader
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionDataStore: SessionDataStore,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

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

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    fun setLocation(latitude: Double, longitude: Double) {
        _selectedLocation.value = Location(latitude, longitude)
    }

    private val _createResult = MutableStateFlow<RequestResult?>(null)
    val createResult: StateFlow<RequestResult?> = _createResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFormValid: Boolean
        get() = title.isValid
                && category.isValid
                && description.isValid
                && minValue.isValid
                && maxValue.isValid
                && _images.value.size >= 1

    fun createService() {
        title.touch()
        category.touch()
        description.touch()
        minValue.touch()
        maxValue.touch()

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
                val session = sessionDataStore.sessionFlow.first()
                val ownerId = session?.userId
                if (ownerId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure(context.getString(R.string.error_login_required_to_publish))
                    _isLoading.value = false
                    return@launch
                }

                // Subir todas las imágenes a Cloudinary antes de guardar el servicio
                val photoUrls = mutableListOf<String>()
                for (imageBytes in _images.value) {
                    val uploadResult = CloudinaryUploader.uploadImage(
                        imageBytes = imageBytes,
                        cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME,
                        uploadPreset = BuildConfig.CLOUDINARY_UPLOAD_PRESET
                    )
                    if (uploadResult.isSuccess) {
                        photoUrls.add(uploadResult.getOrThrow())
                    } else {
                        _createResult.value = RequestResult.Failure(
                            "Error al subir imagen: ${uploadResult.exceptionOrNull()?.message}"
                        )
                        _isLoading.value = false
                        return@launch
                    }
                }

                val id = UUID.randomUUID().toString()
                val service = Service(
                    id = id,
                    title = title.value,
                    description = description.value,
                    location = _selectedLocation.value ?: Location(0.0, 0.0),
                    priceMin = minValue.value.toDouble(),
                    priceMax = maxValue.value.toDouble(),
                    status = ServiceStatus.PENDING,
                    type = category.value,
                    photoUrl = photoUrls.first(),
                    ownerId = ownerId
                )

                serviceRepository.save(service)

                // Notificar a los moderadores del nuevo servicio pendiente
                val notification = Notification(
                    id = UUID.randomUUID().toString(),
                    userId = "MODERATOR_ROLE",
                    title = context.getString(R.string.notification_moderation_title),
                    message = context.getString(R.string.notification_moderation_message, title.value),
                    date = "Ahora",
                    imageRes = R.drawable.nueva_solicitud_servicio,
                    notificationType = NotificationType.MODERATION,
                    targetId = id
                )
                notificationRepository.addNotification(notification)

                _createResult.value = RequestResult.Success(context.getString(R.string.service_published_success))
                resetForm()
                clearImages()
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure(
                    context.getString(R.string.error_publishing_service, e.message ?: "")
                )
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
        _selectedLocation.value = null
    }
}
