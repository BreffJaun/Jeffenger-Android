package com.example.jeffenger.ui.chat

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.core.AppTextField

@Composable
fun AppEditMessageDialog(
    text: String,
    onTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Speichern")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        },

        title = { Text("Nachricht bearbeiten") },

        text = {
            AppTextField(
                value = text,
                placeholder = "",
                onValueChange = onTextChange,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 42.dp, max = 120.dp)
            )
        }
    )
}