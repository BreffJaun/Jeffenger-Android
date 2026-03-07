package com.example.jeffenger.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val company: String = "",
    val companyId: String = "",

    val avatarUrl: String? = null,
    val bio: String? = null,

    val createdAt: Long = 0L,
    val lastActiveAt: Long = 0L,

    val online: Boolean = false,

    // Settings
    val darkModeEnabled: Boolean? = null,
    val notificationsEnabled: Boolean = false,

    // Meta / Future-proof
    val deviceTokens: List<String> = emptyList(),
    val blockedUserIds: List<String> = emptyList(),
    val global: Boolean = false
)

