package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.local.MockData
import com.example.jeffenger.data.local.MockData.chats
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.ui_model.ChatListItemUiModel
import com.example.jeffenger.data.repository.AuthRepositoryFirebase
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.utils.classifier.ChatClassifier
import com.example.jeffenger.utils.enums.ChatType
import com.example.jeffenger.utils.model.StartChatUiState
import com.example.jeffenger.utils.mapper.mapToAvatarUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ChatsViewModel(
    private val repository: ChatRepositoryInterface,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    private val jeffId = "user_jeff"
    private val currentUserId = "user_jeff"
//    private val currentUserId = authRepository.authState.value?.uid

    private val userChatsFlow = repository.observeChatsForUser(currentUserId)

    val startChatUiState: StateFlow<StartChatUiState> =
//        repository.observeChats()
        userChatsFlow
            .map { chats ->
                buildStartChatUiState(chats)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                StartChatUiState()
            )

//    val chatListItems: Flow<List<ChatListItemUiModel>> =
////        repository.observeChats()
//        userChatsFlow
//            .map { chats ->
//            chats.map { chat ->
//                mapChatToUiModel(chat)
//            }
//        }

    val chatListItems: StateFlow<List<ChatListItemUiModel>> =
        userChatsFlow
            .map { chats ->
                chats.map { chat ->
                    mapChatToUiModel(chat)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private fun mapChatToUiModel(chat: Chat): ChatListItemUiModel {

        val users = MockData.users

        val displayName =
            if (chat.isGroupChat) {
                chat.title ?: "Gruppe"
            } else {
                users
                    .first { it.id != currentUserId && chat.participantIds.contains(it.id) }
                    .displayName
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
            ChatClassifier.classify(
                chat = it,
                currentUserId = currentUserId,
                jeffId = jeffId,
                users = MockData.users
            )
        }

        return StartChatUiState(
            showDirectJeff = ChatType.DIRECT_JEFF !in chatTypes,
            showCompany = ChatType.COMPANY_ONLY !in chatTypes,
            showCompanyWithJeff = ChatType.COMPANY_WITH_JEFF !in chatTypes
        )
    }
}







