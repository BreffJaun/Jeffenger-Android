package com.example.jeffenger.data.repository

import com.example.jeffenger.data.local.MockData.chats
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.ChatRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

        val results = mutableListOf<User>()
        val listeners = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()

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

                results.removeAll { u -> u.id in chunk }
                results.addAll(users)

                trySend(results.distinctBy { it.id })
            }

            listeners += listener
        }

        //  GLOBAL USERS (Jeff)
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

                results.removeAll { u -> u.id in chunk }
                results.addAll(users)

                trySend(results.distinctBy { it.id })
            }

            listeners += listener
        }

        awaitClose { listeners.forEach { it.remove() } }
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
}