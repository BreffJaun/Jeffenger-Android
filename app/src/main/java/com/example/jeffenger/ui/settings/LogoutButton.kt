package com.example.jeffenger.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.core.ButtonContent

@Composable
fun LogoutButton(
    text: String = "Logout",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        border = BorderStroke(2.dp, scheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = scheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {

        Text(
            text = text,
            color = scheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}