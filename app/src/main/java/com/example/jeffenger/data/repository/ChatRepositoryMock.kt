//package com.example.jeffenger.data.repository
//
//import com.example.jeffenger.data.local.MockData
//import com.example.jeffenger.data.remote.model.Chat
//import com.example.jeffenger.data.remote.model.Message
//import com.example.jeffenger.data.remote.model.User
//import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//
//class ChatRepositoryMock : ChatRepositoryInterface {
//
//    private val chats = MutableStateFlow(MockData.chats)
//    private val messages = MutableStateFlow(MockData.messages)
//    private val users = MutableStateFlow(MockData.users)
//
//    override fun observeChatsForUser(
//        companyId: String,
//        userId: String
//    ): Flow<List<Chat>> =
//        chats.map { list ->
//            list.filter { chat ->
//                chat.participantIds.contains(userId)
//            }
//        }
//
//    override fun observeChat(
//        companyId: String,
//        chatId: String
//    ): Flow<Chat?> =
//        chats.map { list ->
//            list.find { it.id == chatId }
//        }
//
//    override fun observeLatestMessages(
//        companyId: String,
//        chatId: String
//    ): Flow<List<Message>> = callbackFlow {
//        TODO()
//    }
//
//    override suspend fun loadMoreMessages(
//        companyId: String,
//        chatId: String,
//        lastMessage: Message
//    ): List<Message> {
//        TODO()
//    }
//
////    override fun observeMessages(
////        companyId: String,
////        chatId: String
////    ): Flow<List<Message>> =
////        messages.map { list ->
////            list.filter { it.chatId == chatId }
////        }
//
//    override fun observeUsers(
//        companyId: String,
//        userIds: List<String>
//    ): Flow<List<User>> =
//        users.map { list ->
//            list.filter { it.id in userIds }
//        }
//
//    override fun observeCompanyMembers(companyId: String): Flow<List<User>> =
//        users.map { list ->
//            list.filter { it.companyId == companyId }
//        }
//
//    override suspend fun sendMessage(
//        companyId: String,
//        chatId: String,
//        message: Message
//    ) {
//        messages.value = messages.value + message
//    }
//
//    override suspend fun createChat(
//        companyId: String,
//        participantIds: List<String>,
//        isGroupChat: Boolean,
//        title: String?,
//        imageUrl: String?
//    ): String {
//        TODO()
////        val id = "mock_${System.currentTimeMillis()}"
////
////        val now = System.currentTimeMillis()
////
////        val newChat = Chat(
////            id = id,
////            participantIds = participantIds.distinct(),
////            groupChat = isGroupChat,
////            title = title,
////            createdAt = now,
////            lastMessageTimestamp = now,
////            lastMessageText = null,
////            lastMessageId = null,
////            unreadCount = emptyMap()
////        )
////
////        chats.value = chats.value + newChat
////        return id
//    }
//
//    override suspend fun findDirectChat(
//        companyId: String,
//        participantIds: List<String>
//    ): Chat? {
//
//        val key = participantIds
//            .distinct()
//            .sorted()
//            .joinToString("_")
//
//        return chats.value.firstOrNull { chat ->
//            !chat.groupChat &&
//                    chat.directChatKey == key
//        }
//    }
//
//    override suspend fun findOrCreateDirectChat(
//        companyId: String,
//        participantIds: List<String>
//    ): String {
//        TODO()
////        val distinctParticipants = participantIds.distinct()
////
////        val existing = findDirectChat(companyId, distinctParticipants)
////
////        return if (existing != null) {
////            existing.id
////        } else {
////
////            val newId = "mock_${System.currentTimeMillis()}"
////
////            val newChat = Chat(
////                id = newId,
////                participantIds = distinctParticipants,
////                groupChat = false,
////                title = null,
////                createdAt = System.currentTimeMillis(),
////                lastMessageTimestamp = System.currentTimeMillis(),
////                lastMessageText = null,
////                lastMessageId = null,
////                unreadCount = emptyMap(),
////                directChatKey = distinctParticipants.sorted().joinToString("_")
////            )
////
////            chats.value = chats.value + newChat
////
////            newId
////        }
//    }
//
//    override suspend fun updateChatImage(
//        companyId: String,
//        chatId: String,
//        imageUrl: String
//    ) {
//        // Für Mock erstmal leer lassen
//    }
//
//    override suspend fun resetUnreadCount(
//        companyId: String,
//        chatId: String,
//        userId: String
//    ) {
//        // Für Mock erstmal leer lassen
//    }
//
//    override suspend fun editMessage(
//        companyId: String,
//        chatId: String,
//        messageId: String,
//        newText: String
//    ) {
//        // Für Mock erstmal leer lassen
//    }
//
//    override suspend fun deleteMessage(
//        companyId: String,
//        chatId: String,
//        messageId: String
//    ) {
//        // Für Mock erstmal leer lassen
//    }
//}
//
//
//
