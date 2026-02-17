package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
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

    private val currentUserIdFlow: Flow<String?> =
        authRepository.authState.map { it?.uid }

    private val companyIdFlow: Flow<String?> =
        userRepository.appUser.map { it?.companyId }

    private val companyAndUserFlow: Flow<Pair<String, String>> =
        combine(companyIdFlow, currentUserIdFlow) { companyId, userId ->
            companyId to userId
        }.flatMapLatest { (companyId, userId) ->
            if (companyId == null || userId == null) flowOf(null)
            else flowOf(companyId to userId)
        }.map { it ?: return@map null }
            .flatMapLatest { pair ->
                if (pair == null) flowOf(Pair("", "")) else flowOf(pair)
            }

    // Chat-Dokument (Header-Daten)
    val chat: StateFlow<Chat?> =
        companyIdFlow
            .flatMapLatest { companyId ->
                if (companyId.isNullOrBlank()) flowOf(null)
                else chatRepository.observeChat(companyId, chatId)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    // Messages für den Chat
    val messages: StateFlow<List<Message>> =
        companyIdFlow
            .flatMapLatest { companyId ->
                if (companyId.isNullOrBlank()) flowOf(emptyList())
                else chatRepository.observeMessages(companyId, chatId)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun sendTextMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val companyId = companyIdFlow.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                null
            ).value

            val userId = authRepository.authState.value?.uid

            if (companyId.isNullOrBlank() || userId.isNullOrBlank()) return@launch

            val msg = Message(
                chatId = chatId,
                senderId = userId,
                text = trimmed,
                createdAt = System.currentTimeMillis()
            )

            chatRepository.sendMessage(companyId, chatId, msg)
        }
    }
}