package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryFirebase(
    private val auth: FirebaseAuth
) : AuthRepositoryInterface {
    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    override val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    private val _appUser = MutableStateFlow<User?>(null)
    override val appUser: StateFlow<User?> = _appUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        addAuthListener()
    }

    override fun loginWithEmailAndPassword(
        email: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnFailureListener {
            // Beispiel wie man Fehlermessages setzen kann.
            _errorMessage.value = "Fehler beim Login"
        }
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override fun logout() {
        auth.signOut()
    }

    private fun addAuthListener() {
        // Damit stellen wir sicher, das wir auch eingeloggt bleiben können,
        // des Weiteren sparen wir uns damit die Setzung des authStates
        // in dem addOnSuccessListener der jeweiligen Funktionen.
        auth.addAuthStateListener {
            _authState.value = it.currentUser
        }
    }
}