package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.collections.emptyList

class ChatRepositoryFirebase(
    private val db: FirebaseFirestore
) : ChatRepositoryInterface {

    override fun observeChatsForUser(
        companyId: String,
        userId: String
    ): Flow<List<Chat>> = callbackFlow {

        val ref = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .whereArrayContains("participantIds", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val chats = snapshot?.documents
                ?.mapNotNull { it.toObject(Chat::class.java) }
                ?: emptyList()

            trySend(chats)
        }

        awaitClose { listener.remove() }
    }

    override fun observeChat(
        companyId: String,
        chatId: String
    ): Flow<Chat?> = callbackFlow {

        val ref = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .document(chatId)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val chat = snapshot?.toObject(Chat::class.java)
            trySend(chat)
        }

        awaitClose { listener.remove() }
    }


    override fun observeMessages(
        companyId: String,
        chatId: String
    ): Flow<List<Message>> = callbackFlow {

        val ref = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .document(chatId)
            .collection(CollectionNames.MESSAGES.path)
            .orderBy("createdAt", Query.Direction.ASCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val messages = snapshot?.documents
                ?.mapNotNull { it.toObject(Message::class.java) }
                ?: emptyList()

            trySend(messages)
        }

        awaitClose { listener.remove() }
    }

    override fun observeUsers(
        companyId: String,
        userIds: List<String>
    ): Flow<List<User>> = callbackFlow {

        if (userIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val companyUsers = mutableMapOf<String, User>()
        val globalUsers = mutableMapOf<String, User>()
        val listeners = mutableListOf<ListenerRegistration>()

        // COMPANY USERS
        val companyUsersRef = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.USERS.path)

        userIds.chunked(10).forEach { chunk ->

            val query = companyUsersRef.whereIn(
                FieldPath.documentId(),
                chunk
            )

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents
                    ?.mapNotNull { it.toObject(User::class.java) }
                    ?: emptyList()

                users.forEach { companyUsers[it.id] = it }

                // Company überschreibt Global
                val merged = (globalUsers + companyUsers).values.toList()
                trySend(merged)
            }

            listeners += listener
        }

        // GLOBAL USERS
        val globalUsersRef = db.collection(CollectionNames.GLOBAL_USERS.path)

        userIds.chunked(10).forEach { chunk ->

            val query = globalUsersRef.whereIn(
                FieldPath.documentId(),
                chunk
            )

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents
                    ?.mapNotNull { it.toObject(User::class.java) }
                    ?: emptyList()

                users.forEach { globalUsers[it.id] = it }

                val merged = (globalUsers + companyUsers).values.toList()
                trySend(merged)
            }

            listeners += listener
        }

        awaitClose { listeners.forEach { it.remove() } }
    }

    override fun observeCompanyMembers(
        companyId: String
    ): Flow<List<User>> = callbackFlow {

        val ref = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.USERS.path)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val users = snapshot?.documents
                ?.mapNotNull { it.toObject(User::class.java) }
                ?: emptyList()

            trySend(users)
        }

        awaitClose { listener.remove() }
    }


    override suspend fun sendMessage(
        companyId: String,
        chatId: String,
        message: Message
    ) {

        val chatRef = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .document(chatId)

        val msgRef = chatRef
            .collection(CollectionNames.MESSAGES.path)
            .document()

        val msgToSave = message.copy(
            id = msgRef.id,
            chatId = chatId
        )

        // 1) Save message
        msgRef.set(msgToSave).await()

        // 2) Update chat metadata
        val lastText = msgToSave.text
            ?: if (msgToSave.imageUrl != null) "📷 Bild" else "Nachricht"

        chatRef.update(
            mapOf(
                "lastMessageId" to msgToSave.id,
                "lastMessageText" to lastText,
                "lastMessageTimestamp" to msgToSave.createdAt
            )
        ).await()
    }


    override suspend fun createChat(
        companyId: String,
        participantIds: List<String>,
        groupChat: Boolean,
        title: String?,
        imageUrl: String?
    ): String {

        val chatRef = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .document()

        val now = System.currentTimeMillis()

        val distinctParticipants = participantIds.distinct()

        val directKey =
            if (!groupChat) {
                distinctParticipants.sorted().joinToString("_")
            } else null

        val chatToSave = Chat(
            id = chatRef.id,
            participantIds = distinctParticipants,
            groupChat = groupChat,
            title = title,
            imageUrl = imageUrl,
            createdAt = now,
            lastMessageTimestamp = now,
            lastMessageText = null,
            lastMessageId = null,
            unreadCount = emptyMap(),
            directChatKey = directKey
        )

        chatRef.set(chatToSave).await()
        return chatRef.id
    }

    override suspend fun findDirectChat(
        companyId: String,
        participantIds: List<String>
    ): Chat? {

        val key = participantIds
            .distinct()
            .sorted()
            .joinToString("_")

        val snapshot = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .whereEqualTo("groupChat", false)
            .whereEqualTo("directChatKey", key)
            .limit(1)
            .get()
            .await()

        return snapshot.documents
            .firstOrNull()
            ?.toObject(Chat::class.java)
    }

    override suspend fun findOrCreateDirectChat(
        companyId: String,
        participantIds: List<String>
    ): String {

        val distinctParticipants = participantIds.distinct()
        val key = distinctParticipants
            .sorted()
            .joinToString("_")

        val snapshot = db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .whereEqualTo("groupChat", false)
            .whereEqualTo("directChatKey", key)
            .limit(1)
            .get()
            .await()

        val existing = snapshot.documents
            .firstOrNull()
            ?.toObject(Chat::class.java)

        return if (existing != null) {
            existing.id
        } else {
            createChat(
                companyId = companyId,
                participantIds = distinctParticipants,
                groupChat = false,
                title = null,
                imageUrl = null
            )
        }
    }

    override suspend fun updateChatImage(
        companyId: String,
        chatId: String,
        imageUrl: String
    ) {
        db.collection(CollectionNames.COMPANIES.path)
            .document(companyId)
            .collection(CollectionNames.CHATS.path)
            .document(chatId)
            .update("imageUrl", imageUrl)
            .await()
    }
}