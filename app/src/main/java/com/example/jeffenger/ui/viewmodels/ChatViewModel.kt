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
import com.example.jeffenger.utils.state.AppForegroundState
import com.example.jeffenger.utils.state.LoadingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
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

    // FOR SNACKBAR
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    // MESSAGES & PAGINATION
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    private var oldestLoadedMessage: Message? = null
    private var isLoadingMore = false

    val chatId: String = savedStateHandle.toRoute<ChatRoute>().id
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

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
//    private fun observeRealtimeMessages(companyId: String) {
//        viewModelScope.launch {
//            chatRepository.observeLatestMessages(companyId, chatId)
//                .collect { latestMessages ->
//                    _messages.value = latestMessages
//                    oldestLoadedMessage = latestMessages.firstOrNull()
//                }
//        }
//    }

    fun loadMoreMessages() {
        val cid = companyId.value ?: return
        val oldest = oldestLoadedMessage ?: return
        if (isLoadingMore) return

        viewModelScope.launch {
            isLoadingMore = true

            val olderMessages = chatRepository.loadMoreMessages(
                cid,
                chatId,
                oldest
            )

            if (olderMessages.isNotEmpty()) {
                _messages.value = olderMessages + _messages.value
                oldestLoadedMessage = olderMessages.first()
            }

            isLoadingMore = false
        }
    }
//    val messages: StateFlow<List<Message>> =
//        companyId
//            .flatMapLatest { cid ->
//                if (cid.isNullOrBlank()) flowOf(emptyList())
//                else chatRepository.observeMessages(cid, chatId)
//            }
//            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    init {
        viewModelScope.launch {
            companyId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { cid ->
                    chatRepository.observeLatestMessages(cid, chatId)
                }
                .collect { latestMessages ->
                    _messages.value = latestMessages
                    oldestLoadedMessage = latestMessages.firstOrNull()

                    // When chat is open -> mark as read immediately
                    if (AppForegroundState.currentOpenChatId == chatId) {
                        markChatAsRead()
                    }
                }
        }
    }

    fun sendTextMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val cid = companyId.value
            val uid = currentUserId.value

            if (cid.isNullOrBlank() || uid.isNullOrBlank()) return@launch

            try {
                _loadingState.value = LoadingState.Loading("Nachricht wird gesendet")

                val msg = Message(
                    chatId = chatId,
                    senderId = uid,
                    text = trimmed,
                    clientCreatedAt = System.currentTimeMillis()
                )

                chatRepository.sendMessage(cid, chatId, msg)

                _loadingState.value = LoadingState.Idle
            } catch (e: Exception) {
                _uiEvents.emit("Nachricht konnte nicht gesendet werden")
            }
        }
    }

    fun markChatAsRead() {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch
            val uid = currentUserId.value ?: return@launch

            try {
                chatRepository.resetUnreadCount(
                    companyId = cid,
                    chatId = chatId,
                    userId = uid
                )
            } catch (e: Exception) {
//                _loadingState.value = LoadingState.Error(
//                    message = "Lesestatus konnte nicht aktualisiert werden",
//                    throwable = e
//                )
                _uiEvents.emit("Lesestatus konnte nicht aktualisiert werden")
            }
        }
    }

    fun editMessage(messageId: String, newText: String) {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch
            val trimmed = newText.trim()

            if (trimmed.isEmpty()) {
                _uiEvents.emit("Nachricht darf nicht leer sein")
                return@launch
            }

            try {
                chatRepository.editMessage(cid, chatId, messageId, trimmed)
            } catch (e: Exception) {
                _uiEvents.emit("Nachricht konnte nicht bearbeitet werden")
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val cid = companyId.value ?: return@launch

            try {
                _loadingState.value = LoadingState.Loading("Nachricht wird gelöscht")

                chatRepository.deleteMessage(cid, chatId, messageId)

                _loadingState.value = LoadingState.Idle
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(
                    message = "Nachricht konnte nicht gelöscht werden",
                    throwable = e
                )
            }
        }
    }
}

