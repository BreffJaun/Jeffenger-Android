package com.example.jeffenger.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scheme = MaterialTheme.colorScheme

    val borderColor =
        when {
            !enabled -> scheme.outline.copy(alpha = 0.4f)
            checked -> scheme.secondary
            else -> scheme.outline
        }

    val checkColor =
        if (enabled) scheme.secondary
        else scheme.secondary.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(
                enabled = enabled
            ) {
                onCheckedChange(!checked)
            }
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
