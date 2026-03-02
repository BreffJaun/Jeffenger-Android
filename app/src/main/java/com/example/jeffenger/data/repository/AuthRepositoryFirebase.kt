package com.example.jeffenger.data.repository

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.utils.enums.CollectionNames
import com.example.jeffenger.utils.error.AppError
import com.example.jeffenger.utils.error.ErrorMapper
import com.example.jeffenger.utils.normalization.normalizeCompanyId
import com.example.jeffenger.utils.state.LoadingState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


/**
 * Firebase-backed implementation of [AuthRepositoryInterface].
 *
 * Responsibilities:
 * - Handles user authentication via FirebaseAuth
 * - Creates user entries in Firestore (top-level + nested company structure)
 * - Exposes authentication state as reactive StateFlows
 *
 * Exposed reactive states:
 * - authState: current authenticated Firebase user (null if logged out)
 * - errorMessage: latest authentication error
 * - loadingState: represents loading, success or error state
 *
 * This repository does NOT handle local preference logic.
 * Local flags like "hasRegistered" are handled by AuthPreferencesRepository.
 */
class AuthRepositoryFirebase(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepositoryInterface {
    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    override val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    //    private val _errorMessage = MutableStateFlow<String?>(null)
    //    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _errorEvents = MutableSharedFlow<AppError>()
    override val errorEvents: SharedFlow<AppError> = _errorEvents.asSharedFlow()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

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
            .addOnFailureListener { throwable ->

                repositoryScope.launch {
                    _errorEvents.emit(ErrorMapper.map(throwable))
                }

                _loadingState.value = LoadingState.Idle
            }
//            .addOnFailureListener {
//                _loadingState.value = LoadingState.Error(
//                    it.message ?: "Login fehlgeschlagen"
//                )
//            }
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
                val companyId = normalizeCompanyId(company)

                val newUser = User(
                    id = userId,
                    displayName = displayName,
                    email = email,
                    company = company,
                    companyId = companyId,
                    createdAt = System.currentTimeMillis(),
                    lastActiveAt = System.currentTimeMillis()
                )

                // Top-level user index
                val topLevelWrite = db.collection(CollectionNames.USERS.path)
                    .document(userId)
                    .set(newUser)

                // Nested company user
                val nestedWrite = db.collection(CollectionNames.COMPANIES.path)
                    .document(companyId)
                    .collection(CollectionNames.USERS.path)
                    .document(userId)
                    .set(newUser)

                Tasks.whenAllSuccess<Void>(topLevelWrite, nestedWrite)
                    .addOnSuccessListener {
                        _loadingState.value = LoadingState.Success()
                    }
                    .addOnFailureListener { throwable ->
                        repositoryScope.launch {
                            _errorEvents.emit(ErrorMapper.map(throwable))
                        }
                        _loadingState.value = LoadingState.Idle
                    }
//                    .addOnFailureListener {
//                        _loadingState.value =
//                            LoadingState.Error(it.message ?: "Fehler beim Speichern")
//                    }
            }
            .addOnFailureListener { throwable ->
                repositoryScope.launch {
                    _errorEvents.emit(ErrorMapper.map(throwable))
                }
                _loadingState.value = LoadingState.Idle
            }
//            .addOnFailureListener {
//                _loadingState.value = LoadingState.Error(
//                    it.message ?: "Registrierung fehlgeschlagen"
//                )
//            }
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