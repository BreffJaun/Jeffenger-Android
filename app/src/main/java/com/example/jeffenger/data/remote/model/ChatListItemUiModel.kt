package com.example.jeffenger.data.remote.model


data class ChatListItemUiModel(
    val chatId: String,

    val displayName: String,
    val avatarUrl: String?,
    val initials: String,

    val lastMessageText: String?,
    val lastMessageTimestamp: Long,

    val unreadCount: Int
)