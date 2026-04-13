package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    fun save(user: User)
    fun findById(id: String): User?
    fun login(email: String, password: String): User?
    suspend fun deleteAccount(userId: String): Result<Unit>
}