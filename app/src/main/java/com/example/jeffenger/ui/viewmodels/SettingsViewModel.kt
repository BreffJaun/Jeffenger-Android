package com.example.jeffenger.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.data.repository.interfaces.AuthRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.StorageRepositoryInterface
import com.example.jeffenger.data.repository.interfaces.UserRepositoryInterface
import com.example.jeffenger.dataStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.Task
import java.util.UUID
import kotlin.collections.get

// DATASTORE KEYS
private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")

class SettingsViewModel(
    private val userRepository: UserRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _company = MutableStateFlow("")
    val company = _company.asStateFlow()

    private val _tempAvatarUri = MutableStateFlow<Uri?>(null)
    val tempAvatarUri = _tempAvatarUri.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
    }

    val user = userRepository.appUser
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    init {
        viewModelScope.launch {
            user.collect { u ->
                u ?: return@collect
                _displayName.value = u.displayName
                _email.value = u.email
                _company.value = u.company
            }
        }
    }

    val hasChanges =
        combine(user, displayName, email, company) { user, name, mail, comp ->

            if (user == null) return@combine false

            name != user.displayName ||
                    mail != user.email ||
                    comp != user.company

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    fun saveProfile() {
        viewModelScope.launch {
            val user = userRepository.appUser.value ?: return@launch

            if (displayName.value.isBlank()) {
                _uiEvents.emit("Name darf nicht leer sein")
                return@launch
            }

            val nameChanged = displayName.value != user.displayName
            val companyChanged = company.value != user.company
            val emailChanged = email.value != user.email

            if (!nameChanged && !companyChanged && !emailChanged) {
                _uiEvents.emit("Keine Änderungen vorhanden")
                return@launch
            }

            try {
                if (nameChanged || companyChanged) {
                    userRepository.updateUserProfile(
                        userId = user.id,
                        displayName = displayName.value.trim(),
                        company = company.value.trim()
                    )
                }

                if (emailChanged) {
                    _uiEvents.emit(
                        "E-Mail Änderung bitte über Passwort bestätigen"
                    )
                }

                _uiEvents.emit("Profil wurde gespeichert")

            } catch (e: Exception) {
                _uiEvents.emit("Profil konnte nicht gespeichert werden")
            }
        }
    }

    fun onDisplayNameChange(value: String) {
        _displayName.value = value
    }

    fun onCompanyChange(value: String) {
        _company.value = value
    }

    fun emitUi(message: String) {
        viewModelScope.launch { _uiEvents.emit(message) }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            val user = userRepository.appUser.value ?: return@launch
            _tempAvatarUri.value = uri

            try {
                val url = storageRepository.uploadUserAvatar(
                    uri,
                    user.id
                )

                userRepository.updateAvatar(user.id, url)
                _tempAvatarUri.value = null
                _uiEvents.emit("Profilbild aktualisiert")
            } catch (e: Exception) {
                _tempAvatarUri.value = null
                _uiEvents.emit("Upload fehlgeschlagen")
            }
        }
    }

    fun changeEmail(
        currentPassword: String,
        newEmail: String
    ) {
        viewModelScope.launch {
            val user = userRepository.appUser.value ?: return@launch

            try {

                authRepository.updateEmail(
                    currentPassword,
                    newEmail
                )

                userRepository.updateEmail(
                    user.id,
                    newEmail
                )

                _uiEvents.emit("E-Mail geändert – bitte neu anmelden")
                authRepository.logout()
            } catch (e: Exception) {
                _uiEvents.emit("E-Mail konnte nicht geändert werden")
            }
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            if (newPassword.length < 6) {
                _uiEvents.emit("Passwort muss mindestens 6 Zeichen lang sein")
                return@launch
            }

            try {
                authRepository.updatePassword(
                    currentPassword,
                    newPassword
                )

                _uiEvents.emit("Passwort wurde geändert")
                authRepository.logout()
            } catch (e: Exception) {
                _uiEvents.emit("Passwort konnte nicht geändert werden")
            }
        }
    }

    fun deleteAvatar() {
        viewModelScope.launch {
            val user = userRepository.appUser.value ?: return@launch

            try {
                storageRepository.deleteAvatar(user.id)
                userRepository.updateAvatar(
                    user.id,
                    ""
                )

                _uiEvents.emit("Profilbild gelöscht")
            } catch (e: Exception) {
                _uiEvents.emit("Avatar konnte nicht gelöscht werden")
            }
        }
    }
}


