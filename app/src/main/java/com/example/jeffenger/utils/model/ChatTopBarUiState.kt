package com.example.jeffenger.utils.model

import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel

data class ChatTopBarUiState(
    val chatId: String,
    val title: String,
    val subtitle: String,
    val avatar: AvatarUiModel,
    val isGroup: Boolean
)