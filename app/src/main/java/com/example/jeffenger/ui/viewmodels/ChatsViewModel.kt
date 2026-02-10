package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.local.MockData
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.data.remote.model.ui_model.ChatListItemUiModel
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.utils.enums.AvatarType
import com.example.jeffenger.utils.helper.mapToAvatarUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ChatsViewModel(
    private val repository: ChatRepositoryInterface
) : ViewModel() {

    private val currentUserId = "user_jeff"

    val chatListItems: Flow<List<ChatListItemUiModel>> =
        repository.observeChats().map { chats ->
            chats.map { chat ->
                mapChatToUiModel(chat)
            }
        }

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

//    private fun mapChatToUiModel(chat: Chat): ChatListItemUiModel {
//
//        val users = MockData.users
//
//        val displayName =
//            if (chat.isGroupChat) {
//                chat.title ?: "Gruppe"
//            } else {
//                users
//                    .firstOrNull { it.id != currentUserId && chat.participantIds.contains(it.id) }
//                    ?.displayName
//                    ?: "Unbekannt"
//            }
//
//        val avatar = mapToAvatarUiModel(
//            chat = chat,
//            currentUserId = currentUserId,
//            users = users
//        )
//
//        return ChatListItemUiModel(
//            chatId = chat.id,
//            displayName = displayName,
//            avatar = avatar,
//            lastMessageText = chat.lastMessageText,
//            lastMessageTimestamp = chat.lastMessageTimestamp,
//            unreadCount = chat.unreadCount[currentUserId] ?: 0
//        )
//    }
}
