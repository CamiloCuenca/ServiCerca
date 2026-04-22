package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Chat
import com.servicerca.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun sendMessage(chatId: String, message: Message)
    suspend fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun getOrCreateChat(userId: String, userName: String, userImage: String): String
    suspend fun markAsRead(chatId: String)
}