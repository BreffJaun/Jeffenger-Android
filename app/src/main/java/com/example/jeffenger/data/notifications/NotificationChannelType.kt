package com.example.jeffenger.data.notifications

enum class NotificationChannelType(
    val channelId: String,
    val channelName: String
) {
    CHAT_MESSAGES(
        channelId = "chat_messages_channel",
        channelName = "Chat Messages"
    )
}