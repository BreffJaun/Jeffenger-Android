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

//@Serializable
//data class CalendarRoute(
//    val openEventId: String? = null
//)

@Serializable
object SettingsRoute