package com.servicerca.app.domain.model

import java.util.UUID.randomUUID

data class Message(
    val id: String = randomUUID().toString(),
    val senderId: String = "",
    val message: String = "",
    val time: String = "",
    val isMine: Boolean = false,
    val isRead: Boolean = false,
    val imageProfile: String? = null
)
