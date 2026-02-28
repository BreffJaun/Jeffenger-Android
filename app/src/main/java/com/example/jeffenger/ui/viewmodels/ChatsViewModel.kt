package com.example.jeffenger.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.remote.model.ui_model.ChatListItemUiModel
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.StorageRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.model.StartChatUiState
import com.example.jeffenger.utils.mapper.mapToAvatarUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.flatten

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModel(
    private val chatRepository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface
) : ViewModel() {

    // UI Events (Snackbar)
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _lockedCompanyId = MutableStateFlow<String?>(null)
    val lockedCompanyId: StateFlow<String?> = _lockedCompanyId

    // AUTH & USER STATE
    // Current loggedIn User (Firebase Auth)
    private val currentUserIdState = authRepository.authState
        .map { it?.uid }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Company-ID of the loggedIn user
    private val companyIdState = userRepository.appUser
        .map { it?.companyId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Globale "Jeff"-User-ID (is needed for speical Chat logic)
    private val jeffUserIdState = userRepository.observeGlobalUsers()
        .map { users -> users.firstOrNull { it.global }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


    val currentUserIsGlobalState = userRepository.appUser
            .map { it?.global == true }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // NEW CHAT
    private val _isGroupMode = MutableStateFlow(false)
    val isGroupMode: StateFlow<Boolean> = _isGroupMode

    private val _groupTitle = MutableStateFlow("")
    val groupTitle: StateFlow<String> = _groupTitle

    private val _groupImageUrl = MutableStateFlow<String?>(null)
    val groupImageUrl: StateFlow<String?> = _groupImageUrl

    private val _groupImageUri = MutableStateFlow<Uri?>(null)
    val groupImageUri: StateFlow<Uri?> = _groupImageUri

    fun setGroupImageUri(uri: Uri?) {
        _groupImageUri.value = uri
    }

    // NAVIGATION
    // ...into a specific chat
//    private val _navigateToChat = MutableSharedFlow<String>()
//    val navigateToChat = _navigateToChat.asSharedFlow()
    private val _navigateToChat = MutableSharedFlow<Pair<String, String>>()
    val navigateToChat = _navigateToChat.asSharedFlow()

    // RAW CHATS
    // Observes all chats of the current user within their company
    private val userChatsFlow: Flow<List<Chat>> =
        combine(companyIdState, currentUserIdState, currentUserIsGlobalState) { companyId, userId, isGlobal ->
            Triple(companyId, userId, isGlobal)
        }.flatMapLatest { (companyId, userId, isGlobal) ->

            when {
                userId == null -> flowOf(emptyList())

                isGlobal -> chatRepository.observeChatsForUserGlobal(userId)

                companyId != null ->
                    chatRepository.observeChatsForUser(companyId, userId)

                else -> flowOf(emptyList())
            }
        }


    // START CHAT UI STATE
    // Controls which quick start options are displayed
    val startChatUiState: StateFlow<StartChatUiState> =
        combine(userChatsFlow, jeffUserIdState) { chats, jeffId ->
            buildStartChatUiState(chats, jeffId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StartChatUiState()
        )

    // ONLY FOR GLOBAL USER (JEFF)
    val groupedMembersUiState: StateFlow<Map<String, List<User>>> =
        combine(
            currentUserIsGlobalState,
            chatRepository.observeAllCompanyMembers()
        ) { isGlobal, allMembers ->

            if (isGlobal) {
                allMembers
            } else {
                emptyMap()
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyMap()
        )

    // CHAT LIST UI
    // Transforms raw chat data into UI models for the chat list
    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
        combine(
            userChatsFlow,
            currentUserIdState,
            currentUserIsGlobalState
        ) { chats, userId, isGlobal ->
            Triple(chats, userId, isGlobal)
        }.flatMapLatest { (chats, userId, isGlobal) ->

            if (userId == null) {
                flowOf(emptyList())
            } else {

                val allUserIds = chats
                    .flatMap { it.participantIds }
                    .distinct()

                if (allUserIds.isEmpty()) {
                    flowOf(emptyList())
                } else {

                    if (isGlobal) {

                        // 🔥 Users pro Company auflösen
                        val flows = chats
                            .mapNotNull { it.companyId }
                            .distinct()
                            .map { companyId ->
                                chatRepository.observeUsers(companyId, allUserIds)
                            }

                        combine(flows) { results ->
                            val mergedUsers = results
                                .toList()
                                .flatten()
                                .distinctBy { it.id }

                            chats.map { chat ->
                                mapChatToUiModel(chat, userId, mergedUsers)
                            }
                        }

                    } else {

                        val companyId = companyIdState.value

                        if (companyId != null) {
                            chatRepository
                                .observeUsers(companyId, allUserIds)
                                .map { users ->
                                    chats.map { chat ->
                                        mapChatToUiModel(chat, userId, users)
                                    }
                                }
                        } else {
                            flowOf(emptyList())
                        }
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
//    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
//        combine(
//            userChatsFlow,
//            companyIdState,
//            currentUserIdState,
//            currentUserIsGlobalState
//        ) { chats, companyId, userId, isGlobal ->
//            chats to Triple(companyId, userId, isGlobal)
//        }.flatMapLatest { (chats, triple) ->
//
//            val (companyId, userId, isGlobal) = triple
//
//            if (userId == null) {
//                flowOf(emptyList())
//            } else {
//
//                val allUserIds = chats
//                    .flatMap { it.participantIds }
//                    .distinct()
//
//                if (allUserIds.isEmpty()) {
//                    flowOf(emptyList())
//                } else {
//
//                    when {
//                        isGlobal -> {
//                            chatRepository
//                                .observeUsersFromMultipleCompanies(allUserIds)
//                                .map { users ->
//                                    Log.d("CHAT_UI_DEBUG", "Chats: ${chats.size} | Users: ${users.size}")
//                                    chats.map { chat ->
//                                        mapChatToUiModel(chat, userId, users)
//                                    }
//                                }
//                        }
//
//                        companyId != null -> {
//                            chatRepository
//                                .observeUsers(companyId, allUserIds)
//                                .map { users ->
//                                    chats.map { chat ->
//                                        mapChatToUiModel(chat, userId, users)
//                                    }
//                                }
//                        }
//
//                        else -> flowOf(emptyList())
//                    }
//                }
//            }
//        }.stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5_000),
//            emptyList()
//        )

//    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
//        combine(userChatsFlow, companyIdState, currentUserIdState) { chats, companyId, userId ->
//            Triple(chats, companyId, userId)
//        }.flatMapLatest { (chats, companyId, userId) ->
//            if (companyId == null || userId == null) {
//                flowOf(emptyList())
//            } else {
//
//                val allUserIds = chats
//                    .flatMap { it.participantIds }
//                    .distinct()
//
//                chatRepository.observeUsers(companyId, allUserIds)
//                    .map { users ->
//                        chats.map { chat ->
//                            mapChatToUiModel(
//                                chat = chat,
//                                currentUserId = userId,
//                                users = users
//                            )
//                        }
//                    }
//            }
//        }.stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5_000),
//            emptyList()
//        )


    // MAPPERS
    // Wandelt ein Chat-Domain-Objekt in ein ChatListItemUiModel für die Anzeige um
    private fun mapChatToUiModel(
        chat: Chat,
        currentUserId: String,
        users: List<User>
    ): ChatListItemUiModel {

        val displayName =
            if (chat.groupChat) {
                chat.title ?: "Gruppe"
            } else {
                users.firstOrNull {
                    it.id != currentUserId &&
                            chat.participantIds.contains(it.id)
                }?.displayName ?: "Unbekannt"
            }

        return ChatListItemUiModel(
            chatId = chat.id,
            companyId = chat.companyId ?: companyIdState.value,
            displayName = displayName,
            lastMessageText = chat.lastMessageText,
            lastMessageTimestamp = chat.lastMessageTimestamp,
            unreadCount = chat.unreadCount[currentUserId] ?: 0,
            avatar = mapToAvatarUiModel(chat, currentUserId, users)
        )
    }

    // START CHAT LOGIK
    // Checks existing chats to prevent showing a sinnless button
    private fun buildStartChatUiState(
        chats: List<Chat>,
        jeffId: String?
    ): StartChatUiState {

        val hasCompanyChat = chats.any { chat ->
            chat.groupChat &&
                    (jeffId == null || !chat.participantIds.contains(jeffId))
        }

        val hasDirectJeffChat =
            if (jeffId != null) {
                chats.any { chat ->
                    !chat.groupChat &&
                            chat.participantIds.size == 2 &&
                            chat.participantIds.contains(jeffId)
                }
            } else false

        val hasCompanyWithJeffChat =
            if (jeffId != null) {
                chats.any { chat ->
                    chat.groupChat &&
                            chat.participantIds.contains(jeffId)
                }
            } else false

//        Log.d("DEBUG_STATE", "Chats: ${chats.map { it.participantIds }}")
//        Log.d("DEBUG_STATE", "JeffId: $jeffId")
//        Log.d("DEBUG_STATE", "hasCompanyChat: $hasCompanyChat")
//        Log.d("DEBUG_STATE", "hasCompanyWithJeffChat: $hasCompanyWithJeffChat")
//        Log.d("DEBUG_STATE", "hasDirectJeffChat: $hasDirectJeffChat")


        return StartChatUiState(
            showDirectJeff = !hasDirectJeffChat,
            showCompany = !hasCompanyChat,
            showCompanyWithJeff = !hasCompanyWithJeffChat
        )
    }

    fun setGroupMode(enabled: Boolean) {
        _isGroupMode.value = enabled
    }

    fun setGroupTitle(title: String) {
        _groupTitle.value = title
    }

    fun setGroupImage(url: String?) {
        _groupImageUrl.value = url
    }

    // COMPANY MEMBERS -> without current user
    // SELECTION STATE
    // Contains currently selected user IDs for new chats
    private val _selectedParticipantIds =
        MutableStateFlow<Set<String>>(emptySet())

    val selectedParticipantIds: StateFlow<Set<String>> =
        _selectedParticipantIds

    // Observes all company members (excluding the current user)
    val companyMembersUiState: StateFlow<List<User>> =
        companyIdState
            .flatMapLatest { companyId ->
                if (companyId == null) {
                    flowOf(emptyList())
                } else {
                    chatRepository.observeCompanyMembers(companyId)
                }
            }
            .combine(currentUserIdState) { users, currentUserId ->
                users.filter { it.id != currentUserId }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // Optionally adds the global Jeff user to the selection
    val companyMembersWithJeffUiState: StateFlow<List<User>> =
        combine(
            companyMembersUiState,
            jeffUserIdState
        ) { members, jeffId ->

            if (jeffId == null) {
                members
            } else {
                members + User(
                    id = jeffId,
                    displayName = "Jeff",
                    global = true
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // All company members + Jeff are available
    val generalMembersUiState: StateFlow<List<User>> =
        combine(companyMembersUiState, jeffUserIdState) { members, jeffId ->
            if (jeffId == null) members
            else members + User(
                id = jeffId,
                displayName = "Jeff",
                global = true
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // ACTIONS
    // Create or find a direct chat with Jeff
    fun startDirectJeffChat() {
        viewModelScope.launch {
            try {
                val currentUserId = currentUserIdState.value ?: return@launch
                val companyId = companyIdState.value ?: return@launch
                val jeffId = jeffUserIdState.value ?: return@launch

                val chatId = chatRepository.findOrCreateDirectChat(
                    companyId = companyId,
                    participantIds = listOf(currentUserId, jeffId)
                )

//                _navigateToChat.emit(chatId)
                _navigateToChat.emit(chatId to companyId)

            } catch (e: Exception) {
                _uiEvents.emit("Chat konnte nicht gestartet werden")
            }
        }
    }

    // Adds or removes user from selection
    // Automatically activates group mode with >= 2 participants
    fun toggleParticipantSelection(user: User) {

        val current = _selectedParticipantIds.value.toMutableSet()
        val locked = _lockedCompanyId.value

        if (current.contains(user.id)) {
            current.remove(user.id)

            if (current.isEmpty()) {
                _lockedCompanyId.value = null
            }
        } else {

            if (locked == null) {
                _lockedCompanyId.value = user.companyId
                current.add(user.id)
            } else if (locked == user.companyId) {
                current.add(user.id)
            }
            // else: ignorieren (andere Company)
        }

        _selectedParticipantIds.value = current
    }
//    fun toggleParticipantSelection(userId: String) {
//        val current = _selectedParticipantIds.value.toMutableSet()
//
//        if (current.contains(userId)) {
//            current.remove(userId)
//        } else {
//            current.add(userId)
//        }
//
//        _selectedParticipantIds.value = current
//
//        // Automatd Grouplogic
//        if (current.size >= 2) {
//            _isGroupMode.value = true
//        } else if (current.size == 1) {
//            // 1 Person -> frei wählbar
//            // nichts erzwingen
//        } else {
//            _isGroupMode.value = false
//        }
//    }

    // Creates chat based on current selection
    // Optionally uploads group image and updates chat afterwards
    fun createChatFromSelection() {
        viewModelScope.launch {
            try {

                val currentUserId = currentUserIdState.value ?: return@launch
                val companyId = companyIdState.value ?: return@launch
                val selected = _selectedParticipantIds.value
                val imageUri = _groupImageUri.value

                if (selected.isEmpty()) return@launch

                val participants = (selected + currentUserId).distinct()
                val isGroup = _isGroupMode.value || selected.size >= 2

                val finalTitle =
                    if (isGroup) {
                        _groupTitle.value.takeIf { it.isNotBlank() }
                            ?: "Gruppe"
                    } else null

                // 1. Chat ERST erstellen (ohne Bild)
                val chatId = chatRepository.createChat(
                    companyId = companyId,
                    participantIds = participants.toList(),
                    isGroupChat = isGroup,
                    title = finalTitle,
                    imageUrl = null
                )

                // 2. Falls Bild existiert → hochladen
                if (imageUri != null) {

                    val imageUrl = storageRepository.uploadGroupImage(
                        uri = imageUri,
                        chatId = chatId
                    )

                    // 3. Chat danach updaten
                    chatRepository.updateChatImage(
                        companyId = companyId,
                        chatId = chatId,
                        imageUrl = imageUrl
                    )
                }

                // 4. Reset danach
                resetSelection()
                _isGroupMode.value = false
                _groupTitle.value = ""
                _groupImageUri.value = null

//                _navigateToChat.emit(chatId)
                _navigateToChat.emit(chatId to companyId)
            } catch (e: Exception) {
                _uiEvents.emit("Chat konnte nicht erstellt werden")
            }
        }
    }

//

    fun resetSelection() {
        _selectedParticipantIds.value = emptySet()
    }

    fun prepareCompanySelection() {
        resetSelection()
        _isGroupMode.value = true
    }

    fun prepareCompanyWithJeffSelection() {
        val jeffId = jeffUserIdState.value ?: return
        resetSelection()
        _selectedParticipantIds.value = setOf(jeffId)
        _isGroupMode.value = true
    }

//    fun createGroupChatFromSelection() {
//        viewModelScope.launch {
//            try {
//                val currentUserId = currentUserIdState.value ?: return@launch
//                val companyId = companyIdState.value ?: return@launch
//
//                val participants = (_selectedParticipantIds.value + currentUserId).distinct()
//                if (participants.size < 2) return@launch
//
//                val title = _groupTitle.value.takeIf { it.isNotBlank() } ?: "Gruppe"
//
//                val chatId = chatRepository.createChat(
//                    companyId = companyId,
//                    participantIds = participants,
//                    isGroupChat = true,
//                    title = title,
//                    imageUrl = null
//                )
//
//                resetSelection()
//                _navigateToChat.emit(chatId)
//
//            } catch (e: Exception) {
//                _uiEvents.emit("Gruppenchat konnte nicht erstellt werden")
//            }
//        }
//    }
}

//    private val userChatsFlow: Flow<List<Chat>> =
//        combine(
//            currentUserIdState,
//            companyIdState,
//            currentUserIsGlobalState
//        ) { userId, companyId, isGlobal ->
//            Triple(userId, companyId, isGlobal)
//        }.flatMapLatest { (userId, companyId, isGlobal) ->
//
//            Log.d(
//                "CHAT_FLOW_DEBUG",
//                "userId=$userId | companyId=$companyId | isGlobal=$isGlobal"
//            )
//
//            when {
//                userId == null -> {
//                    Log.d("CHAT_FLOW_DEBUG", "→ userId NULL → empty")
//                    flowOf(emptyList())
//                }
//
//                isGlobal -> {
//                    Log.d("CHAT_FLOW_DEBUG", "→ GLOBAL branch")
//                    chatRepository.observeChatsForUserGlobal(userId)
//                }
//
//                companyId != null -> {
//                    Log.d("CHAT_FLOW_DEBUG", "→ COMPANY branch")
//                    chatRepository.observeChatsForUser(companyId, userId)
//                }
//
//                else -> {
//                    Log.d("CHAT_FLOW_DEBUG", "→ FALLBACK empty")
//                    flowOf(emptyList())
//                }
//            }
//        }
//    private val userChatsFlow: Flow<List<Chat>> =
//        combine(companyIdState, currentUserIdState) { companyId, userId ->
//            companyId to userId
//        }.flatMapLatest { (companyId, userId) ->
//            if (companyId == null || userId == null) {
//                flowOf(emptyList())
//            } else {
////                chatRepository.observeChatsForUser(companyId, userId)
//                chatRepository.observeChatsForUserGlobal(userId)
//            }
//        }