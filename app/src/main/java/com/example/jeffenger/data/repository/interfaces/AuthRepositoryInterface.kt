package com.example.jeffenger.data.repository.interfaces

import com.example.jeffenger.data.remote.model.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepositoryInterface {

    val authState: StateFlow<FirebaseUser?>
    val appUser: StateFlow<User?>
    val errorMessage: StateFlow<String?>

    fun loginWithEmailAndPassword(email: String, password: String)
    fun registerWithEmailAndPassword(email: String, password: String)
    fun logout()
}