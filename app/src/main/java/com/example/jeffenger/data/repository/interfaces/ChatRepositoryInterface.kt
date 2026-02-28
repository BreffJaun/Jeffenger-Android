package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepositoryInterface {

    fun observeChatsForUser(companyId: String, userId: String): Flow<List<Chat>>

    fun observeChatsForUserGlobal(userId: String): Flow<List<Chat>>

    fun observeAllCompanyMembers(): Flow<Map<String, List<User>>>

    fun observeChat(companyId: String, chatId: String): Flow<Chat?>

    fun observeLatestMessages(companyId: String, chatId: String): Flow<List<Message>>

    suspend fun loadMoreMessages(
        companyId: String,
        chatId: String,
        lastMessage: Message
    ): List<Message>
    fun observeUsers(companyId: String, userIds: List<String>): Flow<List<User>>

    fun observeCompanyMembers(companyId: String): Flow<List<User>>

    suspend fun sendMessage(companyId: String, chatId: String, message: Message)

    suspend fun createChat(
        companyId: String,
        participantIds: List<String>,
        isGroupChat: Boolean,
        title: String? = null,
        imageUrl: String?
    ): String

    suspend fun findDirectChat(
        companyId: String,
        participantIds: List<String>
    ): Chat?

    suspend fun findOrCreateDirectChat(
        companyId: String,
        participantIds: List<String>
    ): String

    suspend fun updateChatImage(
        companyId: String,
        chatId: String,
        imageUrl: String
    )

    suspend fun resetUnreadCount(
        companyId: String,
        chatId: String,
        userId: String
    )

    suspend fun editMessage(
        companyId: String,
        chatId: String,
        messageId: String,
        newText: String
    )

    suspend fun deleteMessage(
        companyId: String,
        chatId: String,
        messageId: String
    )

//    fun observeUsersFromMultipleCompanies(userIds: List<String>): Flow<List<User>>
}






