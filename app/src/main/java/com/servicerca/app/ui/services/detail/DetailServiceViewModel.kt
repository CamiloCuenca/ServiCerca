package com.servicerca.app.ui.services.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.CommentRepository
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _service = MutableStateFlow<Service?>(null)
    val service: StateFlow<Service?> = _service.asStateFlow()

    private val _provider = MutableStateFlow<User?>(null)
    val provider: StateFlow<User?> = _provider.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()

    fun loadService(serviceId: String) {
        viewModelScope.launch {
            val serviceResult = serviceRepository.findById(serviceId)
            _service.value = serviceResult
            
            if (serviceResult != null) {
                _provider.value = userRepository.findById(serviceResult.ownerId)
                loadComments(serviceId)
            }
        }
    }

    private fun loadComments(serviceId: String) {
        val allComments = commentRepository.comments.value
        val filtered = allComments.filter { it.serviceId == serviceId }
        _comments.value = filtered
        _averageRating.value = if (filtered.isEmpty()) 0f
        else filtered.map { it.rating }.average().toFloat()
    }

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
            loadComments(serviceId)
        }
    }

    fun deleteComment(commentId: String) {
        val serviceId = _service.value?.id ?: return
        viewModelScope.launch {
            commentRepository.delete(commentId)
            loadComments(serviceId)
        }
    }
}
