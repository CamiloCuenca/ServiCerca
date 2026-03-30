package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Comment
import kotlinx.coroutines.flow.StateFlow

interface CommentRepository {

    val comments: StateFlow<List<Comment>>

    suspend fun save(comment: Comment)

    suspend fun update(comment: Comment)

    suspend fun delete(id: String)

    suspend fun findById(id: String): Comment?

    fun findByUserId(userId: String): StateFlow<List<Comment>>

    fun findByServiceId(serviceId: String): StateFlow<List<Comment>>

    fun findByRating(rating: Int): StateFlow<List<Comment>>
}