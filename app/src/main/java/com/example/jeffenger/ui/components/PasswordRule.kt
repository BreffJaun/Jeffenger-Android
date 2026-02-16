package com.example.jeffenger.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.example.jeffenger.ui.theme.UrbanistText

@Composable
fun PasswordRule(
    text: String,
    isValid: Boolean
) {
    val scheme = MaterialTheme.colorScheme

    Text(
        text = text,
        style = UrbanistText.Label,
        color = if (isValid) scheme.primary else scheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}