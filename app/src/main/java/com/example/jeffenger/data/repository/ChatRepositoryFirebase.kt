package com.example.jeffenger.data.repository

import com.example.jeffenger.data.local.MockData.chats
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ChatRepositoryFirebase : ChatRepositoryInterface {

    override fun observeChatsForUser(userId: String): Flow<List<Chat>> {
        // TODO: Firestore whereArrayContains("participantIds", userId)
        return flowOf(emptyList())
    }

    override fun observeChats(): Flow<List<Chat>> {
        // TODO: Firestore collection listener
        return flowOf(emptyList())
    }

    override fun observeChat(chatId: String): Flow<Chat?> {
        // TODO: Firestore document listener
        return flowOf(
            Chat(id = chatId)
        )
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> {
        // TODO: Firestore subcollection listener
        return flowOf(emptyList())
    }

    override fun observeUsers(userIds: List<String>): Flow<List<User>> {
        // TODO Firestore whereIn("id", userIds)
        return flowOf(emptyList())
    }

    override suspend fun sendMessage(message: Message) {
        // TODO: Firestore add()
        // pro forma: nichts tun
    }
}