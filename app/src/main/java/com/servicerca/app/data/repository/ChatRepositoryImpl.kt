package com.servicerca.app.data.repository

import com.servicerca.app.R
import com.servicerca.app.domain.model.Chat
import com.servicerca.app.domain.model.Message
import com.servicerca.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    private val fakeChats = listOf(
        Chat(
            chatId = "chat_deiver",
            participantName = "Deiver Bonano Cuenca",
            participantImage = R.drawable.primo_de_juan_camilo,
            lastMessage = "Uy mano, mandame 50 mil pesos porfa",
            lastMessageTime = "3:45 PM",
            unreadCount = 2
        ),
        Chat(
            chatId = "chat_mauricio",
            participantName = "Mauricio Cuenca",
            participantImage = R.drawable.papa_de_juan_camilo,
            lastMessage = "Usted donde anda!",
            lastMessageTime = "4:28 PM",
            unreadCount = 1
        ),
        Chat(
            chatId = "chat_julian",
            participantName = "Julian Montealegre",
            participantImage = R.drawable.tio_de_brandon,
            lastMessage = "Hola, ¿Donde estas?",
            lastMessageTime = "5:28 PM",
            unreadCount = 0
        ),
        Chat(
            chatId = "chat_ferney",
            participantName = "Ferney Alexander",
            participantImage = R.drawable.otro_primo,
            lastMessage = "Mano llegame, estoy embalado",
            lastMessageTime = "11:43 AM",
            unreadCount = 3
        )
    )

    // Mensajes simulados por chatId
    private val fakeMessages = mapOf(
        "chat_deiver" to listOf(
            Message(
                senderId = "deiver",
                message = "Uy mano, mandame 50 mil pesos porfa",
                time = "3:40 PM",
                isMine = false,
                imageProfile = R.drawable.primo_de_juan_camilo
            ),
            Message(senderId = "me", message = "Jajaja no tengo ni pa' mí", time = "3:42 PM", isMine = true),
            Message(senderId = "deiver", message = "Uy mano, mandame 50 mil pesos porfa, estoy en la quiebra", time = "3:45 PM", isMine = false, imageProfile = R.drawable.primo_de_juan_camilo)
        ),
        "chat_mauricio" to listOf(
            Message(senderId = "mauricio", message = "Usted donde anda!", time = "4:28 PM", isMine = false, imageProfile = R.drawable.papa_de_juan_camilo)
        ),
        "chat_julian" to listOf(
            Message(senderId = "julian", message = "Hola, ¿Donde estas? Te necesito para una cosa", time = "5:28 PM", isMine = false, imageProfile = R.drawable.tio_de_brandon)
        ),
        "chat_ferney" to listOf(
            Message(senderId = "ferney", message = "Mano llegame, estoy embalado con los tombos", time = "11:43 AM", isMine = false, imageProfile = R.drawable.otro_primo)
        )
    )

    // Flows internos para simular tiempo real
    private val chatsFlow = MutableStateFlow(fakeChats)
    private val messagesFlow = mutableMapOf<String, MutableStateFlow<List<Message>>>()

    fun getChats(): Flow<List<Chat>> = chatsFlow

    override suspend fun getMessages(chatId: String): Flow<List<Message>> {
        // Si no existe el flow para ese chat, lo crea con los mensajes simulados
        if (!messagesFlow.containsKey(chatId)) {
            messagesFlow[chatId] = MutableStateFlow(fakeMessages[chatId] ?: emptyList())
        }
        return messagesFlow[chatId]!!
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        val current = messagesFlow[chatId] ?: MutableStateFlow(emptyList())
        current.value += message
        messagesFlow[chatId] = current

        // Actualizar el último mensaje en la lista de chats
        chatsFlow.value = chatsFlow.value.map {
            if (it.chatId == chatId) it.copy(
                lastMessage = message.message,
                lastMessageTime = message.time
            ) else it
        }
    }
}