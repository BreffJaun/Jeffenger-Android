package com.example.jeffenger.ui.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.jeffenger.ui.theme.UrbanistText

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = UrbanistText.Label,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}