package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.state.LoadingState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepositoryInterface {

    val authState: StateFlow<FirebaseUser?>
    val errorMessage: StateFlow<String?>
    val loadingState: StateFlow<LoadingState>

    fun loginWithEmailAndPassword(email: String, password: String)
    fun registerWithEmailAndPassword(email: String, password: String, displayName: String, company: String)
    fun logout()
}

