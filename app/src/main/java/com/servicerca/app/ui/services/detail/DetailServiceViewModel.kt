package com.servicerca.app.ui.services.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel encargado de gestionar el estado de la pantalla de detalle de un servicio.
 *
 * Orquesta dos repositorios:
 *  - [ServiceRepository] para obtener la información del servicio.
 *  - [CommentRepository] para cargar y guardar comentarios asociados al servicio.
 *
 * Nota: actualmente los datos se manejan en memoria (mock). Esta estructura está preparada
 * para conectarse a Firebase u otra fuente de datos sin cambiar la interfaz pública.
 */
@HiltViewModel
class DetailServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    // ── Estado del servicio ──────────────────────────────────────────────────

    private val _service = MutableStateFlow<Service?>(null)
    /** Servicio actualmente seleccionado. Null mientras carga o si no se encontró. */
    val service: StateFlow<Service?> = _service.asStateFlow()

    // ── Estado de los comentarios ────────────────────────────────────────────

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    /** Lista de comentarios filtrada por el servicio cargado. */
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // ── Rating promedio calculado ────────────────────────────────────────────

    private val _averageRating = MutableStateFlow(0f)
    /** Promedio de calificaciones de los comentarios del servicio. */
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()

    // ── Carga inicial ────────────────────────────────────────────────────────

    /**
     * Carga el servicio y sus comentarios asociados por [serviceId].
     * Debe llamarse desde un LaunchedEffect en la pantalla.
     */
    fun loadService(serviceId: String) {
        viewModelScope.launch {
            _service.value = serviceRepository.findById(serviceId)
            loadComments(serviceId)
        }
    }

    /**
     * Carga (o recarga) los comentarios asociados al [serviceId] indicado.
     * Se llama internamente después de cada operación de escritura para mantener
     * la UI sincronizada con el estado del repositorio.
     */
    private fun loadComments(serviceId: String) {
        val allComments = commentRepository.comments.value
        val filtered = allComments.filter { it.serviceId == serviceId }
        _comments.value = filtered
        _averageRating.value = if (filtered.isEmpty()) 0f
        else filtered.map { it.rating }.average().toFloat()
    }

    // ── Operaciones de escritura ─────────────────────────────────────────────

    /**
     * Agrega un nuevo comentario al servicio actual.
     *
     * @param userId      ID del usuario que comenta.
     * @param userName    Nombre visible del usuario.
     * @param userAvatar  URL del avatar del usuario.
     * @param rating      Calificación de 1 a 5 estrellas.
     * @param text        Texto del comentario.
     */
    fun addComment(
        userId: String,
        userName: String,
        userAvatar: String,
        rating: Int,
        text: String
    ) {
        val serviceId = _service.value?.id ?: return
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            userId = userId,
            serviceId = serviceId,
            userName = userName,
            userAvatar = userAvatar,
            rating = rating,
            text = text,
            date = System.currentTimeMillis(),
            timeAgo = "Ahora"
        )
        viewModelScope.launch {
            commentRepository.save(comment)
            // Recargamos la lista filtrada para reflejar el nuevo comentario en la UI
            loadComments(serviceId)
        }
    }

    /**
     * Elimina un comentario por su [commentId].
     * Solo el autor o un administrador debería poder invocar esta función.
     */
    fun deleteComment(commentId: String) {
        val serviceId = _service.value?.id ?: return
        viewModelScope.launch {
            commentRepository.delete(commentId)
            loadComments(serviceId)
        }
    }
}
