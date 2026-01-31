package com.example.jeffenger.utils.debugging

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Utility composable for logging state changes within a composable.
 *
 * Responsibilities:
 * - Logs key-value pairs whenever one of the provided states changes
 *
 * Behaviour:
 * - Uses `LaunchedEffect` keyed to state values to detect changes
 * - Outputs debug messages with the given tag
 * - Stateless regarding app logic; purely for development/debugging
 *
 * @param tag Log tag used for filtering
 * @param states Vararg of state name and value pairs to monitor
 */
@Composable
fun LogStateMap(
    tag: String,
    vararg states: Pair<String, Any?>
) {
    // Only take the values as keys for LaunchedEffect
    LaunchedEffect(*states.map { it.second }.toTypedArray()) {
        states.forEach { (name, value) ->
            Log.d(tag, "$name changed → $value")
        }
    }
}

