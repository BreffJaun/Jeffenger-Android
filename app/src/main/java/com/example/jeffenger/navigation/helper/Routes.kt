package com.example.jeffenger.navigation.helper

import kotlinx.serialization.Serializable

@Serializable
object ChatsRoute

@Serializable
data class ChatRoute(
    val id: String,
    val companyId: String
)

@Serializable
object CalendarRoute

@Serializable
object SettingsRoute