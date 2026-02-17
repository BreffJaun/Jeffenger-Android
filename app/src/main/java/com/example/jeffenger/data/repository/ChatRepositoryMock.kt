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

    override suspend fun sendMessage(
        companyId: String,
        chatId: String,
        message: Message
    ) {
        messages.value = messages.value + message
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

