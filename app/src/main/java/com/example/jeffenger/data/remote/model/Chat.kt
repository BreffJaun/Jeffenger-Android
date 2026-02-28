package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.enums.MessageStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId
    val id: String = "",

    val participantIds: List<String> = emptyList(),
    var companyId: String? = null,
    val groupChat: Boolean = false,

    val title: String? = null,            // Groupname
    val imageUrl: String? = null,         // Groupimage

    val lastMessageId: String? = null,
    val lastMessageText: String? = null,
    val lastMessageTimestamp: Timestamp? = null,

    val createdAt: Timestamp? = null,

    // Unread handling
    val unreadCount: Map<String, Int> = emptyMap(),

    // Meta
    val mutedUserIds: List<String> = emptyList(),

    val directChatKey: String? = null
)



