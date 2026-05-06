package com.servicerca.app.domain.model

    import java.time.Year
    import androidx.compose.runtime.Immutable

    @Immutable
    data class User(
        val id: String = "",
        val name1: String = "",
        val name2: String? = null,
        val lastname1: String = "",
        val lastname2: String? = null,
        val city: String = "",
        val address: String = "",
        val email: String = "",
        val password: String = "",
        val phoneNumber: String = "",
        val profilePictureUrl: String = "",
        val role: UserRole = UserRole.USER,
        val completedServices: Int = 0,
        val totalPoints: Int = 0,
        val rating: Double = 0.0,
        val memberSince: Int = Year.now().value,
        val pendingReviews: Int = 0,
        val approvedReviews: Int = 0,
        val rejectReviews: Int = 0,
        @get:JvmName("getIsEmailVerified") val isEmailVerified: Boolean = false,
        val listInteresting: List<String> = emptyList(),
        @get:JvmName("getIsSuspended") val isSuspended: Boolean = false
    )