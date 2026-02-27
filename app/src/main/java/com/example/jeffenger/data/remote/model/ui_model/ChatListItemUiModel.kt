package com.example.jeffenger.data.remote.model.ui_model

import com.google.firebase.Timestamp

data class ChatListItemUiModel(
    val chatId: String,
    val displayName: String,
    val lastMessageText: String?,
    val lastMessageTimestamp: Timestamp? = null,
    val unreadCount: Int,
    val avatar: AvatarUiModel
)