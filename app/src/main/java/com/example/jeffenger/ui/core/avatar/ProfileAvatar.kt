package com.example.jeffenger.ui.core.avatar

import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.jeffenger.R
import com.example.jeffenger.utils.extensions.initials

@Composable
fun ProfileAvatar(
    displayName: String? = null,
    imageUrl: String? = null,
    imageUri: Uri? = null,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {

    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                color = scheme.tertiaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {

        when {

            // Firebase Avatar
            !imageUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(size),
                    contentScale = ContentScale.Crop
                )
            }

            // Locale Avatar (snappy preview)
            imageUri != null -> {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Avatar Preview",
                    modifier = Modifier.size(size),
                    contentScale = ContentScale.Crop
                )
            }

            // Initials
            !displayName.isNullOrBlank() -> {
                Text(
                    text = displayName.initials(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = scheme.onTertiaryContainer
                )
            }

            // Default Icon
            else -> {
                Icon(
                    painter = painterResource(R.drawable.ic_person_outlined),
                    contentDescription = "Default Avatar",
                    tint = scheme.outline,
                    modifier = Modifier.size(size * 0.8f)
                )
            }
        }
    }
}
