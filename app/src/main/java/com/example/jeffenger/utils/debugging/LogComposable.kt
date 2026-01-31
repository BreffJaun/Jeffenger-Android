package com.example.jeffenger.utils.debugging

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Utility composable for logging when a composable is loaded.
 *
 * Responsibilities:
 * - Logs a debug message indicating that the wrapped composable has been composed
 *
 * Behaviour:
 * - Executes logging once when the composable enters the composition
 * - Does not modify or interfere with the wrapped content
 *
 * @param name Name identifier for the composable, used in the log
 * @param content The composable content to be rendered
 */
@Composable
fun LogComposable(
    name: String,
    content: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        Log.d("ComposableLogging", "$name loaded")
    }
    content()
}

