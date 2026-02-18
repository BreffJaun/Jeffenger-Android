package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.local.MockData
import com.example.jeffenger.data.local.MockData.chats
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.remote.model.ui_model.ChatListItemUiModel
import com.example.jeffenger.data.repository.AuthRepositoryFirebase
import com.example.jeffenger.data.repository.UserRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.classifier.ChatClassifier
import com.example.jeffenger.utils.enums.ChatType
import com.example.jeffenger.utils.model.StartChatUiState
import com.example.jeffenger.utils.mapper.mapToAvatarUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModel(
    private val chatRepository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    // AUTH & USER STATE
    private val currentUserIdState = authRepository.authState
        .map { it?.uid }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val companyIdState = userRepository.appUser
        .map { it?.companyId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val jeffUserIdState = userRepository.observeGlobalUsers()
        .map { users -> users.firstOrNull { it.isGlobal }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // NAVIGATION
    private val _navigateToChat = MutableSharedFlow<String>()
    val navigateToChat = _navigateToChat.asSharedFlow()

    // RAW CHATS
    private val userChatsFlow: Flow<List<Chat>> =
        combine(companyIdState, currentUserIdState) { companyId, userId ->
            companyId to userId
        }.flatMapLatest { (companyId, userId) ->
            if (companyId == null || userId == null) {
                flowOf(emptyList())
            } else {
                chatRepository.observeChatsForUser(companyId, userId)
            }
        }

    // START CHAT UI STATE
    val startChatUiState: StateFlow<StartChatUiState> =
        combine(userChatsFlow, jeffUserIdState) { chats, jeffId ->
            buildStartChatUiState(chats, jeffId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StartChatUiState()
        )

    // CHAT LIST UI
    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
        combine(userChatsFlow, companyIdState, currentUserIdState) { chats, companyId, userId ->
            Triple(chats, companyId, userId)
        }.flatMapLatest { (chats, companyId, userId) ->
            if (companyId == null || userId == null) {
                flowOf(emptyList())
            } else {

                val allUserIds = chats
                    .flatMap { it.participantIds }
                    .distinct()

                chatRepository.observeUsers(companyId, allUserIds)
                    .map { users ->
                        chats.map { chat ->
                            mapChatToUiModel(
                                chat = chat,
                                currentUserId = userId,
                                users = users
                            )
                        }
                    }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // MAPPERS
    private fun mapChatToUiModel(
        chat: Chat,
        currentUserId: String,
        users: List<User>
    ): ChatListItemUiModel {

        val displayName =
            if (chat.isGroupChat) {
                chat.title ?: "Gruppe"
            } else {
                users.firstOrNull {
                    it.id != currentUserId &&
                            chat.participantIds.contains(it.id)
                }?.displayName ?: "Unbekannt"
            }

        return ChatListItemUiModel(
            chatId = chat.id,
            displayName = displayName,
            lastMessageText = chat.lastMessageText,
            lastMessageTimestamp = chat.lastMessageTimestamp,
            unreadCount = chat.unreadCount[currentUserId] ?: 0,
            avatar = mapToAvatarUiModel(chat, currentUserId, users)
        )
    }

    // START CHAT LOGIK
    private fun buildStartChatUiState(
        chats: List<Chat>,
        jeffId: String?
    ): StartChatUiState {

        if (jeffId == null) return StartChatUiState()

        val hasDirectJeffChat = chats.any { chat ->
            !chat.isGroupChat &&
                    chat.participantIds.size == 2 &&
                    chat.participantIds.contains(jeffId)
        }

        val hasCompanyChat = chats.any { chat ->
            chat.isGroupChat &&
                    !chat.participantIds.contains(jeffId)
        }

        val hasCompanyWithJeffChat = chats.any { chat ->
            chat.isGroupChat &&
                    chat.participantIds.contains(jeffId)
        }

        return StartChatUiState(
            showDirectJeff = !hasDirectJeffChat,
            showCompany = !hasCompanyChat,
            showCompanyWithJeff = !hasCompanyWithJeffChat
        )
    }

    // ACTIONS
    fun startDirectJeffChat() {
        viewModelScope.launch {

            val currentUserId = currentUserIdState.value ?: return@launch
            val companyId = companyIdState.value ?: return@launch
            val jeffId = jeffUserIdState.value ?: return@launch

            val chatId = chatRepository.createChat(
                companyId = companyId,
                participantIds = listOf(currentUserId, jeffId),
                isGroupChat = false,
                title = null
            )

            _navigateToChat.emit(chatId)
        }
    }

    fun startCompanyChat() {
        viewModelScope.launch {

            val currentUserId = currentUserIdState.value ?: return@launch
            val companyId = companyIdState.value ?: return@launch

            val chatId = chatRepository.createChat(
                companyId = companyId,
                participantIds = listOf(currentUserId), // Phase 1
                isGroupChat = true,
                title = "Company"
            )

            _navigateToChat.emit(chatId)
        }
    }

    fun startCompanyWithJeffChat() {
        viewModelScope.launch {

            val currentUserId = currentUserIdState.value ?: return@launch
            val companyId = companyIdState.value ?: return@launch
            val jeffId = jeffUserIdState.value ?: return@launch

            val chatId = chatRepository.createChat(
                companyId = companyId,
                participantIds = listOf(currentUserId, jeffId),
                isGroupChat = true,
                title = "Company + Jeff"
            )

            _navigateToChat.emit(chatId)
        }
    }
}

//@OptIn(ExperimentalCoroutinesApi::class)
//class ChatsViewModel(
//    private val chatRepository: ChatRepositoryInterface,
//    private val authRepository: AuthRepositoryInterface,
//    private val userRepository: UserRepositoryInterface
//) : ViewModel() {
//
//    //    private val currentUserIdFlow = authRepository.authState.map { it?.uid }
//    //    private val companyIdFlow = userRepository.appUser.map { it?.companyId }
//    private val currentUserIdState = authRepository.authState
//        .map { it?.uid }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
//
//    private val companyIdState = userRepository.appUser
//        .map { it?.companyId }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
//
//    private val jeffUserIdFlow: StateFlow<String?> =
//        userRepository.observeGlobalUsers()
//            .map { users ->
//                users.firstOrNull { it.isGlobal }?.id
//            }
//            .stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5_000),
//                null
//            )
//
//    private val _navigateToChat = MutableSharedFlow<String>()
//    val navigateToChat = _navigateToChat.asSharedFlow()
//
//    // RAW FIREBASE DATA
//    private val userChatsFlow: Flow<List<Chat>> =
//        // combine means -> whenever one of the two changes, build a new result from both.
//        combine(companyIdFlow, currentUserIdFlow) { companyId, userId ->
//            companyId to userId
//        }
//            // flatMapLatest -> as soon as companyId or userId changes, the old flow is terminated and a new one is started.
//            // A Flow-Operator
//            .flatMapLatest { (companyId, userId) ->
//                if (companyId == null || userId == null) {
//                    flowOf(emptyList())
//                } else {
//                    chatRepository.observeChatsForUser(companyId, userId)
//                }
//            }
//
//
//    val startChatUiState: StateFlow<StartChatUiState> =
//        combine(userChatsFlow, jeffUserIdFlow) { chats, jeffId ->
//            buildStartChatUiState(chats, jeffId)
//        }
//            .stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5_000),
//                StartChatUiState()
//            )
//
//    // UI PREPARED DATA
//    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
//        combine(userChatsFlow, companyIdFlow, currentUserIdFlow) { chats, companyId, userId ->
//            Triple(chats, companyId, userId)
//        }
//            .flatMapLatest { (chats, companyId, userId) ->
//                if (companyId == null || userId == null) {
//                    flowOf(emptyList())
//                } else {
//
//                    // flatMap -> Make a (flat) list out of liszs. Flat means fom nested to one level.
//                    // A List-Operator
//                    val allUserIds = chats.flatMap { it.participantIds }.distinct()
//
//                    chatRepository
//                        .observeUsers(companyId, allUserIds)
//                        .map { users ->
//                            chats.map { chat ->
//                                mapChatToUiModel(
//                                    chat = chat,
//                                    currentUserId = userId,
//                                    users = users
//                                )
//                            }
//                        }
//                }
//            }
//            .stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5_000),
//                emptyList()
//            )
//
//    private fun mapChatToUiModel(
//        chat: Chat,
//        currentUserId: String,
//        users: List<User>
//    ): ChatListItemUiModel {
//
//        val displayName =
//            if (chat.isGroupChat) {
//                chat.title ?: "Gruppe"
//            } else {
//                users
//                    .firstOrNull {
//                        it.id != currentUserId &&
//                                chat.participantIds.contains(it.id)
//                    }
//                    ?.displayName ?: "Unbekannt"
//            }
//
//        return ChatListItemUiModel(
//            chatId = chat.id,
//            displayName = displayName,
//            lastMessageText = chat.lastMessageText,
//            lastMessageTimestamp = chat.lastMessageTimestamp,
//            unreadCount = chat.unreadCount[currentUserId] ?: 0,
//            avatar = mapToAvatarUiModel(
//                chat = chat,
//                currentUserId = currentUserId,
//                users = users
//            )
//        )
//    }
//
//    private fun buildStartChatUiState(
//        chats: List<Chat>,
//        jeffId: String?
//    ): StartChatUiState {
//
//        if (jeffId == null) {
//            return StartChatUiState()
//        }
//
//        val hasDirectJeffChat = chats.any { chat ->
//            !chat.isGroupChat &&
//                    chat.participantIds.size == 2 &&
//                    chat.participantIds.contains(jeffId)
//        }
//
//        val hasCompanyChat = chats.any { chat ->
//            chat.isGroupChat &&
//                    !chat.participantIds.contains(jeffId)
//        }
//
//        val hasCompanyWithJeffChat = chats.any { chat ->
//            chat.isGroupChat &&
//                    chat.participantIds.contains(jeffId)
//        }
//
//        return StartChatUiState(
//            showDirectJeff = !hasDirectJeffChat,
//            showCompany = !hasCompanyChat,
//            showCompanyWithJeff = !hasCompanyWithJeffChat
//        )
//    }
//
//    fun startDirectJeffChat() {
//        viewModelScope.launch {
//
//            val currentUserId = currentUserIdFlow.first() ?: return@launch
//            val companyId = companyIdFlow.first() ?: return@launch
//            val jeffId = jeffUserIdFlow.first() ?: return@launch
//
//            val chatId = chatRepository.createChat(
//                companyId = companyId,
//                participantIds = listOf(currentUserId, jeffId),
//                isGroupChat = false,
//                title = null
//            )
//
//            _navigateToChat.emit(chatId)
//        }
//    }
//
//    fun startCompanyChat() {
//        viewModelScope.launch {
//            val companyId = userRepository.appUser.value?.companyId
//            val currentUserId = authRepository.authState.value?.uid
//            if (companyId.isNullOrBlank() || currentUserId.isNullOrBlank()) return@launch
//
//            // Phase 1 (Shortcut): nur du allein als "Company Chat"
//            // Später: alle Company User IDs reinladen und hinzufügen
//            val chatId = chatRepository.createChat(
//                companyId = companyId,
//                participantIds = listOf(currentUserId),
//                isGroupChat = true,
//                title = "Company"
//            )
//
//            _navigateToChat.emit(chatId)
//        }
//    }
//
//    fun startCompanyWithJeffChat() {
//        viewModelScope.launch {
//            val companyId = userRepository.appUser.value?.companyId
//            val currentUserId = authRepository.authState.value?.uid
//            if (companyId.isNullOrBlank() || currentUserId.isNullOrBlank()) return@launch
//            val jeffId = jeffUserIdFlow.first() ?: return@launch
//
//            // Phase 1: du + Jeff
//            val chatId = chatRepository.createChat(
//                companyId = companyId,
//                participantIds = listOf(currentUserId, jeffId),
//                isGroupChat = true,
//                title = "Company + Jeff"
//            )
//
//            _navigateToChat.emit(chatId)
//        }
//    }
//}







