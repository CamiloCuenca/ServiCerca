package com.servicerca.app.data.repository

import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Chat
import com.servicerca.app.domain.model.Message
import com.servicerca.app.domain.repository.ChatRepository
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val userRepository: UserRepository
) : ChatRepository {

    private data class Conversation(
        val user1Id: String,
        val user1Name: String,
        val user1Image: String,
        var unreadCount1: Int = 0,
        val user2Id: String,
        val user2Name: String,
        val user2Image: String,
        var unreadCount2: Int = 0,
        var lastMessage: String = "",
        var lastMessageTime: String = "",
        val lastSenderId: String = ""
    )

    private val _allConversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())

    private fun getConvId(u1: String, u2: String) = if (u1 < u2) "${u1}_$u2" else "${u2}_$u1"

    override fun getChats(): Flow<List<Chat>> {
        return combine(_allConversations, sessionDataStore.sessionFlow) { conversations, session ->
            val currentUserId = session?.userId ?: return@combine emptyList<Chat>()

            conversations
                .filter { it.user1Id == currentUserId || it.user2Id == currentUserId }
                .map { conv ->
                    val isUser1 = conv.user1Id == currentUserId
                    Chat(
                        chatId = if (isUser1) conv.user2Id else conv.user1Id,
                        participantName = if (isUser1) conv.user2Name else conv.user1Name,
                        participantImage = if (isUser1) conv.user2Image else conv.user1Image,
                        lastMessage = conv.lastMessage,
                        lastMessageTime = conv.lastMessageTime,
                        unreadCount = if (isUser1) conv.unreadCount1 else conv.unreadCount2
                    )
                }
        }
    }

    override suspend fun getMessages(chatId: String): Flow<List<Message>> {
        val currentUserId = sessionDataStore.sessionFlow.first()?.userId ?: ""
        val convId = getConvId(currentUserId, chatId)

        return _messages.map { allMessages ->
            allMessages[convId]?.map { msg ->
                msg.copy(isMine = msg.senderId == currentUserId)
            } ?: emptyList()
        }
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        val currentUserId = sessionDataStore.sessionFlow.first()?.userId ?: return
        val convId = getConvId(currentUserId, chatId)

        val msgWithSender = message.copy(senderId = currentUserId)
        val currentList = _messages.value[convId] ?: emptyList()
        _messages.value += (convId to (currentList + msgWithSender))

        _allConversations.value = _allConversations.value.map { conv ->
            if (getConvId(conv.user1Id, conv.user2Id) == convId) {
                val isUser1Sending = conv.user1Id == currentUserId
                conv.copy(
                    lastMessage = message.message,
                    lastMessageTime = message.time,
                    lastSenderId = currentUserId,
                    unreadCount1 = if (!isUser1Sending) conv.unreadCount1 + 1 else conv.unreadCount1,
                    unreadCount2 = if (isUser1Sending) conv.unreadCount2 + 1 else conv.unreadCount2
                )
            } else {
                conv
            }
        }
    }

    override suspend fun markAsRead(chatId: String) {
        val session = sessionDataStore.sessionFlow.first() ?: return
        val currentUserId = session.userId
        val convId = getConvId(currentUserId, chatId)

        _allConversations.value = _allConversations.value.map { conv ->
            if (getConvId(conv.user1Id, conv.user2Id) == convId) {
                if (conv.user1Id == currentUserId) conv.copy(unreadCount1 = 0)
                else conv.copy(unreadCount2 = 0)
            } else { conv }
        }

        val messages = _messages.value[convId] ?: emptyList()
        val updatedMessages = messages.map { msg ->
            if (msg.senderId != currentUserId) msg.copy(isRead = true) else msg
        }
        _messages.value += (convId to updatedMessages)
    }

    override suspend fun getOrCreateChat(userId: String, userName: String, userImage: String): String {
        val session = sessionDataStore.sessionFlow.first() ?: return userId
        val currentUserId = session.userId
        val convId = getConvId(currentUserId, userId)

        val existing = _allConversations.value.find { getConvId(it.user1Id, it.user2Id) == convId }

        if (existing == null) {
            val currentUserData = userRepository.findById(currentUserId)
            val newConv = Conversation(
                user1Id = currentUserId,
                user1Name = "${currentUserData?.name1 ?: ""} ${currentUserData?.lastname1 ?: ""}".trim().ifEmpty { "Yo" },
                user1Image = currentUserData?.profilePictureUrl ?: "",
                user2Id = userId,
                user2Name = userName,
                user2Image = userImage
            )
            _allConversations.value = listOf(newConv) + _allConversations.value
        }
        return userId
    }
}