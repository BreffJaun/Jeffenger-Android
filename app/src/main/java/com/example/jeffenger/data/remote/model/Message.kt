package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.enums.MessageStatus

data class Message(
    val id: String = "",
    val chatId: String = "",

    val senderId: String = "",

    val text: String? = null,
    val imageUrl: String? = null,
    val fileUrl: String? = null,

    val createdAt: Long = 0L,
    val editedAt: Long? = null,

    val status: MessageStatus = MessageStatus.SENT,

    // Read receipts (Future-proof)
    val readBy: List<String> = emptyList()
)
