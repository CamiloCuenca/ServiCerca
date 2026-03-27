package com.servicerca.app.data.model

import com.servicerca.app.domain.model.UserRole


data class UserSession(
    val userId: String,
    val role: UserRole
)