package com.example.jeffenger.data.repository.interfaces

import android.net.Uri

interface StorageRepositoryInterface {
    suspend fun uploadGroupImage(
        uri: Uri,
        chatId: String
    ): String

    suspend fun uploadUserAvatar(
        uri: Uri,
        userId: String
    ): String

    suspend fun deleteAvatar(userId: String)
}