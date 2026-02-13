package com.example.jeffenger.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.ui.theme.AvatarInitialsTextStyle
import com.example.jeffenger.utils.enums.AvatarType

@Composable
fun AvatarCircle(
    avatar: AvatarUiModel,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val AvatarIconSize = 36.dp

    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(scheme.tertiaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when (avatar.type) {

            AvatarType.IMAGE -> {
                AsyncImage(
                    model = avatar.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            AvatarType.INITIALS -> {
                Text(
                    text = avatar.initials ?: "",
                    color = scheme.onTertiaryContainer,
                    style = AvatarInitialsTextStyle
                )
            }

            AvatarType.COMPANY_ICON -> {
                // SVG / XML from drawable
                androidx.compose.material3.Icon(
                    painter = painterResource(id = avatar.iconResId!!),
                    contentDescription = null,
                    tint = scheme.onTertiaryContainer,
                    modifier = Modifier.size(AvatarIconSize)
                )
            }

            AvatarType.GROUP_ICON -> {
                // Material 3 Icon
                androidx.compose.material3.Icon(
                    imageVector = avatar.iconVector!!,
                    contentDescription = null,
                    tint = scheme.onTertiaryContainer,
                    modifier = Modifier.size(AvatarIconSize)
                )
            }
        }
    }
}


//Text(
//text = fallbackText
//.trim()
//.split(" ")
//.take(2)
//.joinToString("") { it.first().uppercase() },
//
//color = scheme.onSurface,
//
//// Figma-konform
//style = TextStyle(
//fontFamily = Archivo,
//fontWeight = FontWeight.Normal, // Regular
//fontSize = 27.sp,
//lineHeight = 42.sp,
//letterSpacing = 0.sp
//)
//)