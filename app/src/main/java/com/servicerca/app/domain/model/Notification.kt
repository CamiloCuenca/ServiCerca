package com.servicerca.app.domain.model

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val date: String,
    val imageRes: Int,
    val isRead: Boolean = false
)
