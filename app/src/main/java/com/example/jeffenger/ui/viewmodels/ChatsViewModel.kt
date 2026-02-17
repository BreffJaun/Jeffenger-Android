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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModel(
    private val chatRepository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val currentUserIdFlow = authRepository.authState.map { it?.uid }
    private val companyIdFlow = userRepository.appUser.map { it?.companyId }

    // RAW FIREBASE DATA
    private val userChatsFlow: Flow<List<Chat>> =
        // combine means -> whenever one of the two changes, build a new result from both.
        combine(companyIdFlow, currentUserIdFlow) { companyId, userId ->
            companyId to userId
        }
            // flatMapLatest -> as soon as companyId or userId changes, the old flow is terminated and a new one is started.
            // A Flow-Operator
            .flatMapLatest { (companyId, userId) ->
                if (companyId == null || userId == null) {
                    flowOf(emptyList())
                } else {
                    chatRepository.observeChatsForUser(companyId, userId)
                }
            }


    val startChatUiState: StateFlow<StartChatUiState> =
        userChatsFlow
            .map { chats ->
                buildStartChatUiState(chats)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                StartChatUiState()
            )

    // UI PREPARED DATA
    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
        combine(userChatsFlow, companyIdFlow, currentUserIdFlow) { chats, companyId, userId ->
            Triple(chats, companyId, userId)
        }
            .flatMapLatest { (chats, companyId, userId) ->
                if (companyId == null || userId == null) {
                    flowOf(emptyList())
                } else {

                    // flatMap -> Make a (flat) list out of liszs. Flat means fom nested to one level.
                    // A List-Operator
                    val allUserIds = chats.flatMap { it.participantIds }.distinct()

                    chatRepository
                        .observeUsers(companyId, allUserIds)
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
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private fun mapChatToUiModel(
        chat: Chat,
        currentUserId: String,
        users: List<User>
    ): ChatListItemUiModel {

        val displayName =
            if (chat.isGroupChat) {
                chat.title ?: "Gruppe"
            } else {
                users
                    .firstOrNull {
                        it.id != currentUserId &&
                                chat.participantIds.contains(it.id)
                    }
                    ?.displayName ?: "Unbekannt"
            }

        return ChatListItemUiModel(
            chatId = chat.id,
            displayName = displayName,
            lastMessageText = chat.lastMessageText,
            lastMessageTimestamp = chat.lastMessageTimestamp,
            unreadCount = chat.unreadCount[currentUserId] ?: 0,
            avatar = mapToAvatarUiModel(
                chat = chat,
                currentUserId = currentUserId,
                users = users
            )
        )
    }

    private fun buildStartChatUiState(
        chats: List<Chat>
    ): StartChatUiState {

        val chatTypes = chats.map {
            when {
                it.isGroupChat -> ChatType.COMPANY_ONLY
                else -> ChatType.DIRECT_JEFF
            }
        }

        return StartChatUiState(
            showDirectJeff = ChatType.DIRECT_JEFF !in chatTypes,
            showCompany = ChatType.COMPANY_ONLY !in chatTypes,
            showCompanyWithJeff = true
        )
    }
}







