package com.example.jeffenger.ui.viewmodels

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    // DATASTORE & DATABASE
    private val dataStore = application.dataStore
//    private val database = TasksDatabase.getDatabase(application)

    // DAOs
//    private val taskDao = database.taskDao()
//    private val tagDao = database.tagDao()


    // DARK & LIGHTMODE
    val darkModeEnabled = dataStore.data
        .map { prefs ->
            prefs[DARK_MODE_ENABLED]
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null // null = System default
        )

    fun setDarkMode(value: Boolean?) {
        viewModelScope.launch {
            try {
                dataStore.edit { prefs ->
                    if (value == null) prefs.remove(DARK_MODE_ENABLED)
                    else prefs[DARK_MODE_ENABLED] = value
                }
            } catch (e: Exception) {
                _uiEvents.emit("Einstellung konnte nicht gespeichert werden")
            }
        }
    }
}