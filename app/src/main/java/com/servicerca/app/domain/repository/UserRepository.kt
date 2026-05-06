package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    suspend fun save(user: User)
    suspend fun findById(id: String): User?
    suspend fun login(email: String, password: String): User?
    fun findByEmail(email: String): User?
    suspend fun deleteAccount(userId: String): Result<Unit>
    suspend fun suspendAccount(userId: String): Result<Unit>
    suspend fun verifyEmail(email: String, otpCode: String): Result<Boolean>
    suspend fun resendVerificationEmail(email: String): Result<Unit>
    suspend fun initiatePasswordRecovery(email: String): Result<Unit>
    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit>
    suspend fun updatePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit>
    suspend fun toggleInterestingService(userId: String, serviceId: String): Result<Boolean>
}
