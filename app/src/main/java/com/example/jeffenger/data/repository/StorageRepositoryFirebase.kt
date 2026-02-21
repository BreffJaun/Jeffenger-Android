package com.example.jeffenger.data.repository

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.example.jeffenger.data.repository.interfaces.StorageRepositoryInterface

class StorageRepositoryFirebase(
    private val storage: FirebaseStorage
) : StorageRepositoryInterface {

    override suspend fun uploadGroupImage(
        uri: Uri,
        chatId: String
    ): String {

        val ref = storage
            .reference
            .child("chat_images/$chatId.jpg")

        ref.putFile(uri).await()

        return ref.downloadUrl.await().toString()
    }
}