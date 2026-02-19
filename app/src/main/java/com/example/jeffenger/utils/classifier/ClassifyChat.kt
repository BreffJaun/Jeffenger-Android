package com.example.jeffenger.utils.classifier

import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.data.remote.model.User
import com.example.jeffenger.utils.enums.ChatType

object ChatClassifier {

    fun classify(
        chat: Chat,
        currentUserId: String,
        jeffId: String,
        users: List<User>
    ): ChatType {

        val participantIds = chat.participantIds

        val isDirectJeff =
            !chat.groupChat &&
                    participantIds.contains(currentUserId) &&
                    participantIds.contains(jeffId) &&
                    participantIds.size == 2

        if (isDirectJeff) return ChatType.DIRECT_JEFF

        val participantUsers =
            users.filter { participantIds.contains(it.id) }

        val isCompanyOnly =
            !participantIds.contains(jeffId) &&
                    participantUsers.map { it.companyId }.distinct().size == 1 &&
                    participantIds.contains(currentUserId)

        if (isCompanyOnly) return ChatType.COMPANY_ONLY

        val isCompanyWithJeff =
            participantIds.contains(jeffId) &&
                    participantIds.contains(currentUserId) &&
                    participantUsers
                        .filter { it.id != jeffId }
                        .map { it.companyId }
                        .distinct()
                        .size == 1

        if (isCompanyWithJeff) return ChatType.COMPANY_WITH_JEFF

        return ChatType.OTHER
    }
}