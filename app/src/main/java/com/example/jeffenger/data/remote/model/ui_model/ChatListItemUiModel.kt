package com.example.jeffenger.data.remote.model.ui_model

data class ChatListItemUiModel(
    val chatId: String,
    val displayName: String,
    val lastMessageText: String?,
    val lastMessageTimestamp: Long,
    val unreadCount: Int,
    val avatar: AvatarUiModel
)