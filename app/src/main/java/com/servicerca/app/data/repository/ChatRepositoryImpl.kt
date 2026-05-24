package com.servicerca.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.servicerca.app.core.fcm.FCMSender
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.model.Chat
import com.servicerca.app.domain.model.Message
import com.servicerca.app.domain.repository.ChatRepository
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sessionDataStore: SessionDataStore,
    private val userRepository: UserRepository,
    private val fcmSender: FCMSender
) : ChatRepository {

    private val notifScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Genera un ID de conversación ÚNICO.
     * Si 'otherId' ya contiene un guion bajo, se asume que ya es un ID de conversación y se usa tal cual.
     */
    private fun resolveConvId(myId: String, otherId: String): String {
        if (otherId.contains("_")) return otherId.trim()
        val id1 = myId.trim()
        val id2 = otherId.trim()
        return if (id1 < id2) "${id1}_${id2}" else "${id2}_${id1}"
    }

    override fun getChats(): Flow<List<Chat>> = callbackFlow {
        val currentUserId = sessionDataStore.sessionFlow.firstOrNull()?.userId
        if (currentUserId.isNullOrEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("conversations")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { doc ->
                        val user1Id = doc.getString("user1Id") ?: ""
                        val user2Id = doc.getString("user2Id") ?: ""
                        val isUser1 = user1Id == currentUserId

                        val participantName = if (isUser1) doc.getString("user2Name") ?: "" else doc.getString("user1Name") ?: ""
                        val participantImage = if (isUser1) doc.getString("user2Image") ?: "" else doc.getString("user1Image") ?: ""
                        
                        val unreadCount = if (isUser1) {
                            doc.getLong("unreadCount1")?.toInt() ?: 0
                        } else {
                            doc.getLong("unreadCount2")?.toInt() ?: 0
                        }

                        val lastMessageTimeTimestamp = doc.getTimestamp("lastMessageTimestamp")
                        val timeStr = lastMessageTimeTimestamp?.toDate()?.let {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
                        } ?: ""

                        Chat(
                            chatId = if (isUser1) user2Id else user1Id,
                            participantName = participantName,
                            participantImage = participantImage,
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTime = timeStr,
                            unreadCount = unreadCount
                        )
                    }
                    val sortedChats = chats.sortedByDescending { it.lastMessageTime }
                    trySend(sortedChats)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val currentUserId = sessionDataStore.sessionFlow.firstOrNull()?.userId
        if (currentUserId.isNullOrEmpty() || chatId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val convId = resolveConvId(currentUserId, chatId)

        val listener = firestore.collection("conversations").document(convId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatRepo", "Error en messages para $convId: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        val senderId = doc.getString("senderId") ?: ""
                        val timestamp = doc.getTimestamp("timestamp")
                        val timeStr = timestamp?.toDate()?.let {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
                        } ?: ""

                        Message(
                            id = doc.id,
                            senderId = senderId,
                            message = doc.getString("message") ?: "",
                            time = timeStr,
                            isMine = senderId == currentUserId,
                            isRead = doc.getBoolean("isRead") ?: false,
                            imageProfile = null
                        )
                    }
                    trySend(messages)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        val currentUserId = sessionDataStore.sessionFlow.firstOrNull()?.userId ?: return
        val convId = resolveConvId(currentUserId, chatId)
        val isUser1Sending = convId.startsWith(currentUserId)

        // Extraer el ID real del destinatario (si convId es compuesto, es la parte que no soy yo)
        val realRecipientId = if (chatId.contains("_")) {
            chatId.split("_").firstOrNull { it != currentUserId } ?: chatId
        } else {
            chatId
        }

        val batch = firestore.batch()
        val convRef = firestore.collection("conversations").document(convId)
        
        val convUpdates = mutableMapOf<String, Any>(
            "participants" to listOf(currentUserId, realRecipientId),
            "lastMessage" to message.message,
            "lastMessageTimestamp" to FieldValue.serverTimestamp(),
            "lastSenderId" to currentUserId
        )
        if (isUser1Sending) {
            convUpdates["unreadCount2"] = FieldValue.increment(1)
        } else {
            convUpdates["unreadCount1"] = FieldValue.increment(1)
        }

        batch.set(convRef, convUpdates, SetOptions.merge())

        val messageRef = convRef.collection("messages").document(UUID.randomUUID().toString())
        val messageData = mapOf(
            "senderId" to currentUserId,
            "message" to message.message,
            "timestamp" to FieldValue.serverTimestamp(),
            "isRead" to false
        )
        batch.set(messageRef, messageData)

        try {
            batch.commit().await()
            notifScope.launch {
                trySendChatPush(recipientId = realRecipientId, senderId = currentUserId, text = message.message)
            }
        } catch (e: Exception) {
            Log.e("ChatRepo", "Error enviando mensaje", e)
        }
    }

    private suspend fun trySendChatPush(recipientId: String, senderId: String, text: String) {
        try {
            val recipientToken = firestore.collection("users").document(recipientId)
                .get().await().getString("fcmToken")?.takeIf { it.isNotBlank() } ?: return

            val senderDoc = firestore.collection("users").document(senderId).get().await()
            val senderName = "${senderDoc.getString("name1") ?: ""} ${senderDoc.getString("lastname1") ?: ""}".trim()
                .ifBlank { "Alguien" }

            fcmSender.sendChatNotification(recipientToken, senderName, text, senderId)
        } catch (e: Exception) {
            Log.e("ChatRepo", "Error enviando push", e)
        }
    }

    override suspend fun markAsRead(chatId: String) {
        val currentUserId = sessionDataStore.sessionFlow.firstOrNull()?.userId ?: return
        val convId = resolveConvId(currentUserId, chatId)
        val isUser1 = convId.startsWith(currentUserId)

        val convRef = firestore.collection("conversations").document(convId)
        val unreadField = if (isUser1) "unreadCount1" else "unreadCount2"
        try {
            convRef.update(unreadField, 0).await()
        } catch (e: Exception) {
            // Documento podría no existir aún
        }
    }

    override suspend fun getOrCreateChat(userId: String, userName: String, userImage: String): String {
        val currentUserId = sessionDataStore.sessionFlow.firstOrNull()?.userId ?: return userId
        val convId = resolveConvId(currentUserId, userId)
        val convRef = firestore.collection("conversations").document(convId)
        
        try {
            val snapshot = convRef.get().await()
            if (!snapshot.exists()) {
                val currentUserData = userRepository.findById(currentUserId)
                val isUser1 = convId.startsWith(currentUserId)
                
                val currentUserName = "${currentUserData?.name1 ?: ""} ${currentUserData?.lastname1 ?: ""}".trim().ifEmpty { "Yo" }
                val currentUserImage = currentUserData?.profilePictureUrl ?: ""

                val user1Id = if (isUser1) currentUserId else userId
                val user1Name = if (isUser1) currentUserName else userName
                val user1Image = if (isUser1) currentUserImage else userImage

                val user2Id = if (isUser1) userId else currentUserId
                val user2Name = if (isUser1) userName else currentUserName
                val user2Image = if (isUser1) userImage else currentUserImage

                val newConv = mapOf(
                    "participants" to listOf(currentUserId, userId),
                    "user1Id" to user1Id,
                    "user1Name" to user1Name,
                    "user1Image" to user1Image,
                    "unreadCount1" to 0,
                    "user2Id" to user2Id,
                    "user2Name" to user2Name,
                    "user2Image" to user2Image,
                    "unreadCount2" to 0,
                    "lastMessage" to "",
                    "lastSenderId" to "",
                    "lastMessageTimestamp" to FieldValue.serverTimestamp()
                )
                convRef.set(newConv).await()
            }
        } catch (e: Exception) {
            Log.e("ChatRepo", "Error getOrCreateChat", e)
        }
        return userId
    }
}
