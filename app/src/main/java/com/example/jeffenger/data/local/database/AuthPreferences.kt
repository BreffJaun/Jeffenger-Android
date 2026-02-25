package com.example.jeffenger.data.local.database

import androidx.datastore.preferences.core.booleanPreferencesKey

/**
 * Contains keys for DataStore preferences related to authentication.
 *
 * HAS_REGISTERED:
 * Stores whether the user has successfully registered at least once
 * on this device. Used to determine the initial AuthMode (Register/Login).
 */
object AuthPreferences {
    // KEY OF TYPE BOOLEAN WITH THE NAME -> "has_registered"
    val HAS_REGISTERED = booleanPreferencesKey("has_registered")
}

