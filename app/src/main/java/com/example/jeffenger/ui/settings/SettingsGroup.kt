package com.example.jeffenger.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Small helper: group card-ish section */
@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
//        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = scheme.onSurfaceVariant
        )
        content()
    }
}