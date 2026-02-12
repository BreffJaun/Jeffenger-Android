package com.example.jeffenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.jeffenger.data.repository.AuthRepositoryFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val authRepository: AuthRepositoryFirebase
): ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    val authState = authRepository.authState
    val errorMessage = authRepository.errorMessage

    fun loginWithEmailAndPassword() {
        authRepository.loginWithEmailAndPassword(email.value, password.value)
    }

    fun registerWithEmailAndPassword() {
        authRepository.registerWithEmailAndPassword(email.value, password.value)
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
}
