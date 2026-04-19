package com.servicerca.app.domain.model

data class Chat(
    val chatId: String = "",
    val participantName: String = "",
    val participantImage: String = "",
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val unreadCount: Int = 0
)