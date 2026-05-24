package com.servicerca.app.ui.chat

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.Message
import com.servicerca.app.domain.repository.ChatRepository
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.data.datastore.SessionDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.toRoute
import com.servicerca.app.core.navigation.MainRoutes
import kotlinx.coroutines.async

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isOnline: Boolean = false,
    val participantName: String = "",
    val participantImage: String = ""
)

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val rawChatId: String = try {
        savedStateHandle.toRoute<MainRoutes.Chat>().chatId
    } catch (e: Exception) {
        savedStateHandle.get<String>("chatId") ?: ""
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Variable para almacenar el ID del OTRO usuario de forma limpia
    private var otherUserId: String = ""

    init {
        if (rawChatId.isNotBlank()) {
            resolveParticipantAndStart()
        }
    }

    private fun resolveParticipantAndStart() {
        viewModelScope.launch {
            val myId = sessionDataStore.sessionFlow.firstOrNull()?.userId ?: ""

            otherUserId = if (rawChatId.contains("_")) {
                rawChatId.split("_").firstOrNull { it != myId } ?: rawChatId
            } else {
                rawChatId
            }

            Log.d("ChatVM", "Chat resolved: MyID=$myId, OtherID=$otherUserId, Raw=$rawChatId")

            loadMessages()
            loadChatHeaderInfo()
            observeParticipantPresence()
            updateMyStatus(true)
            markAsRead()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.getMessages(rawChatId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    private fun loadChatHeaderInfo() {
        viewModelScope.launch {
            // Intento 1: Buscar en la lista de chats activos (más rápido)
            val chatsJob = async {
                repository.getChats().firstOrNull()?.find { 
                    it.chatId == otherUserId || it.chatId == rawChatId 
                }
            }
            
            val chatInfo = chatsJob.await()
            if (chatInfo != null) {
                _uiState.value = _uiState.value.copy(
                    participantName = chatInfo.participantName,
                    participantImage = chatInfo.participantImage
                )
            } else {
                // Intento 2: Carga directa desde el perfil del usuario (necesario si venimos de notificación)
                val user = userRepository.findById(otherUserId)
                user?.let {
                    _uiState.value = _uiState.value.copy(
                        participantName = "${it.name1} ${it.lastname1}",
                        participantImage = it.profilePictureUrl
                    )
                }
            }
        }
    }

    private fun observeParticipantPresence() {
        viewModelScope.launch {
            if (otherUserId.isNotBlank()) {
                userRepository.observeUser(otherUserId).collect { user ->
                    _uiState.value = _uiState.value.copy(isOnline = user?.isOnline ?: false)
                }
            }
        }
    }

    private fun updateMyStatus(online: Boolean) {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.firstOrNull()
            session?.userId?.let { uid ->
                userRepository.updateOnlineStatus(uid, online)
            }
        }
    }

    fun onMessageChange(newMessage: String) {
        _uiState.value = _uiState.value.copy(currentMessage = newMessage)
    }

    fun sendMessage() {
        val text = _uiState.value.currentMessage.trim()
        if (text.isEmpty() || rawChatId.isBlank()) return

        val newMessage = Message(
            senderId = "me",
            message = text,
            time = getCurrentTime(),
            isMine = true
        )

        viewModelScope.launch {
            repository.sendMessage(rawChatId, newMessage)
            _uiState.value = _uiState.value.copy(currentMessage = "")
        }
    }

    private fun markAsRead(){
        viewModelScope.launch {
            repository.markAsRead(rawChatId)
        }
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hour12:$minuteStr $amPm"
    }
}
