package com.example.jeffenger.data.repository.interfaces

import android.net.Uri

interface StorageRepositoryInterface {
    suspend fun uploadGroupImage(
        uri: Uri,
        chatId: String
    ): String
}