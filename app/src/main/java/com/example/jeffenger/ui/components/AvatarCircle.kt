package com.example.jeffenger.ui.components

import android.R.attr.scheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.jeffenger.utils.Extensions.initials
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun AvatarCircle(
    imageUrl: String?,
    fallbackText: String,
    modifier: Modifier = Modifier
) {
    LogComposable("AvatarCircle") {
        val scheme = MaterialTheme.colorScheme

        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(scheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = fallbackText.initials(),
                    color = scheme.onTertiaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}