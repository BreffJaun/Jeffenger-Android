package com.example.jeffenger.data.repository

import com.example.jeffenger.data.local.MockData
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class ChatRepositoryMock : ChatRepositoryInterface {

    private val chats = MutableStateFlow(MockData.chats)
    private val messages = MutableStateFlow(MockData.messages)
    private val users = MutableStateFlow(MockData.users)

    override fun observeChatsForUser(
        companyId: String,
        userId: String
    ): Flow<List<Chat>> =
        chats.map { list ->
            list.filter { chat ->
                chat.participantIds.contains(userId)
            }
        }

    override fun observeChat(
        companyId: String,
        chatId: String
    ): Flow<Chat?> =
        chats.map { list ->
            list.find { it.id == chatId }
        }

    override fun observeMessages(
        companyId: String,
        chatId: String
    ): Flow<List<Message>> =
        messages.map { list ->
            list.filter { it.chatId == chatId }
        }

    override fun observeUsers(
        companyId: String,
        userIds: List<String>
    ): Flow<List<User>> =
        users.map { list ->
            list.filter { it.id in userIds }
        }

    override fun observeCompanyMembers(companyId: String): Flow<List<User>> =
        users.map { list ->
            list.filter { it.companyId == companyId }
        }

    override suspend fun sendMessage(
        companyId: String,
        chatId: String,
        message: Message
    ) {
        messages.value = messages.value + message
    }

    override suspend fun createChat(
        companyId: String,
        participantIds: List<String>,
        isGroupChat: Boolean,
        title: String?
    ): String {
        val id = "mock_${System.currentTimeMillis()}"

        val now = System.currentTimeMillis()

        val newChat = Chat(
            id = id,
            participantIds = participantIds.distinct(),
            groupChat = isGroupChat,
            title = title,
            createdAt = now,
            lastMessageTimestamp = now,
            lastMessageText = null,
            lastMessageId = null,
            unreadCount = emptyMap()
        )

        chats.value = chats.value + newChat
        return id
    }

    override suspend fun findDirectChat(
        companyId: String,
        participantIds: List<String>
    ): Chat? {

        val key = participantIds
            .distinct()
            .sorted()
            .joinToString("_")

        return chats.value.firstOrNull { chat ->
            !chat.groupChat &&
                    chat.directChatKey == key
        }
    }

    override suspend fun findOrCreateDirectChat(
        companyId: String,
        participantIds: List<String>
    ): String {

        val distinctParticipants = participantIds.distinct()

        val existing = findDirectChat(companyId, distinctParticipants)

        return if (existing != null) {
            existing.id
        } else {

            val newId = "mock_${System.currentTimeMillis()}"

            val newChat = Chat(
                id = newId,
                participantIds = distinctParticipants,
                groupChat = false,
                title = null,
                createdAt = System.currentTimeMillis(),
                lastMessageTimestamp = System.currentTimeMillis(),
                lastMessageText = null,
                lastMessageId = null,
                unreadCount = emptyMap(),
                directChatKey = distinctParticipants.sorted().joinToString("_")
            )

            chats.value = chats.value + newChat

            newId
        }
    }
}

//class ChatRepositoryMock : ChatRepositoryInterface {
//
//    private val chats = MutableStateFlow(MockData.chats)
//    private val messages = MutableStateFlow(MockData.messages)
//    private val users = MutableStateFlow(MockData.users)
//
//    override fun observeChatsForUser(userId: String): Flow<List<Chat>> =
//        chats.map { list ->
//            list.filter { chat ->
//                chat.participantIds.contains(userId)
//            }
//        }
//
//    override fun observeChats(): Flow<List<Chat>> = chats
//
//    override fun observeChat(chatId: String): Flow<Chat?> =
//        chats.map { list -> list.find { it.id == chatId } }
//
//    override fun observeMessages(chatId: String): Flow<List<Message>> =
//        messages.map { list -> list.filter { it.chatId == chatId } }
//
//    override fun observeUsers(userIds: List<String>): Flow<List<User>> =
//        users.map { list ->
//            list.filter { it.id in userIds }
//        }
//    override suspend fun sendMessage(message: Message) {
//        messages.value = messages.value + message
//    }
//}

