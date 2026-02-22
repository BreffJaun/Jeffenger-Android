package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.navigation.helper.ChatRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    val chatId: String = savedStateHandle.toRoute<ChatRoute>().id
    private var lastMessageCount = 0

    val currentUserId: StateFlow<String?> =
        authRepository.authState
            .map { it?.uid }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val companyId: StateFlow<String?> =
        userRepository.appUser
            .map { it?.companyId }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Chat-Dokument (Header-Daten)
    val chat: StateFlow<Chat?> =
        companyId
            .flatMapLatest { cid ->
                if (cid.isNullOrBlank()) flowOf(null)
                else chatRepository.observeChat(cid, chatId)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Messages für den Chat
    val messages: StateFlow<List<Message>> =
        companyId
            .flatMapLatest { cid ->
                if (cid.isNullOrBlank()) flowOf(emptyList())
                else chatRepository.observeMessages(cid, chatId)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Teilnehmer (für Titel/Subtitel in TopBar)
    val participants: StateFlow<List<User>> =
        combine(companyId, chat) { cid, c -> cid to c }
            .flatMapLatest { (cid, c) ->
                if (cid.isNullOrBlank() || c == null || c.participantIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    chatRepository.observeUsers(cid, c.participantIds)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendTextMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val cid = companyId.value
            val uid = currentUserId.value

            if (cid.isNullOrBlank() || uid.isNullOrBlank()) return@launch

            val msg = Message(
                chatId = chatId,
                senderId = uid,
                text = trimmed,
                createdAt = System.currentTimeMillis()
            )

            chatRepository.sendMessage(cid, chatId, msg)
        }
    }

    fun markChatAsRead() {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch
            val uid = currentUserId.value ?: return@launch

            chatRepository.resetUnreadCount(
                companyId = cid,
                chatId = chatId,
                userId = uid
            )
        }
    }

    fun editMessage(messageId: String, newText: String) {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch
            chatRepository.editMessage(cid, chatId, messageId, newText)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch
            chatRepository.deleteMessage(cid, chatId, messageId)
        }
    }
}

