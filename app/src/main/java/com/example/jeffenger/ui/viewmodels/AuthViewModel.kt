package com.example.jeffenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.repository.AuthPreferencesRepository
import com.example.jeffenger.utils.enums.AuthMode
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling authentication UI state and logic.
 *
 * Responsibilities:
 * - Holds form state (email, password, name, company)
 * - Performs validation logic
 * - Delegates authentication to AuthRepository
 * - Stores UX preference via AuthPreferencesRepository
 *
 * Does NOT directly interact with Firebase.
 * Keeps UI reactive via StateFlow.
 */
class AuthViewModel(
    private val authRepository: AuthRepositoryInterface,
    private val authPreferencesRepository: AuthPreferencesRepository
) : ViewModel() {

    private val _authMode = MutableStateFlow(AuthMode.REGISTER)
    val authMode = _authMode.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.asStateFlow()

    private val _company = MutableStateFlow("")
    val company = _company.asStateFlow()

    val isEmailValid = email.map { input ->
        Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    val isPasswordNotEmpty = password.map { it.isNotBlank() }

    val hasMinLength = password.map { it.length >= 6 }
    val hasUppercase = password.map { it.any { char -> char.isUpperCase() } }
    val hasLowercase = password.map { it.any { char -> char.isLowerCase() } }
    val hasDigit = password.map { it.any { char -> char.isDigit() } }
    val hasSpecialChar = password.map {
        it.any { char -> !char.isLetterOrDigit() }
    }

    val isPasswordValid = combine(
        hasMinLength,
        hasUppercase,
        hasLowercase,
        hasDigit,
        hasSpecialChar
    ) { length, upper, lower, digit, special ->
        length && upper && lower && digit && special
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false
    )

    val isDisplayNameValid = displayName.map { it.isNotBlank() }
    val isCompanyValid = company.map { it.isNotBlank() }

    val isFormValid = combine(
        isEmailValid,
        isPasswordNotEmpty,
        isPasswordValid,
        isDisplayNameValid,
        isCompanyValid
    ) { emailValid, passwordNotEmpty, passwordValid, nameValid, companyValid ->

        Triple(
            emailValid,
            passwordNotEmpty,
            passwordValid && nameValid && companyValid
        )
    }.combine(authMode) { base, mode ->

        val (emailValid, passwordNotEmpty, registerValid) = base

        when (mode) {
            AuthMode.REGISTER -> emailValid && registerValid
            AuthMode.LOGIN -> emailValid && passwordNotEmpty
        }

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false
    )

    val authState = authRepository.authState

    //    val errorMessage = authRepository.errorMessage
    val errorEvents = authRepository.errorEvents
    val loadingState = authRepository.loadingState

    private var lastAuthAction: (() -> Unit)? = null

    init {
        // SAFE IF IT WAS REGISTERED MINIMUM 1X TIME
        viewModelScope.launch {
            authPreferencesRepository.hasRegistered.collect { registered ->
                if (registered) {
                    _authMode.value = AuthMode.LOGIN
                }
            }
        }
    }

    fun loginWithEmailAndPassword() {
        lastAuthAction = { loginWithEmailAndPassword() }

        authRepository.loginWithEmailAndPassword(email.value, password.value)
        clearForm()
    }

    fun registerWithEmailAndPassword() {
        lastAuthAction = { registerWithEmailAndPassword() }

        authRepository.registerWithEmailAndPassword(
            email.value,
            password.value,
            displayName.value,
            company.value
        )

        // SAFE IF IT WAS REGISTERED MINIMUM 1X TIME
        viewModelScope.launch {
            authPreferencesRepository.setHasRegistered(true)
        }
        clearForm()
    }

    fun logout() {
        Log.d("AuthViewModel", "Logout called")
        authRepository.logout()
        clearForm()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onDisplayNameChange(newDisplayName: String) {
        _displayName.value = newDisplayName
    }

    fun onCompanyChange(newCompany: String) {
        _company.value = newCompany
    }

    fun clearForm() {
        _email.value = ""
        _password.value = ""
        _displayName.value = ""
        _company.value = ""
    }

    // FOR POSSIBLE ERROR
    fun retryLastAction() {
        lastAuthAction?.invoke()
    }

    // REGISTER OR LOGIN
    fun setAuthMode(mode: AuthMode) {
        _authMode.value = mode
    }

    //
    fun submit() {
        when (_authMode.value) {
            AuthMode.REGISTER -> registerWithEmailAndPassword()
            AuthMode.LOGIN -> loginWithEmailAndPassword()
        }
    }
}
