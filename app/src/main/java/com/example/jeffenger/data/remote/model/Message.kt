package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.enums.MessageStatus
import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

data class Message(
    @DocumentId
    val id: String = "",
    val chatId: String = "",

    val senderId: String = "",

    val text: String? = null,
    val imageUrl: String? = null,
    val fileUrl: String? = null,

//    val createdAt: Long = 0L,
    // Server time (für Sortierung)
    val createdAt: Timestamp? = null,
    // Client fallback (für sofortige Anzeige)
    val clientCreatedAt: Long = 0L,

    val editedAt: Timestamp? = null,

    val status: MessageStatus = MessageStatus.SENT,

    // Read receipts (Future-proof)
    val readBy: List<String> = emptyList()
)
