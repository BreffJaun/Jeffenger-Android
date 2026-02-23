package com.example.jeffenger.data.notifications

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.jeffenger.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object NotificationPrefs {

    private val KEY_ASKED = booleanPreferencesKey("notif_permission_asked")

    fun askedFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_ASKED] ?: false }

    suspend fun setAsked(context: Context, asked: Boolean) {
        context.dataStore.edit { it[KEY_ASKED] = asked }
    }
}