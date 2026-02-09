package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepositoryInterface {
    fun observeChats(): Flow<List<Chat>>
    fun observeChat(chatId: String): Flow<Chat?>
    fun observeMessages(chatId: String): Flow<List<Message>>

    fun observeUsers(userIds: List<String>): Flow<List<User>>
    suspend fun sendMessage(message: Message)
}