package com.servicerca.app.domain.model

data class Comment(
    val id: String,
    val userId: String,
    val serviceId: String,
    val userName: String,
    val userAvatar: String,
    val rating: Int,
    val text: String,
    val date: Long,
    val timeAgo: String
)