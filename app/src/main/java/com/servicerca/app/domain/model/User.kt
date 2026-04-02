package com.servicerca.app.domain.model

data class User (val id: String,
                 val name1: String,
                 val name2: String?,
                 val lastname1: String,
                 val lastname2: String?,
                 val city: String,
                 val address: String,
                 val email: String,
                 val password: String,
                 val phoneNumber: String = "",
                 val profilePictureUrl: String = "",
                 val role: UserRole = UserRole.USER)
