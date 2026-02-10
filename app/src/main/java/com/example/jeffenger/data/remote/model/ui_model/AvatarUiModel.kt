package com.example.jeffenger.data.remote.model.ui_model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jeffenger.utils.enums.AvatarType

data class AvatarUiModel(
    val type: AvatarType,
    val imageUrl: String? = null,
    val initials: String? = null,
    val iconResId: Int? = null,              // for R.drawable
    val iconVector: ImageVector? = null      // for Icons.Rounded
)