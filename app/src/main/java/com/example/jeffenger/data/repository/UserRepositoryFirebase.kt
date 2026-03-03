package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
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

    /**
     * Listens to FirebaseAuth login/logout changes and updates the appUser accordingly.
     * Ensures that only the currently authenticated user is observed in Firestore.
     * observeUserChanges is the "watchdog" that constantly checks who is actually logged in.
     */
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

    /**
     * Attaches a live Firestore SnapshotListener to the authenticated user's document.
     * Falls back to the global collection if no local user document exists.
     * Lstens only to the current loggedin user
     */
    private fun attachListenerForUser(uid: String) {
        val localRef = db.collection(CollectionNames.USERS.path).document(uid)
        val globalRef = db.collection(CollectionNames.GLOBAL_USERS.path).document(uid)

        // 1 Erst prüfen, ob er in users existiert, sonst globalUsers
        localRef.get().addOnSuccessListener { snapshot ->
            val refToUse = if (snapshot.exists()) localRef else globalRef

            // 2 Dann auf das richtige Dokument live hören
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

    /**
     * Observes all global users in Firestore and emits live updates as a Flow.
     * Converts the SnapshotListener into a coroutine-based reactive stream.
     * Listens to all global users
     */
    override fun observeGlobalUsers(): Flow<List<User>> = callbackFlow {
        val listener = db.collection(CollectionNames.GLOBAL_USERS.path)
            .whereEqualTo("global", true)
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

    override fun observeUsersByIds(ids: List<String>): Flow<List<User>> = callbackFlow {

        if (ids.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("users")
            .whereIn(FieldPath.documentId(), ids)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents?.mapNotNull {
                    it.toObject(User::class.java)
                } ?: emptyList()

                trySend(users)
            }

        awaitClose { listener.remove() }
    }
}

