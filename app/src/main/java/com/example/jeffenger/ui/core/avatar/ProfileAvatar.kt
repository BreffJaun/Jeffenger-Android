package com.example.jeffenger.ui.core.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.utils.extensions.initials

@Composable
fun ProfileAvatar(
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(size)
            .background(
                color = scheme.tertiaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            displayName.isBlank() -> {
                Icon(
                    painter = painterResource(R.drawable.ic_person_outlined),
                    contentDescription = "Default Avatar",
                    tint = scheme.outline,
                    modifier = Modifier.size(size * 0.8f)
                )
            }

            else -> {
                Text(
                    text = displayName.initials(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = scheme.onTertiaryContainer
                )
            }
        }
    }
}