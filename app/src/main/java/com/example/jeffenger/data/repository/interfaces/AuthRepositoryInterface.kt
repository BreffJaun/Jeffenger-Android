package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.error.AppError
import com.example.jeffenger.utils.state.LoadingState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


/**
 * Defines authentication contract for the application.
 *
 * Exposes:
 * - authState: current Firebase authentication state
 * - errorMessage: latest authentication error
 * - loadingState: current loading status (idle, loading, success, error)
 *
 * Implementation is backed by Firebase Authentication.
 */
interface AuthRepositoryInterface {

    /**
     * Emits the currently authenticated FirebaseUser.
     * Null means no user is logged in.
     */
    // LOGGED IN -> FirebaseUser != null
    // LOGGED OUT -> FirebaseUser == null
    val authState: StateFlow<FirebaseUser?>

    //    val errorMessage: StateFlow<String?>
    val errorEvents: SharedFlow<AppError>

    val loadingState: StateFlow<LoadingState>

    fun loginWithEmailAndPassword(email: String, password: String)

    fun registerWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String,
        company: String
    )

    fun logout()

    suspend fun updateEmail(
        currentPassword: String,
        newEmail: String
    )

    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    )
}

