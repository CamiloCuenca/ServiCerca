package com.servicerca.app.core.utils

import com.servicerca.app.domain.model.UserRole

sealed class RequestResult {
    data class Success (val message: String): RequestResult()
    data class SuccessLogin(val userId: String, val role: UserRole): RequestResult()
    data class Failure (val errorMessage: String): RequestResult()
}