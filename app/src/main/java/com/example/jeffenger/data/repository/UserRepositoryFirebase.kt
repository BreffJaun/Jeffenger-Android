package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.enums.CollectionNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    private val _appUser = MutableStateFlow<User?>(null)
    val appUser: StateFlow<User?> = _appUser.asStateFlow()

    init {
        getAppUserAsSnapshotListener()
    }

    fun getAppUserAsSnapshotListener() {
        val userID = auth.currentUser?.uid
        userID?.let { id ->
            db.collection(CollectionNames.USERS.path)
                .document(id)
                .addSnapshotListener { snapshot, error ->
                    _appUser.value = snapshot?.toObject<User>()
                }
        }
    }

    fun getAppUserAsFlow(): Flow<User?> = callbackFlow {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val docRef = db.collection(CollectionNames.USERS.path)
            .document(userID)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if(error != null) {
                close(error)
                return@addSnapshotListener
            }
            val appUser = snapshot?.toObject<User>()
            trySend(appUser)
        }

        awaitClose { listener.remove() }
    }
}