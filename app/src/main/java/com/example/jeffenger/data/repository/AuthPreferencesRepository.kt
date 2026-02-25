package com.example.jeffenger.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.jeffenger.data.local.database.AuthPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository responsible for local authentication-related preferences.
 *
 * Uses DataStore to persist simple flags on the device.
 *
 * Currently stores:
 * - hasRegistered: indicates if a user has completed registration at least once.
 *
 * This is NOT related to Firebase authentication state.
 * It is only used to improve UX (switch between Register/Login on startup).
 */
class AuthPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Emits true if the user has registered at least once on this device.
     */
    val hasRegistered: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[AuthPreferences.HAS_REGISTERED] ?: false
        }

    /**
     * Persists the registration flag in DataStore.
     */
    suspend fun setHasRegistered(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[AuthPreferences.HAS_REGISTERED] = value
        }
    }
}