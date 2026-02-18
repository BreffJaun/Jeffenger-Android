package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepositoryInterface {

    fun observeChatsForUser(companyId: String, userId: String): Flow<List<Chat>>
//    fun observeChats(): Flow<List<Chat>>
    fun observeChat(companyId: String, chatId: String): Flow<Chat?>
    fun observeMessages(companyId: String, chatId: String): Flow<List<Message>>

    fun observeUsers(companyId: String, userIds: List<String>): Flow<List<User>>

    fun observeCompanyMembers(companyId: String): Flow<List<User>>

    suspend fun sendMessage(companyId: String, chatId: String, message: Message)

    suspend fun createChat(
        companyId: String,
        participantIds: List<String>,
        isGroupChat: Boolean,
        title: String? = null
    ): String

    suspend fun findDirectChat(
        companyId: String,
        participantIds: List<String>
    ): Chat?

    suspend fun findOrCreateDirectChat(
        companyId: String,
        participantIds: List<String>
    ): String
}






