package com.example.jeffenger.data.remote.model

import com.example.jeffenger.utils.enums.EventStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CalendarEvent(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val createdByUserId: String = "",
    val participantIds: List<String> = emptyList(),

    // LATER FOR GOOGLE INTEGRATION
    val participantEmails: List<String> = emptyList(),
    val status: EventStatus = EventStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now()
)
