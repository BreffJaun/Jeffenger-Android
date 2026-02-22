package com.example.jeffenger.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModel(
    private val chatRepository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface
) : ViewModel() {

    // AUTH & USER STATE
    private val currentUserIdState = authRepository.authState
        .map { it?.uid }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val companyIdState = userRepository.appUser
        .map { it?.companyId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val jeffUserIdState = userRepository.observeGlobalUsers()
        .map { users -> users.firstOrNull { it.global }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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
    private val _selectedParticipantIds =
        MutableStateFlow<Set<String>>(emptySet())

    val selectedParticipantIds: StateFlow<Set<String>> =
        _selectedParticipantIds

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
    fun startDirectJeffChat() {
        viewModelScope.launch {

            val currentUserId = currentUserIdState.value ?: return@launch
            val companyId = companyIdState.value ?: return@launch
            val jeffId = jeffUserIdState.value ?: return@launch

            val chatId = chatRepository.findOrCreateDirectChat(
                companyId = companyId,
                participantIds = listOf(currentUserId, jeffId)
            )

            _navigateToChat.emit(chatId)
        }
    }

    fun toggleParticipantSelection(userId: String) {
        val current = _selectedParticipantIds.value.toMutableSet()

        if (current.contains(userId)) {
            current.remove(userId)
        } else {
            current.add(userId)
        }

        _selectedParticipantIds.value = current

        // Automatd Grouplogic
        if (current.size >= 2) {
            _isGroupMode.value = true
        } else if (current.size == 1) {
            // 1 Person -> frei wählbar
            // nichts erzwingen
        } else {
            _isGroupMode.value = false
        }
    }

    fun createChatFromSelection() {
        viewModelScope.launch {

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

            // 🔥 1. Chat ERST erstellen (ohne Bild)
            val chatId = chatRepository.createChat(
                companyId = companyId,
                participantIds = participants.toList(),
                isGroupChat = isGroup,
                title = finalTitle,
                imageUrl = null
            )

            // 🔥 2. Falls Bild existiert → hochladen
            if (imageUri != null) {

                val imageUrl = storageRepository.uploadGroupImage(
                    uri = imageUri,
                    chatId = chatId
                )

                // 🔥 3. Chat danach updaten
                chatRepository.updateChatImage(
                    companyId = companyId,
                    chatId = chatId,
                    imageUrl = imageUrl
                )
            }

            // 🔥 4. Reset danach
            resetSelection()
            _isGroupMode.value = false
            _groupTitle.value = ""
            _groupImageUri.value = null

            _navigateToChat.emit(chatId)
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

    fun createGroupChatFromSelection() {
        viewModelScope.launch {
            val currentUserId = currentUserIdState.value ?: return@launch
            val companyId = companyIdState.value ?: return@launch

            val participants = (_selectedParticipantIds.value + currentUserId).distinct()
            if (participants.size < 2) return@launch

            val title = _groupTitle.value.takeIf { it.isNotBlank() } ?: "Gruppe"

            val chatId = chatRepository.createChat(
                companyId = companyId,
                participantIds = participants,
                isGroupChat = true,
                title = title,
                imageUrl = null
            )

            resetSelection()
            _navigateToChat.emit(chatId)
        }
    }
}


