package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(chatId: String, message: Message)
    suspend fun getMessages(chatId: String): Flow<List<Message>>
}