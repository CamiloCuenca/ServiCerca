package com.servicerca.app.data.repository

import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor() : CommentRepository {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    override val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    init {
        _comments.value = fetchComments()
    }

    override suspend fun save(comment: Comment) {
        _comments.value = _comments.value + comment
    }

    override suspend fun update(comment: Comment) {
        _comments.value = _comments.value.map {
            if (it.id == comment.id) comment else it
        }
    }

    override suspend fun delete(id: String) {
        _comments.value = _comments.value.filter { it.id != id }
    }

    override suspend fun findById(id: String): Comment? {
        return _comments.value.find { it.id == id }
    }

    override fun findByUserId(userId: String): StateFlow<List<Comment>> {
        return MutableStateFlow(_comments.value.filter { it.userId == userId })
    }

    override fun findByServiceId(serviceId: String): StateFlow<List<Comment>> {
        return MutableStateFlow(_comments.value.filter { it.serviceId == serviceId })
    }

    override fun findByRating(rating: Int): StateFlow<List<Comment>> {
        return MutableStateFlow(_comments.value.filter { it.rating == rating })
    }

    private fun fetchComments(): List<Comment> {
        return listOf(

        )
    }
}