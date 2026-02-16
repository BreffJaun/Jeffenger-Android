package com.example.jeffenger.data.local.database

import androidx.datastore.preferences.core.booleanPreferencesKey

object AuthPreferences {
    val HAS_REGISTERED = booleanPreferencesKey("has_registered")
}