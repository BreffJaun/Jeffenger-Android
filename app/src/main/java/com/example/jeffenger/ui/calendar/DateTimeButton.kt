package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun DateTimeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LogComposable("DateTimeButton") {
        val scheme = MaterialTheme.colorScheme

        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(42.dp),
            border = BorderStroke(2.dp, scheme.onSurfaceVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = scheme.onSurface
            ),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {

            Text(
                text = text,
                color = scheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}