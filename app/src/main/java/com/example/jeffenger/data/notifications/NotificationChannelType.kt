package com.example.jeffenger.data.notifications

enum class NotificationChannelType(
    val channelId: String,
    val channelName: String
) {
    CHAT_MESSAGES(
        channelId = "chat_messages_channel",
        channelName = "Chat Messages"
    ),

    CALENDAR_EVENTS(
    channelId = "calendar_events_channel",
    channelName = "Calendar Events"
    )
}