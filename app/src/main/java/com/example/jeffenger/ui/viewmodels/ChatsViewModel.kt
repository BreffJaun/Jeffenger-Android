package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.local.MockData
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.ChatListItemUiModel
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
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

        val otherUser =
            if (chat.isGroupChat) null
            else users.firstOrNull {
                it.id != currentUserId && chat.participantIds.contains(it.id)
            }

        val displayName =
            if (chat.isGroupChat) {
                chat.title ?: "Gruppe"
            } else {
                otherUser?.displayName
                    ?.ifBlank { otherUser.username }
                    ?: "Unbekannt"
            }

        val initials =
            displayName
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }

        val avatarUrl =
            if (chat.isGroupChat) chat.imageUrl
            else otherUser?.avatarUrl

        return ChatListItemUiModel(
            chatId = chat.id,
            displayName = displayName,
            avatarUrl = avatarUrl,
            initials = initials,
            lastMessageText = chat.lastMessageText,
            lastMessageTimestamp = chat.lastMessageTimestamp,
            unreadCount = chat.unreadCount[currentUserId] ?: 0
        )
    }
}
