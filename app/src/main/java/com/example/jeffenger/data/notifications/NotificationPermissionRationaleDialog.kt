package com.example.jeffenger.data.notifications

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.jeffenger.utils.helper.openAppSettings

@Composable
fun NotificationPermissionRationaleDialog(
    context: Context,
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit
) {
    AlertDialog(
        title = { Text("Benachrichtigungen deaktiviert") },
        text = {
            Text("Damit du neue Nachrichten sofort siehst, braucht Jeffenger die Benachrichtigungs-Erlaubnis.")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onTryAgain()
                onDismiss()
            }) { Text("Erlauben") }
        },
        dismissButton = {
            Button(onClick = {
                openAppSettings(context)
                onDismiss()
            }) { Text("Einstellungen") }
        }
    )
}