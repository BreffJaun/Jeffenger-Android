package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.example.jeffenger.utils.normalization.normalizeCompanyId
import com.example.jeffenger.utils.state.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepositoryInterface {
    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    override val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    override val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    init {
        addAuthListener()
    }

    override fun loginWithEmailAndPassword(
        email: String,
        password: String
    ) {
        _loadingState.value = LoadingState.Loading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loadingState.value = LoadingState.Success()
            }
            .addOnFailureListener {
                _loadingState.value = LoadingState.Error(
                    it.message ?: "Login fehlgeschlagen"
                )
            }
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String,
        company: String
    ) {
        _loadingState.value = LoadingState.Loading()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user!!.uid

                val newUser = User(
                    id = userId,
                    displayName = displayName,
                    email = email,
                    company = company,
                    companyId = normalizeCompanyId(company),
                    createdAt = System.currentTimeMillis(),
                    lastActiveAt = System.currentTimeMillis()
                )

                db.collection(CollectionNames.USERS.path)
                    .document(userId)
                    .set(newUser)
                    .addOnSuccessListener {
                        _loadingState.value = LoadingState.Success()
                    }
                    .addOnFailureListener {
                        _loadingState.value = LoadingState.Error(
                            it.message ?: "Fehler beim Speichern"
                        )
                    }
            }
            .addOnFailureListener {
                _loadingState.value = LoadingState.Error(
                    it.message ?: "Registrierung fehlgeschlagen"
                )
            }
    }

    override fun logout() {
        auth.signOut()
    }

    private fun addAuthListener() {
        auth.addAuthStateListener {
            _authState.value = it.currentUser
        }
    }
}