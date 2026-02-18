package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
) : UserRepositoryInterface {

    private val _appUser = MutableStateFlow<User?>(null)
    override val appUser: StateFlow<User?> = _appUser.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeUserChanges()
    }

    private fun observeUserChanges() {
        auth.addAuthStateListener { firebaseAuth ->
            // alten Firestore-Listener entfernen
            listenerRegistration?.remove()
            listenerRegistration = null

            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                _appUser.value = null
                return@addAuthStateListener
            }

            attachListenerForUser(uid)
        }
    }

    private fun attachListenerForUser(uid: String) {
        val localRef = db.collection(CollectionNames.USERS.path).document(uid)
        val globalRef = db.collection(CollectionNames.GLOBAL_USERS.path).document(uid)

        // 1) Erst prüfen, ob er in users existiert, sonst globalUsers
        localRef.get().addOnSuccessListener { snapshot ->
            val refToUse = if (snapshot.exists()) localRef else globalRef

            // 2) Dann auf das richtige Dokument live hören
            listenerRegistration = refToUse.addSnapshotListener { snap, error ->
                if (error != null) {
                    _appUser.value = null
                    return@addSnapshotListener
                }
                _appUser.value = snap?.toObject<User>()
            }
        }.addOnFailureListener {
            // falls get() fehlschlägt (Netz/Permission)
            _appUser.value = null
        }
    }

//    override fun observeAppUser(): Flow<User?> = appUser

    override fun observeGlobalUsers(): Flow<List<User>> = callbackFlow {
        val listener = db.collection(CollectionNames.GLOBAL_USERS.path)
            .whereEqualTo("isGlobal", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users)
            }

        awaitClose { listener.remove() }
    }
}

