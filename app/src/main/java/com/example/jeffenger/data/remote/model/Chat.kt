package com.example.jeffenger.data.remote.model

data class Chat(
    val id: String = "",

    val participantIds: List<String> = emptyList(),
    val isGroupChat: Boolean = false,

    val title: String? = null,            // Groupname
    val imageUrl: String? = null,         // Groupimage

    val lastMessageId: String? = null,
    val lastMessageText: String? = null,
    val lastMessageTimestamp: Long = 0L,

    val createdAt: Long = 0L,

    // Unread handling
    val unreadCount: Map<String, Int> = emptyMap(),

    // Meta
    val mutedUserIds: List<String> = emptyList()
)