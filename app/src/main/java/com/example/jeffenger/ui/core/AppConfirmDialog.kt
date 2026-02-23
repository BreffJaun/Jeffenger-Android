package com.example.jeffenger.ui.core

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String = "Abbrechen",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    LogComposable("AppConfirmDialog") {
        val scheme = MaterialTheme.colorScheme

        AlertDialog(
            onDismissRequest = onDismiss,

            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        text = confirmText,
                        color = if (isDestructive) scheme.error else scheme.primary
                    )
                }
            },

            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },

            title = { Text(title) },

            text = { Text(message) }
        )
    }
}