package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.helper.normalizeCompanyId

data class User(
    val id: String = "",                 // Firebase UID
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val company: String = "",
    val companyId: String = "",

    val avatarUrl: String? = null,
    val bio: String? = null,

    val createdAt: Long = 0L,
    val lastActiveAt: Long = 0L,

    val isOnline: Boolean = false,

    // Settings
    val darkModeEnabled: Boolean? = null,
    val notificationsEnabled: Boolean = true,

    // Meta / Future-proof
    val deviceTokens: List<String> = emptyList(),
    val blockedUserIds: List<String> = emptyList()
)

//val user = User(
//    id = uid,
//    username = username,
//    displayName = displayName,
//    email = email,
//    company = companyInput,
//    companyId = normalizeCompanyId(companyInput),
//    createdAt = System.currentTimeMillis()
//)