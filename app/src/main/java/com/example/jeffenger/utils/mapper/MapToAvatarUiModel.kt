package com.example.jeffenger.utils.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import com.example.jeffenger.R
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.utils.extensions.initials
import com.example.jeffenger.utils.enums.AvatarType
import kotlin.collections.map

fun mapToAvatarUiModel(
    chat: Chat,
    currentUserId: String,
    users: List<User>
): AvatarUiModel {

    // 1. Image has top priority
    if (chat.imageUrl != null) {
        return AvatarUiModel(
            type = AvatarType.IMAGE,
            imageUrl = chat.imageUrl
        )
    }

    val participantUsers =
        users.filter { chat.participantIds.contains(it.id) }

    val isUserPartOfChat =
        participantUsers.any { it.id == currentUserId }

    // 2. Single Chat → Initials of the other person
    if (!chat.groupChat) {
        val otherUser = participantUsers
            .firstOrNull { it.id != currentUserId }
        return AvatarUiModel(
            type = AvatarType.INITIALS,
            initials = otherUser?.displayName?.initials() ?: "?"
        )
    }

    // 3. Group chat without picture
    val isCompanyInternal =
        participantUsers
            .map { it.companyId }
            .filter { it.isNotBlank() }
            .distinct()
            .size == 1

    return if (!isUserPartOfChat && isCompanyInternal) {
        AvatarUiModel(
            type = AvatarType.COMPANY_ICON,
            iconResId = R.drawable.ic_family_group
        )
    } else {
        AvatarUiModel(
            type = AvatarType.GROUP_ICON,
            iconVector = Icons.Rounded.Groups
        )
    }
}